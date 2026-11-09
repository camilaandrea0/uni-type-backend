package edu.unimag.modelo.convertidor;

import com.cleandev.tpa.api.converter.BaseCodedEnumConverter;
import edu.unimag.modelo.enumeracion.NivelPrograma;

public class NivelProgramaConverter extends BaseCodedEnumConverter<NivelPrograma> {

    @Override
    protected Class<NivelPrograma> getEnumClass() {
        return NivelPrograma.class;
    }
}
