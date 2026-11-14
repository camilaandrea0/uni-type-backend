package edu.unimag.modelo.convertidor;

import com.cleandev.tpa.api.converter.BaseCodedEnumConverter;
import edu.unimag.modelo.enumeracion.TipoAsignacion;

public class TipoAsignacionConverter extends BaseCodedEnumConverter<TipoAsignacion> {

    @Override
    protected Class<TipoAsignacion> getEnumClass() {
        return TipoAsignacion.class;
    }
}
