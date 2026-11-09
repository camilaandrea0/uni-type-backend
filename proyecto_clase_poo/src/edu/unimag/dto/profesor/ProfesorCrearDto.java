package edu.unimag.dto.profesor;

import edu.unimag.dto.validacion.ReglasValidacion;

public record ProfesorCrearDto(
        String nombre,
        String celular
        ) {

    public ProfesorCrearDto {
        nombre = ReglasValidacion.limpiarRequerido(nombre, "El nombre es obligatorio");
        celular = ReglasValidacion.limpiarCelular(celular);
    }
}
