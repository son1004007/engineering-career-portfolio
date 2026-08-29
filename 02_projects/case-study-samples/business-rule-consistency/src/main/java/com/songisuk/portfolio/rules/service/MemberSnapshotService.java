package com.songisuk.portfolio.rules.service;

import com.songisuk.portfolio.rules.domain.MemberSnapshot;
import com.songisuk.portfolio.rules.domain.SnapshotKey;
import com.songisuk.portfolio.rules.error.SnapshotNotFoundException;
import com.songisuk.portfolio.rules.mapper.MemberSnapshotMapper;
import org.springframework.stereotype.Service;

@Service
public class MemberSnapshotService {
    private final MemberSnapshotMapper mapper;

    public MemberSnapshotService(MemberSnapshotMapper mapper) {
        this.mapper = mapper;
    }

    public MemberSnapshot load(
            String subjectId,
            SnapshotPolicy policy,
            Integer requestedYear,
            Integer requestedMonth
    ) {
        SnapshotKey snapshot = switch (policy) {
            case LATEST_ONLY -> latest(subjectId);
            case EXPLICIT_OR_LATEST -> explicitOrLatest(subjectId, requestedYear, requestedMonth);
        };

        return mapper.findBySubjectAndSnapshot(subjectId, snapshot)
                .orElseThrow(() -> new SnapshotNotFoundException(
                        "snapshot data is missing for the resolved subject and period"
                ));
    }

    private SnapshotKey explicitOrLatest(String subjectId, Integer year, Integer month) {
        if (year == null && month == null) {
            return latest(subjectId);
        }
        if (year == null || month == null) {
            throw new IllegalArgumentException("year and month must be provided together");
        }
        return new SnapshotKey(year, month);
    }

    private SnapshotKey latest(String subjectId) {
        return mapper.findLatestSnapshot(subjectId)
                .orElseThrow(() -> new SnapshotNotFoundException(
                        "latest snapshot is unavailable for the resolved subject"
                ));
    }
}
