package edu.unimag.modelo.pk;

import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.modelo.Estudiante;
import edu.unimag.modelo.Profesor;
import java.util.Objects;
import java.util.UUID;

public class MonitoriaPk {

    private final CursoPeriodo cursoPeriodo;
    private final Estudiante estudiante;
    private final Profesor profesor;

    protected MonitoriaPk() {
        this.cursoPeriodo = null;
        this.estudiante = null;
        this.profesor = null;
    }

    public MonitoriaPk(CursoPeriodo cursoPeriodo, Estudiante estudiante, Profesor profesor) {
        if (cursoPeriodo == null) {
            throw new IllegalArgumentException("El curso periodo es obligatorio");
        }
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante es obligatorio");
        }
        if (profesor == null) {
            throw new IllegalArgumentException("El profesor es obligatorio");
        }
        this.cursoPeriodo = cursoPeriodo;
        this.estudiante = estudiante;
        this.profesor = profesor;
    }

    public CursoPeriodo getCursoPeriodo() {
        return cursoPeriodo;
    }

    public Estudiante getEstudiante() {
        return estudiante;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    // Ley de Demeter
    public UUID getIdCursoPeriodo() {
        return cursoPeriodo.getIdCursoPeriodo();
    }

    public UUID getIdEstudiante() {
        return estudiante.getIdEstudiante();
    }

    public Long getIdProfesor() {
        return profesor.getIdProfesor();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.cursoPeriodo);
        hash = 97 * hash + Objects.hashCode(this.estudiante);
        hash = 97 * hash + Objects.hashCode(this.profesor);
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
        final MonitoriaPk other = (MonitoriaPk) obj;
        if (!Objects.equals(this.cursoPeriodo, other.cursoPeriodo)) {
            return false;
        }
        if (!Objects.equals(this.estudiante, other.estudiante)) {
            return false;
        }
        return Objects.equals(this.profesor, other.profesor);
    }

    @Override
    public String toString() {
        return "MonitoriaPk \n cursoPeriodo = " + cursoPeriodo
                + "\n estudiante = " + estudiante
                + "\n profesor = " + profesor;
    }
}
