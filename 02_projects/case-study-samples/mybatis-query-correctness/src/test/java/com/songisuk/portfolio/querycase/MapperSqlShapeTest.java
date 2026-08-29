package com.songisuk.portfolio.querycase;

import static org.assertj.core.api.Assertions.assertThat;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.ibatis.mapping.BoundSql;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.session.SqlSessionFactory;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class MapperSqlShapeTest {

    private static final String STATEMENT =
            "com.songisuk.portfolio.querycase.repository.ActivitySnapshotMapper.findPage";
    private static final Pattern OR_TOKEN = Pattern.compile("\\bOR\\b");

    @Autowired
    private SqlSessionFactory sqlSessionFactory;

    @Test
    void sameYearRangeUsesOneSargableBranch() {
        String sql = boundSql("2025", "02", "2025", "03");

        assertThat(sql).doesNotContain("UNION ALL");
        assertThat(sql).doesNotContain("TO_NUMBER(");
        assertThat(OR_TOKEN.matcher(sql).find()).isFalse();
        assertThat(sql).contains("SNAPSHOT_YEAR = ?");
        assertThat(sql).contains("SNAPSHOT_MONTH BETWEEN ? AND ?");
    }

    @Test
    void crossYearRangeUsesThreeDisjointUnionAllBranches() {
        String sql = boundSql("2024", "12", "2026", "02");

        assertThat(countOccurrences(sql, "UNION ALL")).isEqualTo(2);
        assertThat(sql).doesNotContain("TO_NUMBER(");
        assertThat(OR_TOKEN.matcher(sql).find()).isFalse();
        assertThat(sql).contains("SNAPSHOT_YEAR = ?");
        assertThat(sql).contains("SNAPSHOT_YEAR > ?");
        assertThat(sql).contains("SNAPSHOT_YEAR < ?");
        assertThat(sql).contains("SNAPSHOT_MONTH >= ?");
        assertThat(sql).contains("SNAPSHOT_MONTH <= ?");
    }

    private String boundSql(String startYear, String startMonth, String endYear, String endMonth) {
        MappedStatement statement = sqlSessionFactory.getConfiguration().getMappedStatement(STATEMENT);
        Map<String, Object> parameters = new HashMap<>();
        parameters.put("tenantId", "alpha");
        parameters.put("startYear", startYear);
        parameters.put("startMonth", startMonth);
        parameters.put("endYear", endYear);
        parameters.put("endMonth", endMonth);
        parameters.put("offset", 0);
        parameters.put("limit", 20);
        BoundSql boundSql = statement.getBoundSql(parameters);
        return boundSql.getSql().replaceAll("\\s+", " ").trim().toUpperCase(Locale.ROOT);
    }

    private static int countOccurrences(String text, String needle) {
        int count = 0;
        int index = 0;
        while ((index = text.indexOf(needle, index)) >= 0) {
            count++;
            index += needle.length();
        }
        return count;
    }
}
