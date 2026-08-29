package com.songisuk.portfolio.querycase.domain;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.Test;

class YearMonthRangeTest {

    @Test
    void normalizesMonthsForLexicallySortableStorage() {
        YearMonthRange range = new YearMonthRange(2024, 9, 2025, 2);

        assertThat(range.startYearText()).isEqualTo("2024");
        assertThat(range.startMonthText()).isEqualTo("09");
        assertThat(range.endYearText()).isEqualTo("2025");
        assertThat(range.endMonthText()).isEqualTo("02");
    }

    @Test
    void rejectsReversedRange() {
        assertThatThrownBy(() -> new YearMonthRange(2025, 3, 2025, 2))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("must not be after");
    }

    @Test
    void rejectsInvalidMonth() {
        assertThatThrownBy(() -> new YearMonthRange(2025, 0, 2025, 3))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("between 1 and 12");
    }
}
