package com.eventos.pf.service.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.*;
import java.io.Serializable;
import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * A DTO for the {@link com.eventos.pf.domain.Integrante} entity.
 */
@Schema(
    description = "Integrante/Participante de un evento\nCAMBIO: Ahora con relación ManyToMany (un integrante puede estar en varios eventos)"
)
@SuppressWarnings("common-java:DuplicatedBlocks")
public class IntegranteDTO implements Serializable {

    private Long id;

    @NotNull
    @Size(max = 100)
    private String nombre;

    @NotNull
    @Size(max = 100)
    private String apellido;

    @Size(max = 100)
    private String identificacion;

    private Set<EventoDTO> eventos = new HashSet<>();

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public String getApellido() {
        return apellido;
    }

    public void setApellido(String apellido) {
        this.apellido = apellido;
    }

    public String getIdentificacion() {
        return identificacion;
    }

    public void setIdentificacion(String identificacion) {
        this.identificacion = identificacion;
    }

    public Set<EventoDTO> getEventos() {
        return eventos;
    }

    public void setEventos(Set<EventoDTO> eventos) {
        this.eventos = eventos;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof IntegranteDTO)) {
            return false;
        }

        IntegranteDTO integranteDTO = (IntegranteDTO) o;
        if (this.id == null) {
            return false;
        }
        return Objects.equals(this.id, integranteDTO.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.id);
    }

    // prettier-ignore
    @Override
    public String toString() {
        return "IntegranteDTO{" +
            "id=" + getId() +
            ", nombre='" + getNombre() + "'" +
            ", apellido='" + getApellido() + "'" +
            ", identificacion='" + getIdentificacion() + "'" +
            ", eventos=" + getEventos() +
            "}";
    }
}
