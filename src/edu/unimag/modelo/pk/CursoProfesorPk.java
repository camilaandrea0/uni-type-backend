package edu.unimag.modelo.pk;

import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.modelo.Profesor;
import java.util.Objects;
import java.util.UUID;

public class CursoProfesorPk {
    private  final  CursoPeriodo cursoPeriodo;
    private final Profesor profesor;

    protected CursoProfesorPk() {
        this.cursoPeriodo = null;
        this.profesor = null;
    }

    public CursoProfesorPk(CursoPeriodo cursoPeriodo, Profesor profesor) {
        if (cursoPeriodo==null) {
            throw new IllegalArgumentException("Curso perido es obligatorio");
        }
        if (profesor==null) {
            throw  new IllegalArgumentException("El profesor es obligatorio");
        }
        this.cursoPeriodo = cursoPeriodo;
        this.profesor = profesor;
    }

    public CursoPeriodo getCursoPeriodo() {
        return cursoPeriodo;
    }

    public Profesor getProfesor() {
        return profesor;
    }
    //ley de demeter
    public UUID getIdCusrso(){
        return cursoPeriodo.getIdCursoPeriodo();
    }
    public Long getIdProfesor(){
        return  profesor.getIdProfesor();
    }
    //metodos de indentidad

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.cursoPeriodo);
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
        final CursoProfesorPk other = (CursoProfesorPk) obj;
        if (!Objects.equals(this.cursoPeriodo, other.cursoPeriodo)) {
            return false;
        }
        return Objects.equals(this.profesor, other.profesor);
    }

    @Override
    public String toString() {
        return "CursoProfesorPk \n " + "cursoPeriodo = " + cursoPeriodo + "\n profesor = " + profesor ;
    }
    
}
