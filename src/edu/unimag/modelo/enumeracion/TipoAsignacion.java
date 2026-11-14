package edu.unimag.modelo.enumeracion;

import com.cleandev.tpa.api.converter.CodedEnum;

public enum TipoAsignacion implements CodedEnum {
    PROFESOR_PRINCIPAL(1, "Profesor Principal"),
    PROFESOR_ACOMPANANTE(2, "Profesor Acompañante"),
    AUDITOR(3, "Auditor");

    private final int code;
    private final String description;

    TipoAsignacion(int code, String description) {
        this.code = code;
        this.description = description;
    }

    @Override
    public int getCode() {
        return code;
    }

    @Override
    public String getDescription() {
        return description;
    }

}
