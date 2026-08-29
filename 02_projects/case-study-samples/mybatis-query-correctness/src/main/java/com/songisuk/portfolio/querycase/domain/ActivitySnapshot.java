package com.songisuk.portfolio.querycase.domain;

public class ActivitySnapshot {

    private long id;
    private String tenantId;
    private String snapshotYear;
    private String snapshotMonth;
    private String itemKey;
    private String status;
    private int score;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getTenantId() {
        return tenantId;
    }

    public void setTenantId(String tenantId) {
        this.tenantId = tenantId;
    }

    public String getSnapshotYear() {
        return snapshotYear;
    }

    public void setSnapshotYear(String snapshotYear) {
        this.snapshotYear = snapshotYear;
    }

    public String getSnapshotMonth() {
        return snapshotMonth;
    }

    public void setSnapshotMonth(String snapshotMonth) {
        this.snapshotMonth = snapshotMonth;
    }

    public String getItemKey() {
        return itemKey;
    }

    public void setItemKey(String itemKey) {
        this.itemKey = itemKey;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }
}
