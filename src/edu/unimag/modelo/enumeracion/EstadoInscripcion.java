package edu.unimag.modelo.enumeracion;

import com.cleandev.tpa.api.converter.CodedEnum;
import edu.unimag.modelo.constante.PoliticasAcademicas;

public enum EstadoInscripcion implements CodedEnum {
    INSCRITO(1, "Inscrito"),
    ADMITIDO(2, "Admitido"),
    APROBADO(3, "Aprobado"),
    REPROBADO(4, "Reprobado"),
    RETIRADO(5, "Retirado"),
    RECHAZADO(6, "Rechazado");

    private final int code;
    private final String description;

    EstadoInscripcion(int code, String description) {
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

    public EstadoInscripcion cambiarEstadoA(EstadoInscripcion nuevoEstado) {
        if (nuevoEstado == null) {
            throw new IllegalArgumentException("El nuevo estado de la inscripcion es obligatorio");
        }
        if (this == nuevoEstado) {
            throw new IllegalStateException("La inscripcion ya se encuentra en estado: " + this);
        }

        boolean transicionValida = switch (this) {
            case INSCRITO ->
                nuevoEstado == ADMITIDO || nuevoEstado == RETIRADO || nuevoEstado == RECHAZADO;
            case ADMITIDO ->
                nuevoEstado == APROBADO || nuevoEstado == REPROBADO || nuevoEstado == RETIRADO;
            case APROBADO, REPROBADO, RETIRADO, RECHAZADO ->
                false;
        };

        if (!transicionValida) {
            throw new IllegalStateException(
                    "Transicion invalida de inscripcion: " + this + " -> " + nuevoEstado
            );
        }
        return nuevoEstado;
    }

    public static EstadoInscripcion desdeCalificacion(Integer calificacion) {
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

    public boolean estaActiva() {
        return this == INSCRITO || this == ADMITIDO;
    }

    public boolean estaEvaluada() {
        return this == APROBADO || this == REPROBADO;
    }
}
