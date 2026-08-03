package io.github.son1004007.opsmate.api;

import java.util.List;

import io.github.son1004007.opsmate.api.ApiModels.AuditEventResponse;
import io.github.son1004007.opsmate.application.AuditQueryService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/audit-events")
public class AuditEventController {

    private final AuditQueryService service;

    public AuditEventController(AuditQueryService service) {
        this.service = service;
    }

    @GetMapping
    List<AuditEventResponse> findAll() {
        return service.findAll().stream().map(AuditEventResponse::from).toList();
    }
}
