package edu.unimag.dto.programa;

import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.modelo.enumeracion.NivelPrograma;

public record ProgramaCrearDto(
        String nombre,
        NivelPrograma nivelPrograma,
        Long idFacultad
        ) {

    public ProgramaCrearDto {
        nombre = ReglasValidacion.limpiarRequerido(nombre, "El nombre del programa es obligatorio");

        if (nivelPrograma == null) {
            throw new IllegalArgumentException("El nivel académico es obligatorio");
        }

        if (idFacultad == null) {
            throw new IllegalArgumentException("El ID de la facultad es obligatorio");
        }
    }
}
