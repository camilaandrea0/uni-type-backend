package edu.unimag.modelo.convertidor;

import com.cleandev.tpa.api.converter.BaseCodedEnumConverter;
import edu.unimag.modelo.enumeracion.EstadoProfesorCurso;

public class EstadoProfesorCursoCoverter extends BaseCodedEnumConverter<EstadoProfesorCurso> {

    @Override
    protected Class<EstadoProfesorCurso> getEnumClass() {
        return EstadoProfesorCurso.class;
    }

}
