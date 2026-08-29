package com.songisuk.portfolio.rules.mapper;

import com.songisuk.portfolio.rules.domain.MemberSnapshot;
import com.songisuk.portfolio.rules.domain.SnapshotKey;

import java.util.Optional;

public interface MemberSnapshotMapper {
    Optional<SnapshotKey> findLatestSnapshot(String subjectId);

    Optional<MemberSnapshot> findBySubjectAndSnapshot(String subjectId, SnapshotKey snapshot);
}
