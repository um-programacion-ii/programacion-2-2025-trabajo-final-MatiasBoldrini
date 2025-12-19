package com.eventos.pf.service.catedra.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;

public class EventoCatedraDTO {
    private Long id;
    private String titulo;
    private String resumen;
    private String descripcion;
    private Instant fecha;
    private String direccion;
    private String imagen;
    private Integer filaAsientos;

    /**
     * En la cátedra se llama "columnAsientos".
     */
    @JsonProperty("columnAsientos")
    private Integer columnAsientos;

    private BigDecimal precioEntrada;
    private EventoTipoDTO eventoTipo;
    private List<IntegranteCatedraDTO> integrantes;

    public EventoCatedraDTO() {}

    public EventoCatedraDTO(
        Long id,
        String titulo,
        String resumen,
        String descripcion,
        Instant fecha,
        String direccion,
        String imagen,
        Integer filaAsientos,
        Integer columnAsientos,
        BigDecimal precioEntrada,
        EventoTipoDTO eventoTipo,
        List<IntegranteCatedraDTO> integrantes
    ) {
        this.id = id;
        this.titulo = titulo;
        this.resumen = resumen;
        this.descripcion = descripcion;
        this.fecha = fecha;
        this.direccion = direccion;
        this.imagen = imagen;
        this.filaAsientos = filaAsientos;
        this.columnAsientos = columnAsientos;
        this.precioEntrada = precioEntrada;
        this.eventoTipo = eventoTipo;
        this.integrantes = integrantes;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getTitulo() {
        return titulo;
    }

    public void setTitulo(String titulo) {
        this.titulo = titulo;
    }

    public String getResumen() {
        return resumen;
    }

    public void setResumen(String resumen) {
        this.resumen = resumen;
    }

    public String getDescripcion() {
        return descripcion;
    }

    public void setDescripcion(String descripcion) {
        this.descripcion = descripcion;
    }

    public Instant getFecha() {
        return fecha;
    }

    public void setFecha(Instant fecha) {
        this.fecha = fecha;
    }

    public String getDireccion() {
        return direccion;
    }

    public void setDireccion(String direccion) {
        this.direccion = direccion;
    }

    public String getImagen() {
        return imagen;
    }

    public void setImagen(String imagen) {
        this.imagen = imagen;
    }

    public Integer getFilaAsientos() {
        return filaAsientos;
    }

    public void setFilaAsientos(Integer filaAsientos) {
        this.filaAsientos = filaAsientos;
    }

    public Integer getColumnAsientos() {
        return columnAsientos;
    }

    public void setColumnAsientos(Integer columnAsientos) {
        this.columnAsientos = columnAsientos;
    }

    public BigDecimal getPrecioEntrada() {
        return precioEntrada;
    }

    public void setPrecioEntrada(BigDecimal precioEntrada) {
        this.precioEntrada = precioEntrada;
    }

    public EventoTipoDTO getEventoTipo() {
        return eventoTipo;
    }

    public void setEventoTipo(EventoTipoDTO eventoTipo) {
        this.eventoTipo = eventoTipo;
    }

    public List<IntegranteCatedraDTO> getIntegrantes() {
        return integrantes;
    }

    public void setIntegrantes(List<IntegranteCatedraDTO> integrantes) {
        this.integrantes = integrantes;
    }
}


