package io.github.son1004007.opsmate.application;

import io.github.son1004007.opsmate.domain.PurchaseOrder;

public interface OrderPostPersistHook {

    void afterPersist(PurchaseOrder order);
}
