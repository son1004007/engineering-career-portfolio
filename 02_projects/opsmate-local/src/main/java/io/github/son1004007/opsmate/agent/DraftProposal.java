package io.github.son1004007.opsmate.agent;

import java.math.BigDecimal;
import java.util.List;

import io.github.son1004007.opsmate.domain.PurchaseCategory;

/** 모델이 제안한 값이며 {@link PurchaseDraftAgent}의 서버 검증 전에는 저장할 수 없는 초안 DTO. */
public record DraftProposal(
        String title,
        String purpose,
        BigDecimal amount,
        String currency,
        PurchaseCategory category,
        List<String> policyIds) {

    public DraftProposal {
        policyIds = policyIds == null ? List.of() : List.copyOf(policyIds);
    }
}
