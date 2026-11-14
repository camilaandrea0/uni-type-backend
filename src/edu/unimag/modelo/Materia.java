package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaConvert;
import com.cleandev.tpa.api.annotations.TpaId;
import edu.unimag.modelo.convertidor.EstadoEntidadConverter;
import edu.unimag.modelo.enumeracion.EstadoEntidad;

public class Materia {

    @TpaId
    private Integer idMateria;
    private String nombreMateria;
    private Integer creditosMateria;
    private Integer cupoMaximoMateria;
    @TpaConvert(converter = EstadoEntidadConverter.class)
    private EstadoEntidad estadoMateria;

    // Reflexión
    protected Materia() {
    }

    // Creación
    public Materia(String nombreMateria, Integer creditosMateria, Integer cupoMaximoMateria) {
        this.nombreMateria = nombreMateria;
        this.creditosMateria = creditosMateria;
        this.cupoMaximoMateria = cupoMaximoMateria;
        this.estadoMateria = EstadoEntidad.ACTIVO;
    }

    // Hidratación
    public Materia(
            Integer idMateria,
            String nombreMateria,
            Integer creditosMateria,
            Integer cupoMaximoMateria,
            EstadoEntidad estadoMateria
    ) {
        this.nombreMateria = nombreMateria;
        this.creditosMateria = creditosMateria;
        this.cupoMaximoMateria = cupoMaximoMateria;
        this.estadoMateria = (estadoMateria != null) ? estadoMateria : EstadoEntidad.ACTIVO;

        if (idMateria == null) {
            throw new IllegalArgumentException("El ID de la materia es obligatorio en hidratacion");
        }
        this.idMateria = idMateria;
    }

    // Métodos de comportamiento
    public void actualizarNombre(String nuevoNombre) {
        if (nuevoNombre.equalsIgnoreCase(this.nombreMateria)) {
            throw new IllegalArgumentException("El nuevo nombre es igual al actual");
        }
        this.nombreMateria = nuevoNombre;
    }

    public void actualizarCupo(Integer nuevoCupo) {
        if (nuevoCupo.equals(this.cupoMaximoMateria)) {
            throw new IllegalArgumentException("El nuevo cupo es igual al actual");
        }
        this.cupoMaximoMateria = nuevoCupo;
    }

    public void actualizarCreditos(Integer nuevosCreditos) {
        if (nuevosCreditos.equals(this.creditosMateria)) {
            throw new IllegalArgumentException("Los nuevos créditos son iguales a los actuales");
        }
        this.creditosMateria = nuevosCreditos;
    }

    public void suspender() {
        this.estadoMateria = estadoMateria.cambiarEstadoA(EstadoEntidad.INACTIVO);
    }

    public void reactivar() {
        this.estadoMateria = estadoMateria.cambiarEstadoA(EstadoEntidad.ACTIVO);
    }

    public boolean estaActiva() {
        return this.estadoMateria == EstadoEntidad.ACTIVO;
    }

    // Getters
    public Integer getIdMateria() {
        return idMateria;
    }

    public String getNombreMateria() {
        return nombreMateria;
    }

    public Integer getCreditosMateria() {
        return creditosMateria;
    }

    public Integer getCupoMaximoMateria() {
        return cupoMaximoMateria;
    }

    public EstadoEntidad getEstadoMateria() {
        return estadoMateria;
    }
}
