package edu.unimag.dto.cursoestudiante;

import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.estudiante.EstudianteDto;
import edu.unimag.modelo.enumeracion.EstadoInscripcion;
import java.time.OffsetDateTime;

public record CursoEstudianteDto(
        EstudianteDto estudiante,
        CursoPeriodoDto cursoPeriodo,
        EstadoInscripcion estado,
        Integer calificacion,
        String observacion,
        OffsetDateTime fechaInscripcion,
        OffsetDateTime fechaEvaluacion
        ) {

}
