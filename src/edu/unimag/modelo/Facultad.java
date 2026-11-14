package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaConvert;
import com.cleandev.tpa.api.annotations.TpaId;
import edu.unimag.modelo.convertidor.EstadoEntidadConverter;
import edu.unimag.modelo.enumeracion.EstadoEntidad;

public class Facultad {

    @TpaId
    private Long idFacultad;
    private String nombreFacultad;
    @TpaConvert(converter = EstadoEntidadConverter.class)
    private EstadoEntidad estadoFacultad;

    // Reflexión
    protected Facultad() {
    }

    // Creación
    public Facultad(String nombreFacultad) {
        this.nombreFacultad = nombreFacultad;
        this.estadoFacultad = EstadoEntidad.ACTIVO;
    }

    // Hidratación
    public Facultad(Long idFacultad, String nombreFacultad, EstadoEntidad estadoFacultad) {
        this.nombreFacultad = nombreFacultad;
        this.estadoFacultad = estadoFacultad;

        if (idFacultad == null) {
            throw new IllegalArgumentException("El ID de la facultad es obligatorio en hidratacion");
        }
        this.idFacultad = idFacultad;
    }

    // Métodos de comportamiento
    public void actualizarNombre(String nuevoNombre) {
        if (nuevoNombre.equalsIgnoreCase(this.nombreFacultad)) {
            throw new IllegalArgumentException("El nuevo nombre es igual al actual");
        }
        this.nombreFacultad = nuevoNombre;
    }

    public void suspender() {
        this.estadoFacultad = estadoFacultad.cambiarEstadoA(EstadoEntidad.INACTIVO);
    }

    public void reactivar() {
        this.estadoFacultad = estadoFacultad.cambiarEstadoA(EstadoEntidad.ACTIVO);
    }

    public boolean estaActiva() {
        return this.estadoFacultad == EstadoEntidad.ACTIVO;
    }

    // Getters
    public Long getIdFacultad() {
        return idFacultad;
    }

    public String getNombreFacultad() {
        return nombreFacultad;
    }

    public EstadoEntidad getEstadoFacultad() {
        return estadoFacultad;
    }
}
