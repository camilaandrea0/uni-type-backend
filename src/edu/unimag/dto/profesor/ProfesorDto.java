package edu.unimag.dto.profesor;

import edu.unimag.modelo.enumeracion.EstadoEntidad;

public record ProfesorDto(
        Long id,
        String nombre,
        String celular,
        EstadoEntidad estado,
        Boolean activo
        ) {

}
