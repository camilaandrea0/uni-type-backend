package edu.unimag.dto.formacion;

import edu.unimag.dto.validacion.ReglasValidacion;
import java.time.OffsetDateTime;

public record FormacionProfesorCrearDto(
        Long idProfesor,
        String titulo,
        OffsetDateTime fecha
        ) {

    public FormacionProfesorCrearDto {
        if (idProfesor == null) {
            throw new IllegalArgumentException("El ID del profesor es obligatorio");
        }
        titulo = ReglasValidacion.limpiarRequerido(titulo, "El titulo es obligatorio");
        if (fecha == null) {
            throw new IllegalArgumentException("La fecha es obligatoria");
        }
    }
}
