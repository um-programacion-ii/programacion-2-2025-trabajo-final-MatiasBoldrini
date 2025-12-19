package com.eventos.pf.service.sync.dto;

import java.time.Instant;

public class SyncTriggerRequest {
    private String source;
    private Long offset;
    private Instant timestamp;

    public SyncTriggerRequest() {}

    public String getSource() {
        return source;
    }

    public void setSource(String source) {
        this.source = source;
    }

    public Long getOffset() {
        return offset;
    }

    public void setOffset(Long offset) {
        this.offset = offset;
    }

    public Instant getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Instant timestamp) {
        this.timestamp = timestamp;
    }
}


