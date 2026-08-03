package io.github.son1004007.opsmate.agent;

import java.math.BigDecimal;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import io.github.son1004007.opsmate.domain.PurchaseCategory;
import org.springframework.stereotype.Component;

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
