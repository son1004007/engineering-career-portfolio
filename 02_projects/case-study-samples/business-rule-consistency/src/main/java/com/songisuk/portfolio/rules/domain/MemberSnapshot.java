package com.songisuk.portfolio.rules.domain;

public record MemberSnapshot(String subjectId, SnapshotKey snapshot, int score) {
    public MemberSnapshot {
        if (subjectId == null || subjectId.isBlank()) {
            throw new IllegalArgumentException("subjectId is required");
        }
        if (score < 0 || score > 100) {
            throw new IllegalArgumentException("score must be between 0 and 100");
        }
    }
}
