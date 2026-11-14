package edu.unimag.modelo.enumeracion;

import com.cleandev.tpa.api.converter.CodedEnum;

public enum EstadoProfesorCurso implements CodedEnum{
    ACTIVO(1,"Asignado" ),
    RETIRADO(2, "Retirado");

    private int code;
    private String description;

    private EstadoProfesorCurso(int code, String description) {
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
    public EstadoProfesorCurso cambiarEstado(EstadoProfesorCurso nuevo){
        if (this==ACTIVO&&nuevo==RETIRADO) {
            return nuevo;
        }
        if (this==RETIRADO&&nuevo==ACTIVO) {
            return nuevo;
        }
        throw new IllegalArgumentException("Estado incorrecto");
    }
}