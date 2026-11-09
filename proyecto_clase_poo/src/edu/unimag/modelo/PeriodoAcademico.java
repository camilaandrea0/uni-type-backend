package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaConvert;
import com.cleandev.tpa.api.annotations.TpaId;
import edu.unimag.modelo.convertidor.EstadoEntidadConverter;
import edu.unimag.modelo.enumeracion.EstadoEntidad;

public class PeriodoAcademico {

    @TpaId
    private Integer idPeriodoAcademico;
    private String nombrePeriodoAcademico;
    @TpaConvert(converter = EstadoEntidadConverter.class)
    private EstadoEntidad estadoPeriodoAcademico;

    // Reflexión
    protected PeriodoAcademico() {
    }

    // Creación
    public PeriodoAcademico(String nombrePeriodoAcademico) {
        this.nombrePeriodoAcademico = nombrePeriodoAcademico;
        this.estadoPeriodoAcademico = EstadoEntidad.ACTIVO;
    }

    // Hidratación
    public PeriodoAcademico(
            Integer idPeriodoAcademico,
            String nombrePeriodoAcademico,
            EstadoEntidad estadoPeriodoAcademico
    ) {
        this.nombrePeriodoAcademico = nombrePeriodoAcademico;
        this.estadoPeriodoAcademico = estadoPeriodoAcademico;

        if (idPeriodoAcademico == null) {
            throw new IllegalArgumentException("El ID del periodo academico es obligatorio en hidratacion");
        }
        this.idPeriodoAcademico = idPeriodoAcademico;
    }

    // Métodos de comportamiento
    public void corregirNombre(String nuevoNombre) {
        if (nuevoNombre.equalsIgnoreCase(this.nombrePeriodoAcademico)) {
            throw new IllegalArgumentException("El nuevo nombre es igual al actual, no hay nada que corregir");
        }
        this.nombrePeriodoAcademico = nuevoNombre;
    }

    public void suspender() {
        this.estadoPeriodoAcademico = estadoPeriodoAcademico.cambiarEstadoA(EstadoEntidad.INACTIVO);
    }

    public void reactivar() {
        this.estadoPeriodoAcademico = estadoPeriodoAcademico.cambiarEstadoA(EstadoEntidad.ACTIVO);
    }

    public boolean estaActivo() {
        return this.estadoPeriodoAcademico == EstadoEntidad.ACTIVO;
    }

    // Getters
    public Integer getIdPeriodoAcademico() {
        return idPeriodoAcademico;
    }

    public String getNombrePeriodoAcademico() {
        return nombrePeriodoAcademico;
    }

    public EstadoEntidad getEstadoPeriodoAcademico() {
        return estadoPeriodoAcademico;
    }
}
