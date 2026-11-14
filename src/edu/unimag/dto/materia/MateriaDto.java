package edu.unimag.dto.materia;

import edu.unimag.modelo.enumeracion.EstadoEntidad;

public record MateriaDto(
        Integer id,
        String nombre,
        Integer creditos,
        Integer cupoMaximo,
        EstadoEntidad estado
        ) {

}
