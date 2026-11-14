package edu.unimag.dto.cursoestudiante;

import edu.unimag.dto.validacion.ReglasValidacion;
import java.util.UUID;

public record CursoEstudianteCrearDto(
        UUID idEstudiante,
        UUID idCursoPeriodo
        ) {

    public CursoEstudianteCrearDto {
        idEstudiante = ReglasValidacion.limpiarUuidRequerido(
                idEstudiante,
                "El ID del estudiante es obligatorio"
        );
        idCursoPeriodo = ReglasValidacion.limpiarUuidRequerido(
                idCursoPeriodo,
                "El ID del curso periodo es obligatorio"
        );
    }
}
