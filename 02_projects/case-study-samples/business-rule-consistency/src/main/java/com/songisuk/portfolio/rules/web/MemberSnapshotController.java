package com.songisuk.portfolio.rules.web;

import com.songisuk.portfolio.rules.domain.MemberSnapshot;
import com.songisuk.portfolio.rules.service.MemberSnapshotService;
import com.songisuk.portfolio.rules.service.SnapshotPolicy;
import jakarta.servlet.http.HttpSession;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/portal")
public class MemberSnapshotController {
    private final SessionSubjectResolver subjectResolver;
    private final MemberSnapshotService service;

    public MemberSnapshotController(SessionSubjectResolver subjectResolver, MemberSnapshotService service) {
        this.subjectResolver = subjectResolver;
        this.service = service;
    }

    @GetMapping("/current")
    public MemberSnapshot current(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpSession session
    ) {
        String subjectId = subjectResolver.resolve(session);
        return service.load(subjectId, SnapshotPolicy.LATEST_ONLY, year, month);
    }

    @GetMapping("/snapshot")
    public MemberSnapshot snapshot(
            @RequestParam(required = false) Integer year,
            @RequestParam(required = false) Integer month,
            HttpSession session
    ) {
        String subjectId = subjectResolver.resolve(session);
        return service.load(subjectId, SnapshotPolicy.EXPLICIT_OR_LATEST, year, month);
    }
}
