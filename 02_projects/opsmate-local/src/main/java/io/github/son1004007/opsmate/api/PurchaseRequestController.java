package io.github.son1004007.opsmate.api;

import java.util.UUID;

import io.github.son1004007.opsmate.api.ApiModels.CreateDraftRequest;
import io.github.son1004007.opsmate.api.ApiModels.DecisionRequest;
import io.github.son1004007.opsmate.api.ApiModels.PurchaseRequestResponse;
import io.github.son1004007.opsmate.application.PurchaseRequestService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/purchase-requests")
public class PurchaseRequestController {

    private final PurchaseRequestService service;

    public PurchaseRequestController(PurchaseRequestService service) {
        this.service = service;
    }

    @PostMapping("/drafts")
    @ResponseStatus(HttpStatus.CREATED)
    PurchaseRequestResponse createDraft(
            @RequestHeader("Idempotency-Key") String idempotencyKey,
            @Valid @RequestBody CreateDraftRequest request) {
        return PurchaseRequestResponse.from(service.createDraft(idempotencyKey, request.requestText()));
    }

    @PostMapping("/{requestId}/submit")
    PurchaseRequestResponse submit(@PathVariable UUID requestId) {
        return PurchaseRequestResponse.from(service.submit(requestId));
    }

    @PostMapping("/{requestId}/decisions")
    PurchaseRequestResponse decide(
            @PathVariable UUID requestId,
            @Valid @RequestBody DecisionRequest request) {
        return PurchaseRequestResponse.from(service.decide(requestId, request.decision(), request.reason()));
    }

    @GetMapping("/{requestId}")
    PurchaseRequestResponse get(@PathVariable UUID requestId) {
        return PurchaseRequestResponse.from(service.get(requestId));
    }
}
