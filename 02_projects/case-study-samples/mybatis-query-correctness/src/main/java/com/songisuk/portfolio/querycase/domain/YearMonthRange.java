package com.songisuk.portfolio.querycase.domain;

import java.util.Locale;

public record YearMonthRange(int startYear, int startMonth, int endYear, int endMonth) {

    public YearMonthRange {
        validateYear(startYear, "startYear");
        validateYear(endYear, "endYear");
        validateMonth(startMonth, "startMonth");
        validateMonth(endMonth, "endMonth");
        if (startYear > endYear || (startYear == endYear && startMonth > endMonth)) {
            throw new IllegalArgumentException("start year-month must not be after end year-month");
        }
    }

    private static void validateYear(int year, String label) {
        if (year < 1000 || year > 9999) {
            throw new IllegalArgumentException(label + " must be a four-digit year");
        }
    }

    private static void validateMonth(int month, String label) {
        if (month < 1 || month > 12) {
            throw new IllegalArgumentException(label + " must be between 1 and 12");
        }
    }

    public String startYearText() {
        return String.format(Locale.ROOT, "%04d", startYear);
    }

    public String startMonthText() {
        return String.format(Locale.ROOT, "%02d", startMonth);
    }

    public String endYearText() {
        return String.format(Locale.ROOT, "%04d", endYear);
    }

    public String endMonthText() {
        return String.format(Locale.ROOT, "%02d", endMonth);
    }
}
