package edu.unimag.dto.planestudiomateria;

import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.modelo.constante.PoliticasAcademicas;

public record PlanEstudioMateriaActualizarDto(
        Long idPlanEstudio,
        Integer idMateria,
        Integer nuevoSemestre,
        Integer nuevoOrden
        ) {

    public PlanEstudioMateriaActualizarDto {
        idPlanEstudio = ReglasValidacion.limpiarLongRequerido(
                idPlanEstudio,
                "El ID del plan de estudio es obligatorio"
        );
        idMateria = ReglasValidacion.limpiarEnteroRequerido(
                idMateria,
                "El ID de la materia es obligatorio"
        );
        if (nuevoSemestre == null && nuevoOrden == null) {
            throw new IllegalArgumentException(
                    "Debe indicar el nuevo semestre o el nuevo orden de la materia"
            );
        }
        if (nuevoSemestre != null) {
            nuevoSemestre = ReglasValidacion.limpiarEnteroEnRango(
                    nuevoSemestre,
                    PoliticasAcademicas.MIN_SEMESTRE_PLAN,
                    PoliticasAcademicas.MAX_SEMESTRE_PLAN,
                    "El semestre debe estar entre " + PoliticasAcademicas.MIN_SEMESTRE_PLAN
                    + " y " + PoliticasAcademicas.MAX_SEMESTRE_PLAN
            );
        }
        if (nuevoOrden != null) {
            nuevoOrden = ReglasValidacion.limpiarEnteroEnRango(
                    nuevoOrden,
                    PoliticasAcademicas.MIN_ORDEN_MATERIA,
                    PoliticasAcademicas.MAX_ORDEN_MATERIA,
                    "El orden debe estar entre " + PoliticasAcademicas.MIN_ORDEN_MATERIA
                    + " y " + PoliticasAcademicas.MAX_ORDEN_MATERIA
            );
        }
    }
}
