package com.songisuk.portfolio.rules.domain;

public record SnapshotKey(int year, int month) implements Comparable<SnapshotKey> {
    public SnapshotKey {
        if (year < 2000 || year > 2100) {
            throw new IllegalArgumentException("year must be between 2000 and 2100");
        }
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException("month must be between 1 and 12");
        }
    }

    @Override
    public int compareTo(SnapshotKey other) {
        int yearCompare = Integer.compare(year, other.year);
        return yearCompare != 0 ? yearCompare : Integer.compare(month, other.month);
    }
}
