package edu.unimag.dto.monitoria;

import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.modelo.constante.PoliticasAcademicas;
import java.util.UUID;

public record MonitoriaEvaluarDto(
        UUID idCursoPeriodo,
        UUID idEstudiante,
        Long idProfesor,
        Integer calificacion,
        String comentario
        ) {

    public MonitoriaEvaluarDto {
        idCursoPeriodo = ReglasValidacion.limpiarUuidRequerido(
                idCursoPeriodo,
                "El ID del curso periodo es obligatorio"
        );
        idEstudiante = ReglasValidacion.limpiarUuidRequerido(
                idEstudiante,
                "El ID del estudiante es obligatorio"
        );
        idProfesor = ReglasValidacion.limpiarLongRequerido(
                idProfesor,
                "El ID del profesor es obligatorio"
        );
        calificacion = ReglasValidacion.limpiarEnteroEnRango(
                calificacion,
                PoliticasAcademicas.NOTA_MINIMA,
                PoliticasAcademicas.NOTA_MAXIMA,
                "La calificacion debe estar entre " + PoliticasAcademicas.NOTA_MINIMA
                + " y " + PoliticasAcademicas.NOTA_MAXIMA
        );
        if (comentario != null) {
            comentario = comentario.trim().isEmpty() ? null : comentario.trim();
        }
    }
}
