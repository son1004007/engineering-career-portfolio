package io.github.son1004007.opsmate.agent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.github.son1004007.opsmate.domain.PurchaseCategory;
import org.springframework.stereotype.Component;

/**
 * 실제 회사 정책 대신 공개 데모 전용 합성 정책 근거를 keyword로 조회한다.
 *
 * <p>검색 결과는 모델이 임의로 만든 정책이 아니라 서버가 허용한 ID·분류·금액 한도다.
 * catalog를 바꿀 때는 모델 prompt뿐 아니라 경계값·잘못된 policy ID·한도 초과 검증도
 * 함께 확인해야 한다.
 */
@Component
public class SyntheticPolicyEvidenceTool implements PolicyEvidenceTool {

    private static final List<PolicyEntry> CATALOG = List.of(
            new PolicyEntry(
                    new PolicyEvidence(
                            "POL-IT-001",
                            "IT 장비 구매 기준",
                            "업무용 IT 장비는 5,000,000원 이하이며 승인 후 발주한다.",
                            new BigDecimal("5000000"),
                            Set.of(PurchaseCategory.IT_EQUIPMENT)),
                    Set.of("노트북", "컴퓨터", "모니터", "laptop", "computer", "monitor")),
            new PolicyEntry(
                    new PolicyEvidence(
                            "POL-SW-001",
                            "소프트웨어 구독 기준",
                            "업무용 소프트웨어 구독은 10,000,000원 이하이며 사용 목적을 기록한다.",
                            new BigDecimal("10000000"),
                            Set.of(PurchaseCategory.SOFTWARE)),
                    Set.of("소프트웨어", "라이선스", "구독", "software", "license", "subscription")),
            new PolicyEntry(
                    new PolicyEvidence(
                            "POL-OFFICE-001",
                            "사무용품 구매 기준",
                            "사무용품은 1,000,000원 이하이며 부서 사용 목적을 기록한다.",
                            new BigDecimal("1000000"),
                            Set.of(PurchaseCategory.OFFICE_SUPPLIES)),
                    Set.of("사무용품", "의자", "책상", "office", "chair", "desk")));

    @Override
    public List<PolicyEvidence> search(PolicySearchQuery query) {
        if (query == null || query.requestText() == null || query.requestText().isBlank()) {
            return List.of();
        }
        String normalized = query.requestText().toLowerCase(Locale.ROOT);
        return CATALOG.stream()
                .filter(entry -> entry.keywords().stream().anyMatch(normalized::contains))
                .map(PolicyEntry::evidence)
                .toList();
    }

    private record PolicyEntry(PolicyEvidence evidence, Set<String> keywords) {
    }
}
