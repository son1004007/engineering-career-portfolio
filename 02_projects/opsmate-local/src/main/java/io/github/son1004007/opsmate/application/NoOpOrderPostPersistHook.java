package io.github.son1004007.opsmate.application;

import io.github.son1004007.opsmate.domain.PurchaseOrder;
import org.springframework.stereotype.Component;

@Component
public class NoOpOrderPostPersistHook implements OrderPostPersistHook {

    @Override
    public void afterPersist(PurchaseOrder order) {
        // Extension point for an ERP/outbox adapter. The portfolio slice performs no external write.
    }
}
