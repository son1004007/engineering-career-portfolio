package io.github.son1004007.opsmate.api;

import java.util.List;

import io.github.son1004007.opsmate.api.ApiModels.CreateOrderRequest;
import io.github.son1004007.opsmate.api.ApiModels.PurchaseOrderResponse;
import io.github.son1004007.opsmate.application.PurchaseOrderService;
import io.github.son1004007.opsmate.application.PurchaseOrderQueryService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchase-orders")
public class PurchaseOrderController {

    private final PurchaseOrderService service;
    private final PurchaseOrderQueryService queryService;

    public PurchaseOrderController(PurchaseOrderService service, PurchaseOrderQueryService queryService) {
        this.service = service;
        this.queryService = queryService;
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    PurchaseOrderResponse createOrder(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateOrderRequest request) {
        return PurchaseOrderResponse.from(service.createOrder(request.purchaseRequestId(), idempotencyKey));
    }

    @GetMapping
    List<PurchaseOrderResponse> findVisible() {
        return queryService.findVisible().stream().map(PurchaseOrderResponse::from).toList();
    }
}
