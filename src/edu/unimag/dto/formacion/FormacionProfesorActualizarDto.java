package edu.unimag.dto.formacion;

import edu.unimag.dto.validacion.ReglasValidacion;
import java.time.OffsetDateTime;

public record FormacionProfesorActualizarDto(
        Long id,
        String nuevoTitulo,
        OffsetDateTime nuevaFecha
        ) {

    public FormacionProfesorActualizarDto {
        if (id == null) {
            throw new IllegalArgumentException("El ID es obligatorio para actualizar");
        }

        if (nuevoTitulo != null) {
            nuevoTitulo = ReglasValidacion.limpiarRequerido(nuevoTitulo, "El título no puede estar vacío");
        }

    }
}
