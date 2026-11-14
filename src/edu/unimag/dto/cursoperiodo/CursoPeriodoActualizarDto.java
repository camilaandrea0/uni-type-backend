package edu.unimag.dto.cursoperiodo;

import edu.unimag.dto.validacion.ReglasValidacion;

import java.util.UUID;

public record CursoPeriodoActualizarDto(
        UUID id,
        String nuevoNombre,
        Integer nuevoCupo
        ) {

    public CursoPeriodoActualizarDto {
        if (id == null) {
            throw new IllegalArgumentException("El ID del curso periodo es obligatorio para actualizar");
        }

        if (nuevoNombre != null) {
            nuevoNombre = ReglasValidacion.limpiarRequerido(nuevoNombre, "El nuevo nombre no puede estar vacio");
        }

        if (nuevoCupo != null) {
            nuevoCupo = ReglasValidacion.limpiarEnteroPositivo(nuevoCupo, "El nuevo cupo debe ser mayor a 0");
        }
    }
}
