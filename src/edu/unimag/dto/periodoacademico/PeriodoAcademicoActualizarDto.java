package edu.unimag.dto.periodoacademico;

import edu.unimag.dto.validacion.ReglasValidacion;

public record PeriodoAcademicoActualizarDto(
        Integer id,
        String nuevoNombre
        ) {

    public PeriodoAcademicoActualizarDto {
        if (id == null) {
            throw new IllegalArgumentException("El ID del periodo academico es obligatorio para actualizar");
        }

        if (nuevoNombre != null) {
            nuevoNombre = ReglasValidacion.limpiarRequerido(
                    nuevoNombre,
                    "El nuevo nombre no puede estar vacio"
            );
        }
    }
}
