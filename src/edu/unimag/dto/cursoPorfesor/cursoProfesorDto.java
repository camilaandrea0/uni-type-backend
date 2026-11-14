package edu.unimag.dto.cursoPorfesor;

import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.profesor.ProfesorDto;
import edu.unimag.modelo.enumeracion.EstadoProfesorCurso;
import edu.unimag.modelo.enumeracion.TipoAsignacion;
import java.time.OffsetDateTime;

public record cursoProfesorDto(
        ProfesorDto profesor,
        CursoPeriodoDto cursoPeriodo, 
        TipoAsignacion tipoAsignacion,
        EstadoProfesorCurso estado,
        OffsetDateTime fechaasignacion
        ) {

}
