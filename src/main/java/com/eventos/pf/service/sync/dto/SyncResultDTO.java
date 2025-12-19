package com.eventos.pf.service.sync.dto;

public class SyncResultDTO {

    private String status;
    private int creados;
    private int actualizados;
    private int cancelados;
    private long durationMs;

    public SyncResultDTO() {}

    public SyncResultDTO(String status, int creados, int actualizados, int cancelados, long durationMs) {
        this.status = status;
        this.creados = creados;
        this.actualizados = actualizados;
        this.cancelados = cancelados;
        this.durationMs = durationMs;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getCreados() {
        return creados;
    }

    public void setCreados(int creados) {
        this.creados = creados;
    }

    public int getActualizados() {
        return actualizados;
    }

    public void setActualizados(int actualizados) {
        this.actualizados = actualizados;
    }

    public int getCancelados() {
        return cancelados;
    }

    public void setCancelados(int cancelados) {
        this.cancelados = cancelados;
    }

    public long getDurationMs() {
        return durationMs;
    }

    public void setDurationMs(long durationMs) {
        this.durationMs = durationMs;
    }
}


