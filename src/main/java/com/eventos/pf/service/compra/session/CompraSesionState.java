package com.eventos.pf.service.compra.session;

import java.io.Serializable;
import java.time.Instant;

/**
 * Estado “vivo” de la sesión de compra (persistido en Redis local con TTL por inactividad).
 *
 * Nota: guardamos asientos/personas como JSON (String) para desacoplar el storage de DTOs internos.
 */
public class CompraSesionState implements Serializable {

    private Long sesionId;
    private Long eventoIdLocal;
    private Long eventoIdCatedra;
    private Integer pasoActual;

    private String asientosSeleccionadosJson;
    private String datosPersonasJson;

    private Instant ultimaActividad;

    public Long getSesionId() {
        return sesionId;
    }

    public void setSesionId(Long sesionId) {
        this.sesionId = sesionId;
    }

    public Long getEventoIdLocal() {
        return eventoIdLocal;
    }

    public void setEventoIdLocal(Long eventoIdLocal) {
        this.eventoIdLocal = eventoIdLocal;
    }

    public Long getEventoIdCatedra() {
        return eventoIdCatedra;
    }

    public void setEventoIdCatedra(Long eventoIdCatedra) {
        this.eventoIdCatedra = eventoIdCatedra;
    }

    public Integer getPasoActual() {
        return pasoActual;
    }

    public void setPasoActual(Integer pasoActual) {
        this.pasoActual = pasoActual;
    }

    public String getAsientosSeleccionadosJson() {
        return asientosSeleccionadosJson;
    }

    public void setAsientosSeleccionadosJson(String asientosSeleccionadosJson) {
        this.asientosSeleccionadosJson = asientosSeleccionadosJson;
    }

    public String getDatosPersonasJson() {
        return datosPersonasJson;
    }

    public void setDatosPersonasJson(String datosPersonasJson) {
        this.datosPersonasJson = datosPersonasJson;
    }

    public Instant getUltimaActividad() {
        return ultimaActividad;
    }

    public void setUltimaActividad(Instant ultimaActividad) {
        this.ultimaActividad = ultimaActividad;
    }
}





