package edu.unimag.dto.facultad;

import edu.unimag.dto.validacion.ReglasValidacion;

public record FacultadActualizarDto(
        Long id,
        String nuevoNombre
        ) {

    public FacultadActualizarDto {
        if (id == null) {
            throw new IllegalArgumentException("El ID de facultad es obligatorio para actualizar");
        }

        if (nuevoNombre != null) {
            nuevoNombre = ReglasValidacion.limpiarRequerido(nuevoNombre, "El nuevo nombre no puede estar vacio");
        }
    }
}
