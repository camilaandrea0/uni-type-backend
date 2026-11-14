package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaConvert;
import com.cleandev.tpa.api.annotations.TpaId;
import edu.unimag.modelo.convertidor.EstadoEntidadConverter;
import edu.unimag.modelo.enumeracion.EstadoEntidad;
import edu.unimag.modelo.enumeracion.NivelPrograma;
import edu.unimag.modelo.convertidor.NivelProgramaConverter;
import java.util.UUID;

public class Programa {

    @TpaId
    private UUID idPrograma;
    private String nombrePrograma;
    @TpaConvert(converter = NivelProgramaConverter.class)
    private NivelPrograma nivelPrograma;
    private Facultad facultad;
    @TpaConvert(converter = EstadoEntidadConverter.class)
    private EstadoEntidad estadoPrograma;

    // Reflexión
    protected Programa() {
    }

    // Creación
    public Programa(String nombrePrograma, NivelPrograma nivelPrograma, Facultad facultad) {
        this.nombrePrograma = nombrePrograma;
        this.nivelPrograma = nivelPrograma;
        this.facultad = facultad;
        this.estadoPrograma = EstadoEntidad.ACTIVO;
    }

    // Hidratación
    public Programa(
            UUID idPrograma,
            String nombrePrograma,
            NivelPrograma nivelPrograma,
            Facultad facultad,
            EstadoEntidad estadoPrograma
    ) {
        this.nombrePrograma = nombrePrograma;
        this.nivelPrograma = nivelPrograma;
        this.facultad = facultad;
        this.estadoPrograma = estadoPrograma;

        if (idPrograma == null) {
            throw new IllegalArgumentException("El ID del programa es obligatorio en hidratacion");
        }
        this.idPrograma = idPrograma;
    }

    // Métodos de comportamiento
    public void actualizarNombre(String nuevoNombre) {
        if (nuevoNombre.trim().equalsIgnoreCase(this.nombrePrograma)) {
            throw new IllegalArgumentException(
                    "El nuevo nombre es igual, no hay cambios"
            );
        }
        this.nombrePrograma = nuevoNombre.trim();
    }

    public void actualizarNivel(NivelPrograma nuevoNivel) {
        if (nuevoNivel == this.nivelPrograma) {
            throw new IllegalArgumentException(
                    "El nuevo nivel es igual, no hay cambios"
            );
        }
        this.nivelPrograma = nuevoNivel;
    }

    public void cambiarFacultad(Facultad nuevaFacultad) {
        if (nuevaFacultad == null) {
            throw new IllegalArgumentException("La facultad no puede ser nula");
        }
        if (nuevaFacultad.getIdFacultad().equals(this.facultad.getIdFacultad())) {
            throw new IllegalArgumentException(
                    "La nueva facultad es la misma, no hay cambios"
            );
        }
        this.facultad = nuevaFacultad;
    }

    public void suspender() {
        this.estadoPrograma = estadoPrograma.cambiarEstadoA(EstadoEntidad.INACTIVO);
    }

    public void reactivar() {
        this.estadoPrograma = estadoPrograma.cambiarEstadoA(EstadoEntidad.ACTIVO);
    }

    public boolean estaActivo() {
        return this.estadoPrograma == EstadoEntidad.ACTIVO;
    }

    // Getters
    public UUID getIdPrograma() {
        return idPrograma;
    }

    public String getNombrePrograma() {
        return nombrePrograma;
    }

    public NivelPrograma getNivelPrograma() {
        return nivelPrograma;
    }

    public Facultad getFacultad() {
        return facultad;
    }

    public EstadoEntidad getEstadoPrograma() {
        return estadoPrograma;
    }

    // Getters de conveniencia (Ley de Demeter)
    public Long getIdFacultad() {
        return this.facultad.getIdFacultad();
    }

    public String getNombreFacultad() {
        return this.facultad.getNombreFacultad();
    }

}
