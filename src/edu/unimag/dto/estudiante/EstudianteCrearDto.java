package edu.unimag.dto.estudiante;

import edu.unimag.dto.validacion.ReglasValidacion;

public record EstudianteCrearDto(
        String codigo,
        String nombre,
        String correo,
        String celular,
        String direccion
        ) {

    public EstudianteCrearDto {
        codigo = ReglasValidacion.limpiarRequerido(codigo, "El código es obligatorio");
        nombre = ReglasValidacion.limpiarRequerido(nombre, "El nombre es obligatorio");
        correo = ReglasValidacion.limpiarCorreo(correo);
        celular = ReglasValidacion.limpiarCelular(celular);
        direccion = ReglasValidacion.limpiarRequerido(direccion, "La dirección es obligatoria");
    }
}
