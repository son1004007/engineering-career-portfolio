package com.songisuk.portfolio.rules.mapper;

import com.songisuk.portfolio.rules.domain.MemberSnapshot;
import com.songisuk.portfolio.rules.domain.SnapshotKey;
import org.springframework.stereotype.Repository;

import java.util.Comparator;
import java.util.Map;
import java.util.Optional;

@Repository
public class DemoMemberSnapshotMapper implements MemberSnapshotMapper {
    private final Map<String, Map<SnapshotKey, MemberSnapshot>> data = Map.of(
            "member-a", Map.of(
                    new SnapshotKey(2025, 12), new MemberSnapshot("member-a", new SnapshotKey(2025, 12), 71),
                    new SnapshotKey(2026, 2), new MemberSnapshot("member-a", new SnapshotKey(2026, 2), 88)
            ),
            "legacy-only", Map.of(
                    new SnapshotKey(2026, 1), new MemberSnapshot("legacy-only", new SnapshotKey(2026, 1), 64)
            )
    );

    @Override
    public Optional<SnapshotKey> findLatestSnapshot(String subjectId) {
        return Optional.ofNullable(data.get(subjectId))
                .stream()
                .flatMap(map -> map.keySet().stream())
                .max(Comparator.naturalOrder());
    }

    @Override
    public Optional<MemberSnapshot> findBySubjectAndSnapshot(String subjectId, SnapshotKey snapshot) {
        return Optional.ofNullable(data.get(subjectId))
                .map(map -> map.get(snapshot));
    }
}
