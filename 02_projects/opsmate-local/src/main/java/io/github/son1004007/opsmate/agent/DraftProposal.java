package io.github.son1004007.opsmate.agent;

import java.math.BigDecimal;
import java.util.List;

import io.github.son1004007.opsmate.domain.PurchaseCategory;

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
