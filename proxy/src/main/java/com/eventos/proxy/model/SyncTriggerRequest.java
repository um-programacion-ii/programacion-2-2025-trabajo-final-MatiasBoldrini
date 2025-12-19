package com.eventos.proxy.model;

import java.time.Instant;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SyncTriggerRequest {
    private String source;
    private Long offset;
    private Instant timestamp;
}


