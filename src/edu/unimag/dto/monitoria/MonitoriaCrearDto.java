package edu.unimag.dto.monitoria;

import edu.unimag.dto.validacion.ReglasValidacion;
import java.util.UUID;

public record MonitoriaCrearDto(
        UUID idCursoPeriodo,
        UUID idEstudiante,
        Long idProfesor
        ) {

    public MonitoriaCrearDto {
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
    }
}
