package edu.unimag.dto.periodoacademico;

import edu.unimag.modelo.enumeracion.EstadoEntidad;

public record PeriodoAcademicoDto(
        Integer id,
        String nombre,
        EstadoEntidad estado
        ) {

}
