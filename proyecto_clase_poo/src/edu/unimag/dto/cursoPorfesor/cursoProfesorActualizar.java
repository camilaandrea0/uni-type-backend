package edu.unimag.dto.cursoPorfesor;

import edu.unimag.modelo.enumeracion.TipoAsignacion;
import java.util.UUID;

public record cursoProfesorActualizar(
        UUID idCursoPeriodo,
        Long idProfesor,
        TipoAsignacion tipoAsignacion
        
        
        ) {
    
    public cursoProfesorActualizar{
        if (idCursoPeriodo==null) {
            throw new IllegalArgumentException("EL id delcurso no puede ser nulo");
        }
        if (idProfesor==null) {
            throw new IllegalArgumentException("EL id del profesor no pueder ser null");
        }
        if (tipoAsignacion==null) {
            throw new IllegalArgumentException("La asignacion del profesor no pueder ser null");
        }
    }
}
