package io.github.son1004007.opsmate.agent;

import java.math.BigDecimal;
import java.util.Set;

import io.github.son1004007.opsmate.domain.PurchaseCategory;

public record PolicyEvidence(
        String id,
        String title,
        String summary,
        BigDecimal maximumAmount,
        Set<PurchaseCategory> categories) {

    public PolicyEvidence {
        categories = Set.copyOf(categories);
    }
}
