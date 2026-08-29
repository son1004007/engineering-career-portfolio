package com.songisuk.portfolio.querycase.service;

import com.songisuk.portfolio.querycase.domain.ActivitySnapshot;
import java.util.List;

public record ActivityPage(List<ActivitySnapshot> rows, long total, int page, int size) {

    public ActivityPage {
        rows = List.copyOf(rows);
    }
}
