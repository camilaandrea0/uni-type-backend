package edu.unimag.dto.facultad;

import edu.unimag.dto.validacion.ReglasValidacion;

public record FacultadCrearDto(
        String nombre
        ) {

    public FacultadCrearDto {
        nombre = ReglasValidacion.limpiarRequerido(nombre, "El nombre de la facultad es obligatorio");
    }
}
