package edu.unimag.dto.monitoria;

import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.estudiante.EstudianteDto;
import edu.unimag.dto.profesor.ProfesorDto;
import edu.unimag.modelo.enumeracion.EstadoMonitoria;
import java.time.OffsetDateTime;

public record MonitoriaDto(
        CursoPeriodoDto cursoPeriodo,
        EstudianteDto estudiante,
        ProfesorDto profesor,
        Integer calificacion,
        String comentario,
        EstadoMonitoria estado,
        OffsetDateTime fechaCreacion,
        OffsetDateTime fechaEvaluacion
        ) {

}
