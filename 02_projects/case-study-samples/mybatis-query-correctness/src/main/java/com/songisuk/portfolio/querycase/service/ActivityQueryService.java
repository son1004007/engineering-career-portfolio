package com.songisuk.portfolio.querycase.service;

import com.songisuk.portfolio.querycase.domain.ActivitySnapshot;
import com.songisuk.portfolio.querycase.domain.YearMonthRange;
import com.songisuk.portfolio.querycase.repository.ActivitySnapshotMapper;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class ActivityQueryService {

    private final ActivitySnapshotMapper mapper;

    public ActivityQueryService(ActivitySnapshotMapper mapper) {
        this.mapper = mapper;
    }

    public ActivityPage findPage(String tenantId, YearMonthRange range, int page, int size) {
        if (tenantId == null || tenantId.isBlank()) {
            throw new IllegalArgumentException("tenantId must not be blank");
        }
        if (page < 0) {
            throw new IllegalArgumentException("page must not be negative");
        }
        if (size < 1 || size > 100) {
            throw new IllegalArgumentException("size must be between 1 and 100");
        }

        int offset = Math.multiplyExact(page, size);
        String startYear = range.startYearText();
        String startMonth = range.startMonthText();
        String endYear = range.endYearText();
        String endMonth = range.endMonthText();

        long total = mapper.count(tenantId, startYear, startMonth, endYear, endMonth);
        List<ActivitySnapshot> rows = mapper.findPage(
                tenantId,
                startYear,
                startMonth,
                endYear,
                endMonth,
                offset,
                size);
        return new ActivityPage(rows, total, page, size);
    }
}
