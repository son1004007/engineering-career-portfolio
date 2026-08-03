package io.github.son1004007.opsmate.api;

import io.github.son1004007.opsmate.api.ApiModels.CreateOrderRequest;
import io.github.son1004007.opsmate.api.ApiModels.PurchaseOrderResponse;
import io.github.son1004007.opsmate.application.PurchaseOrderService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService service;

    public PurchaseOrderController(PurchaseOrderService service) {
        this.service = service;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    PurchaseOrderResponse createOrder(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request) {
        return PurchaseOrderResponse.from(service.createOrder(request.purchaseRequestId(), idempotencyKey));
    }
}
