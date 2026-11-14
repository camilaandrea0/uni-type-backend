package edu.unimag.modelo.convertidor;

import com.cleandev.tpa.api.converter.BaseCodedEnumConverter;
import edu.unimag.modelo.enumeracion.EstadoMonitoria;

public class EstadoMonitoriaConverter extends BaseCodedEnumConverter<EstadoMonitoria> {

    @Override
    protected Class<EstadoMonitoria> getEnumClass() {
        return EstadoMonitoria.class;
    }
}
