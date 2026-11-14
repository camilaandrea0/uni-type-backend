package edu.unimag.dto.cursoestudiante;

import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.modelo.constante.PoliticasAcademicas;
import java.util.UUID;

public record CursoEstudianteEvaluarDto(
        UUID idEstudiante,
        UUID idCursoPeriodo,
        Integer calificacion,
        String observacion
        ) {

    public CursoEstudianteEvaluarDto {
        idEstudiante = ReglasValidacion.limpiarUuidRequerido(
                idEstudiante,
                "El ID del estudiante es obligatorio"
        );
        idCursoPeriodo = ReglasValidacion.limpiarUuidRequerido(
                idCursoPeriodo,
                "El ID del curso periodo es obligatorio"
        );
        calificacion = ReglasValidacion.limpiarEnteroEnRango(
                calificacion,
                PoliticasAcademicas.NOTA_MINIMA,
                PoliticasAcademicas.NOTA_MAXIMA,
                "La calificacion debe estar entre " + PoliticasAcademicas.NOTA_MINIMA
                + " y " + PoliticasAcademicas.NOTA_MAXIMA
        );
        if (observacion != null) {
            observacion = observacion.trim().isEmpty() ? null : observacion.trim();
        }
    }
}
