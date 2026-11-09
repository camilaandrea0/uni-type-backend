package edu.unimag.modelo.convertidor;

import com.cleandev.tpa.api.converter.BaseCodedEnumConverter;
import edu.unimag.modelo.enumeracion.EstadoEntidad;

public class EstadoEntidadConverter extends BaseCodedEnumConverter<EstadoEntidad> {

    @Override
    protected Class<EstadoEntidad> getEnumClass() {
        return EstadoEntidad.class;
    }
}
