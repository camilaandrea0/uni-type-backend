package edu.unimag.modelo.enumeracion;

import com.cleandev.tpa.api.converter.CodedEnum;
import edu.unimag.modelo.constante.PoliticasAcademicas;

public enum EstadoMonitoria implements CodedEnum {
    PENDIENTE(1, "Pendiente"),
    APROBADO(2, "Aprobado"),
    REPROBADO(3, "Reprobado");

    private final int code;
    private final String description;

    EstadoMonitoria(int code, String description) {
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

    public EstadoMonitoria cambiarEstadoA(EstadoMonitoria nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado de la monitoria es obligatorio");
        }
        if (nuevoEstado == PENDIENTE) {
            throw new IllegalStateException("Una monitoria evaluada no puede volver a estado pendiente");
        }
        return nuevoEstado;
    }

    public static EstadoMonitoria desdeCalificacion(Integer calificacion) {
        if (calificacion == null) {
            throw new IllegalArgumentException("La calificacion es obligatoria");
        }
        if (calificacion < PoliticasAcademicas.NOTA_MINIMA
                || calificacion > PoliticasAcademicas.NOTA_MAXIMA) {
            throw new IllegalArgumentException(
                    "La calificacion debe estar entre " + PoliticasAcademicas.NOTA_MINIMA
                    + " y " + PoliticasAcademicas.NOTA_MAXIMA
            );
        }
        return calificacion >= PoliticasAcademicas.NOTA_APROBACION ? APROBADO : REPROBADO;
    }
}
