package edu.unimag.dto.materia;

import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.modelo.constante.PoliticasAcademicas;

public record MateriaCrearDto(
        String nombre,
        Integer creditos,
        Integer cupoMaximo
        ) {

    public MateriaCrearDto {
        nombre = ReglasValidacion.limpiarRequerido(nombre, "El nombre de la materia es obligatorio");

        creditos = ReglasValidacion.limpiarEnteroEnRango(
                creditos,
                PoliticasAcademicas.MIN_CREDITOS_MATERIA,
                PoliticasAcademicas.MAX_CREDITOS_MATERIA,
                "Los creditos deben estar entre "
                + PoliticasAcademicas.MIN_CREDITOS_MATERIA
                + " y "
                + PoliticasAcademicas.MAX_CREDITOS_MATERIA
        );

        cupoMaximo = ReglasValidacion.limpiarEnteroEnRango(
                cupoMaximo,
                PoliticasAcademicas.MIN_CUPO_MATERIA,
                PoliticasAcademicas.MAX_CUPO_MATERIA,
                "El cupo máximo debe estar entre "
                + PoliticasAcademicas.MIN_CUPO_MATERIA
                + " y "
                + PoliticasAcademicas.MAX_CUPO_MATERIA
        );
    }
}
