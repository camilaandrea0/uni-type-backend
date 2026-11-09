package edu.unimag.dto.periodoacademico;

import edu.unimag.dto.validacion.ReglasValidacion;

public record PeriodoAcademicoCrearDto(
        String nombre
        ) {

    public PeriodoAcademicoCrearDto {
        nombre = ReglasValidacion.limpiarRequerido(
                nombre,
                "El nombre del periodo academico es obligatorio"
        );
    }
}
