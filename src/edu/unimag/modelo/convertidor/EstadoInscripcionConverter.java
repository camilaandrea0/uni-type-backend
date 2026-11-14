package edu.unimag.modelo.convertidor;

import com.cleandev.tpa.api.converter.BaseCodedEnumConverter;
import edu.unimag.modelo.enumeracion.EstadoInscripcion;

public class EstadoInscripcionConverter extends BaseCodedEnumConverter<EstadoInscripcion> {

    @Override
    protected Class<EstadoInscripcion> getEnumClass() {
        return EstadoInscripcion.class;
    }
}
