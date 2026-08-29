package com.songisuk.portfolio.querycase.repository;

import com.songisuk.portfolio.querycase.domain.ActivitySnapshot;
import java.util.List;
import org.apache.ibatis.annotations.Param;

public interface ActivitySnapshotMapper {

    List<ActivitySnapshot> findPage(
            @Param("tenantId") String tenantId,
            @Param("startYear") String startYear,
            @Param("startMonth") String startMonth,
            @Param("endYear") String endYear,
            @Param("endMonth") String endMonth,
            @Param("offset") int offset,
            @Param("limit") int limit);

    long count(
            @Param("tenantId") String tenantId,
            @Param("startYear") String startYear,
            @Param("startMonth") String startMonth,
            @Param("endYear") String endYear,
            @Param("endMonth") String endMonth);
}
