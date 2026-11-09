package edu.unimag.dto.programa;

import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.modelo.enumeracion.NivelPrograma;

import java.util.UUID;

public record ProgramaActualizarDto(
        UUID id,
        String nuevoNombre,
        NivelPrograma nuevoNivel,
        Long nuevoIdFacultad
        ) {

    public ProgramaActualizarDto {
        if (id == null) {
            throw new IllegalArgumentException("El ID del programa es obligatorio para actualizar");
        }

        if (nuevoNombre != null) {
            nuevoNombre = ReglasValidacion.limpiarRequerido(nuevoNombre, "El nuevo nombre no puede estar vacío");
        }

    }
}
