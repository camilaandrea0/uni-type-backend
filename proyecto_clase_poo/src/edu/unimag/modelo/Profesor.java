package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaConvert;
import com.cleandev.tpa.api.annotations.TpaId;
import edu.unimag.modelo.convertidor.EstadoEntidadConverter;
import edu.unimag.modelo.enumeracion.EstadoEntidad;

public class Profesor {

    @TpaId
    private Long idProfesor;
    private String nombreProfesor;
    private String celularProfesor;
    @TpaConvert(converter = EstadoEntidadConverter.class)
    private EstadoEntidad estadoProfesor;

    // Reflexión
    protected Profesor() {
    }

    // Creación
    public Profesor(String nombreProfesor, String celularProfesor) {
        this.nombreProfesor = nombreProfesor;
        this.celularProfesor = celularProfesor;
        this.estadoProfesor = EstadoEntidad.ACTIVO;
    }

    // Hidratación
    public Profesor(Long idProfesor, String nombreProfesor,
            String celularProfesor, EstadoEntidad estadoProfesor) {
        this.nombreProfesor = nombreProfesor;
        this.celularProfesor = celularProfesor;
        this.estadoProfesor = (estadoProfesor != null) ? estadoProfesor : EstadoEntidad.ACTIVO;

        if (idProfesor == null) {
            throw new IllegalArgumentException("El ID del profesor es obligatorio en hidratacion");
        }
        this.idProfesor = idProfesor;
    }

    // Métodos de comportamiento
    public void actualizarNombre(String nuevoNombre) {
        if (nuevoNombre.equalsIgnoreCase(this.nombreProfesor)) {
            throw new IllegalArgumentException("El nuevo nombre es igual al actual, no hay nada que actualizar");
        }
        this.nombreProfesor = nuevoNombre;
    }

    public void actualizarCelular(String nuevoCelular) {
        if (nuevoCelular.equals(this.celularProfesor)) {
            throw new IllegalArgumentException("El nuevo celular es igual al actual, no hay nada que actualizar");
        }
        this.celularProfesor = nuevoCelular;
    }

    public void cambiarEstado(EstadoEntidad nuevoEstado) {
        this.estadoProfesor = estadoProfesor.cambiarEstadoA(nuevoEstado);
    }

    // Métodos de consulta
    public boolean estaActivo() {
        return this.estadoProfesor == EstadoEntidad.ACTIVO;
    }

    // Getters
    public Long getIdProfesor() {
        return idProfesor;
    }

    public String getNombreProfesor() {
        return nombreProfesor;
    }

    public String getCelularProfesor() {
        return celularProfesor;
    }

    public EstadoEntidad getEstadoProfesor() {
        return estadoProfesor;
    }
}
