package com.songisuk.portfolio.querycase;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.songisuk.portfolio.querycase.domain.ActivitySnapshot;
import com.songisuk.portfolio.querycase.domain.YearMonthRange;
import com.songisuk.portfolio.querycase.service.ActivityPage;
import com.songisuk.portfolio.querycase.service.ActivityQueryService;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

@SpringBootTest
class ActivityQueryIntegrationTest {

    @Autowired
    private ActivityQueryService service;

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void sameYearRangeIncludesBoundariesAndExcludesOutsideRows() {
        ActivityPage page = service.findPage("alpha", new YearMonthRange(2025, 2, 2025, 3), 0, 20);

        assertThat(page.total()).isEqualTo(3);
        assertThat(ids(page.rows())).containsExactly(6L, 4L, 5L);
    }

    @Test
    void crossYearRangeReturnsStartMiddleAndEndSegmentsWithoutGapsOrDuplicates() {
        ActivityPage page = service.findPage("alpha", new YearMonthRange(2024, 12, 2025, 3), 0, 20);

        assertThat(page.total()).isEqualTo(5);
        assertThat(ids(page.rows())).containsExactly(6L, 4L, 5L, 3L, 2L);
        assertThat(ids(page.rows())).doesNotHaveDuplicates();
    }

    @Test
    void tenantFilterPreventsCrossTenantRows() {
        ActivityPage page = service.findPage("alpha", new YearMonthRange(2024, 12, 2025, 1), 0, 20);

        assertThat(page.total()).isEqualTo(2);
        assertThat(page.rows()).allMatch(row -> row.getTenantId().equals("alpha"));
        assertThat(ids(page.rows())).containsExactly(3L, 2L);
    }

    @Test
    void countAndPageUseTheSameRangeSemantics() {
        ActivityPage first = service.findPage("alpha", new YearMonthRange(2024, 12, 2025, 3), 0, 2);
        ActivityPage second = service.findPage("alpha", new YearMonthRange(2024, 12, 2025, 3), 1, 2);

        assertThat(first.total()).isEqualTo(5);
        assertThat(second.total()).isEqualTo(5);
        assertThat(ids(first.rows())).containsExactly(6L, 4L);
        assertThat(ids(second.rows())).containsExactly(5L, 3L);

        Set<Long> overlap = ids(first.rows()).stream()
                .filter(ids(second.rows())::contains)
                .collect(Collectors.toSet());
        assertThat(overlap).isEmpty();
    }

    @Test
    void paginationOrderingIsDeterministicAcrossRepeatedReads() {
        ActivityPage firstRead = service.findPage("alpha", new YearMonthRange(2024, 12, 2025, 3), 0, 3);
        ActivityPage secondRead = service.findPage("alpha", new YearMonthRange(2024, 12, 2025, 3), 0, 3);

        assertThat(ids(firstRead.rows())).containsExactlyElementsOf(ids(secondRead.rows()));
        assertThat(ids(firstRead.rows())).containsExactly(6L, 4L, 5L);
    }

    @Test
    void serviceRejectsInvalidPaginationBeforeCallingTheDatabase() {
        YearMonthRange range = new YearMonthRange(2025, 1, 2025, 3);

        assertThatThrownBy(() -> service.findPage("alpha", range, -1, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("page");
        assertThatThrownBy(() -> service.findPage("alpha", range, 0, 101))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("size");
        assertThatThrownBy(() -> service.findPage(" ", range, 0, 10))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("tenantId");
    }

    @Test
    void syntheticSchemaDefinesCompositeRangeIndex() {
        Integer count = jdbcTemplate.queryForObject(
                "SELECT COUNT(*) FROM INFORMATION_SCHEMA.INDEXES "
                        + "WHERE TABLE_NAME = 'ACTIVITY_SNAPSHOT' AND INDEX_NAME = 'IDX_ACTIVITY_RANGE'",
                Integer.class);

        assertThat(count).isEqualTo(1);
    }

    private static List<Long> ids(List<ActivitySnapshot> rows) {
        return rows.stream().map(ActivitySnapshot::getId).toList();
    }
}
