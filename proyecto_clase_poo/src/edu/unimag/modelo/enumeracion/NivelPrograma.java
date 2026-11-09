package edu.unimag.modelo.enumeracion;

import com.cleandev.tpa.api.converter.CodedEnum;

public enum NivelPrograma implements CodedEnum {
    TECNICO(1, "Tecnico"),
    TECNOLOGICO(2, "Tecnologico"),
    PREGRADO(3, "Pregrado"),
    ESPECIALIZACION(4, "Especializacion"),
    MAESTRIA(5, "Maestria"),
    DOCTORADO(6, "Doctorado");

    private final int code;
    private final String description;

    NivelPrograma(int code, String description) {
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
