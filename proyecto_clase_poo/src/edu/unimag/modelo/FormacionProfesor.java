package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaId;
import java.time.OffsetDateTime;

public class FormacionProfesor {

    @TpaId
    private Long idFormacionProfesor;
    private Profesor profesor;
    private String tituloFormacionProfesor;
    private OffsetDateTime fechaFormacionProfesor;

    // Reflexión
    protected FormacionProfesor() {
    }

    // Creación
    public FormacionProfesor(Profesor profesor, String tituloFormacionProfesor, OffsetDateTime fechaFormacionProfesor) {
        this.profesor = profesor;
        this.tituloFormacionProfesor = tituloFormacionProfesor;
        this.fechaFormacionProfesor = fechaFormacionProfesor;
    }

    // Hidratación
    public FormacionProfesor(
            Long idFormacionProfesor,
            Profesor profesor,
            String tituloFormacionProfesor,
            OffsetDateTime fechaFormacionProfesor
    ) {
        this(profesor, tituloFormacionProfesor, fechaFormacionProfesor);

        if (idFormacionProfesor == null) {
            throw new IllegalArgumentException("El ID de la formacion es obligatorio en hidratacion");
        }
        this.idFormacionProfesor = idFormacionProfesor;
    }

    // Métodos de comportamiento
    public void actualizarTitulo(String nuevoTitulo) {
        if (nuevoTitulo.equalsIgnoreCase(this.tituloFormacionProfesor)) {
            throw new IllegalArgumentException("El nuevo titulo es igual al actual");
        }
        this.tituloFormacionProfesor = nuevoTitulo;
    }

    public void actualizarFecha(OffsetDateTime nuevaFecha) {
        if (nuevaFecha.equals(this.fechaFormacionProfesor)) {
            throw new IllegalArgumentException("La nueva fecha es igual a la actual");
        }
        this.fechaFormacionProfesor = nuevaFecha;
    }

    // Getters
    public Long getIdFormacionProfesor() {
        return idFormacionProfesor;
    }

    public Profesor getProfesor() {
        return profesor;
    }

    public String getTituloFormacionProfesor() {
        return tituloFormacionProfesor;
    }

    public OffsetDateTime getFechaFormacionProfesor() {
        return fechaFormacionProfesor;
    }

    // Getters de conveniencia - Ley de Demeter
    public Long getIdProfesor() {
        return this.profesor.getIdProfesor();
    }
}
