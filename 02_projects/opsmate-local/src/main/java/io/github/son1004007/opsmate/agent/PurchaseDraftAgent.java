package io.github.son1004007.opsmate.agent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import io.github.son1004007.opsmate.domain.ErrorCode;
import io.github.son1004007.opsmate.domain.OpsMateException;
import org.springframework.stereotype.Service;

@Service
public class PurchaseDraftAgent {

    private final PolicyEvidenceTool policyTool;
    private final LocalOpenWeightLlmGateway llmGateway;

    public PurchaseDraftAgent(PolicyEvidenceTool policyTool, LocalOpenWeightLlmGateway llmGateway) {
        this.policyTool = policyTool;
        this.llmGateway = llmGateway;
    }

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
