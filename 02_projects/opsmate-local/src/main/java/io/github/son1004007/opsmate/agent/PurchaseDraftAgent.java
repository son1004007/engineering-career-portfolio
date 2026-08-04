package io.github.son1004007.opsmate.agent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import org.springframework.stereotype.Service;

/**
 * 신뢰할 수 없는 자연어를 정책 근거가 연결된 구매 초안 제안으로 변환한다.
 *
 * <p><strong>신뢰 경계:</strong> 서버가 정책을 먼저 조회하고 모델에는 요청 원문과
 * 그 결과만 전달한다. 모델이 반환한 policy ID, 분류와 금액은 서버가 다시 검증하며
 * 통과 전에는 JPA entity나 감사 이벤트를 만들지 않는다.
 *
 * <p><strong>수정 시 주의:</strong> 출력 schema나 정책 한도를 바꾸면 adapter의
 * strict JSON 계약, 실패 시 DB 무변경 테스트와 아키텍처 문서를 함께 갱신한다.
 */
@Service
public class PurchaseDraftAgent {

    private final PolicyEvidenceTool policyTool;
    private final LocalOpenWeightLlmGateway llmGateway;

    public PurchaseDraftAgent(PolicyEvidenceTool policyTool, LocalOpenWeightLlmGateway llmGateway) {
        this.policyTool = policyTool;
        this.llmGateway = llmGateway;
    }

    /** 모델을 호출하기 전에 정책 근거를 확정하고, 반환 제안을 서버 규칙으로 재검증한다. */
    public DraftAgentResult createDraft(String requestText) {
        List<PolicyEvidence> evidence = policyTool.search(new PolicySearchQuery(requestText));
        if (evidence.isEmpty()) {
            throw new OpsMateException(ErrorCode.POLICY_NOT_FOUND, "No approved policy evidence matched the request");
        }

        DraftProposal proposal = llmGateway.propose(new DraftPrompt(requestText, evidence));
        validate(proposal, evidence);
        return new DraftAgentResult(proposal, evidence);
    }

    private void validate(DraftProposal proposal, List<PolicyEvidence> evidence) {
        if (proposal == null
                || isBlank(proposal.title())
                || proposal.title().length() > 200
                || isBlank(proposal.purpose())
                || proposal.purpose().length() > 1000
                || proposal.amount() == null
                || proposal.amount().compareTo(BigDecimal.ZERO) <= 0
                || proposal.amount().scale() > 2
                || !"KRW".equals(proposal.currency())
                || proposal.category() == null
                || proposal.policyIds().isEmpty()) {
            throw invalidOutput("Model output failed structural validation");
        }

        Set<String> evidenceIds = evidence.stream().map(PolicyEvidence::id).collect(Collectors.toSet());
        if (!evidenceIds.containsAll(proposal.policyIds())) {
            throw invalidOutput("Model cited policy evidence that was not retrieved");
        }

        List<PolicyEvidence> citedPolicies = evidence.stream()
                .filter(item -> proposal.policyIds().contains(item.id()))
                .filter(item -> item.categories().contains(proposal.category()))
                .toList();
        if (citedPolicies.isEmpty()) {
            throw invalidOutput("Cited policy evidence does not cover the proposed category");
        }

        BigDecimal maximumAllowed = citedPolicies.stream()
                .map(PolicyEvidence::maximumAmount)
                .max(BigDecimal::compareTo)
                .orElse(BigDecimal.ZERO);
        if (proposal.amount().compareTo(maximumAllowed) > 0) {
            throw invalidOutput("Proposed amount exceeds the cited policy limit");
        }
    }

    private boolean isBlank(String value) {
        return value == null || value.isBlank();
    }

    private OpsMateException invalidOutput(String message) {
        return new OpsMateException(ErrorCode.INVALID_MODEL_OUTPUT, message);
    }
}
