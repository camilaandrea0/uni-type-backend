package edu.unimag.dto.cursoperiodo;

import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.modelo.constante.PoliticasAcademicas;

public record CursoPeriodoCrearDto(
        String nombre,
        Integer idMateria,
        Integer idPeriodoAcademico,
        Integer cupo
        ) {

    public CursoPeriodoCrearDto {
        nombre = ReglasValidacion.limpiarRequerido(nombre, "El nombre del curso es obligatorio");

        if (idMateria == null) {
            throw new IllegalArgumentException("El ID de la materia es obligatorio");
        }
        if (idPeriodoAcademico == null) {
            throw new IllegalArgumentException("El ID del periodo academico es obligatorio");
        }
        cupo = ReglasValidacion.limpiarEnteroEnRango(cupo,PoliticasAcademicas.MIN_CUPO_MATERIA, PoliticasAcademicas.MAX_CUPO_MATERIA, nombre);
    }
}
