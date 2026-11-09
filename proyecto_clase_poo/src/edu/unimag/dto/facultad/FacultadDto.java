package edu.unimag.dto.facultad;

import edu.unimag.modelo.enumeracion.EstadoEntidad;

public record FacultadDto(
        Long id,
        String nombre,
        EstadoEntidad estado
        ) {

}
