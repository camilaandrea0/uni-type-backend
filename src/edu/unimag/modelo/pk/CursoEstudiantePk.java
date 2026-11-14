package edu.unimag.modelo.pk;

import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.modelo.Estudiante;
import java.util.Objects;
import java.util.UUID;

public class CursoEstudiantePk {

    private final CursoPeriodo cursoPeriodo;
    private final Estudiante estudiante;

    protected CursoEstudiantePk() {
        this.cursoPeriodo = null;
        this.estudiante = null;
    }

    public CursoEstudiantePk(CursoPeriodo cursoPeriodo, Estudiante estudiante) {
        if (cursoPeriodo == null) {
            throw new IllegalArgumentException("El curso periodo es obligatorio");
        }
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante es obligatorio");
        }
        this.cursoPeriodo = cursoPeriodo;
        this.estudiante = estudiante;
    }

    public CursoPeriodo getCursoPeriodo() {
        return cursoPeriodo;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    // Ley de Demeter
    public UUID getIdCursoPeriodo() {
        return cursoPeriodo.getIdCursoPeriodo();
    }

    public UUID getIdEstudiante() {
        return estudiante.getIdEstudiante();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.cursoPeriodo);
        hash = 97 * hash + Objects.hashCode(this.estudiante);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CursoEstudiantePk other = (CursoEstudiantePk) obj;
        if (!Objects.equals(this.cursoPeriodo, other.cursoPeriodo)) {
            return false;
        }
        return Objects.equals(this.estudiante, other.estudiante);
    }

    @Override
    public String toString() {
        return "CursoEstudiantePk \n cursoPeriodo = " + cursoPeriodo
                + "\n estudiante = " + estudiante;
    }
}
