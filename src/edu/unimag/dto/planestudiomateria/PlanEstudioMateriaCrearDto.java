package edu.unimag.dto.planestudiomateria;

import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.modelo.constante.PoliticasAcademicas;

public record PlanEstudioMateriaCrearDto(
        Long idPlanEstudio,
        Integer idMateria,
        Integer semestre,
        Integer orden
        ) {

    public PlanEstudioMateriaCrearDto {
        idPlanEstudio = ReglasValidacion.limpiarLongRequerido(
                idPlanEstudio,
                "El ID del plan de estudio es obligatorio"
        );
        idMateria = ReglasValidacion.limpiarEnteroRequerido(
                idMateria,
                "El ID de la materia es obligatorio"
        );
        semestre = ReglasValidacion.limpiarEnteroEnRango(
                semestre,
                PoliticasAcademicas.MIN_SEMESTRE_PLAN,
                PoliticasAcademicas.MAX_SEMESTRE_PLAN,
                "El semestre debe estar entre " + PoliticasAcademicas.MIN_SEMESTRE_PLAN
                + " y " + PoliticasAcademicas.MAX_SEMESTRE_PLAN
        );
        orden = ReglasValidacion.limpiarEnteroEnRango(
                orden,
                PoliticasAcademicas.MIN_ORDEN_MATERIA,
                PoliticasAcademicas.MAX_ORDEN_MATERIA,
                "El orden debe estar entre " + PoliticasAcademicas.MIN_ORDEN_MATERIA
                + " y " + PoliticasAcademicas.MAX_ORDEN_MATERIA
        );
    }
}
