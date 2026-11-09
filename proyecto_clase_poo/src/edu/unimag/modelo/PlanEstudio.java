package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaConvert;
import com.cleandev.tpa.api.annotations.TpaId;
import edu.unimag.modelo.convertidor.EstadoEntidadConverter;
import edu.unimag.modelo.enumeracion.EstadoEntidad;
import java.time.OffsetDateTime;
import java.util.UUID;

public class PlanEstudio {

    @TpaId
    private Long idPlanEstudio;
    private String versionPlanEstudio;
    @TpaConvert(converter = EstadoEntidadConverter.class)
    private EstadoEntidad vigentePlanEstudio;
    private OffsetDateTime fechaCreacionPlanEstudio;
    private Programa programa;

    // Reflexion
    protected PlanEstudio() {
    }

    // Creacion
    public PlanEstudio(String versionPlanEstudio, Programa programa) {
        this.versionPlanEstudio = versionPlanEstudio;
        this.programa = programa;
        this.vigentePlanEstudio = EstadoEntidad.ACTIVO;
        this.fechaCreacionPlanEstudio = OffsetDateTime.now();
    }

    // 3. Hidratacion
    public PlanEstudio(
            Long idPlanEstudio,
            String versionPlanEstudio,
            EstadoEntidad vigentePlanEstudio,
            OffsetDateTime fechaCreacionPlanEstudio,
            Programa programa
    ) {
        this.versionPlanEstudio = versionPlanEstudio;
        this.programa = programa;

        this.vigentePlanEstudio = (vigentePlanEstudio != null) ? vigentePlanEstudio : EstadoEntidad.ACTIVO;
        this.fechaCreacionPlanEstudio = (fechaCreacionPlanEstudio != null) ? fechaCreacionPlanEstudio : OffsetDateTime.now();

        if (idPlanEstudio == null) {
            throw new IllegalArgumentException("El ID del plan de estudio es obligatorio en hidratacion");
        }
        this.idPlanEstudio = idPlanEstudio;
    }

    // Métodos de comportamiento
    public void actualizarVersion(String nuevaVersion) {
        if (nuevaVersion.equalsIgnoreCase(this.versionPlanEstudio)) {
            throw new IllegalArgumentException(
                    "La nueva version es igual a la actual, no hay cambios que realizar"
            );
        }
        this.versionPlanEstudio = nuevaVersion;
    }

    public void activar() {
        this.vigentePlanEstudio = vigentePlanEstudio.cambiarEstadoA(EstadoEntidad.ACTIVO);
    }

    public void inactivar() {
        this.vigentePlanEstudio = vigentePlanEstudio.cambiarEstadoA(EstadoEntidad.INACTIVO);
    }

    // Métodos de consulta
    public boolean estaActivo() {
        return this.vigentePlanEstudio == EstadoEntidad.ACTIVO;
    }

    public Long getIdPlanEstudio() {
        return idPlanEstudio;
    }

    public String getVersionPlanEstudio() {
        return versionPlanEstudio;
    }

    public EstadoEntidad getVigentePlanEstudio() {
        return vigentePlanEstudio;
    }

    public OffsetDateTime getFechaCreacionPlanEstudio() {
        return fechaCreacionPlanEstudio;
    }

    public Programa getPrograma() {
        return programa;
    }

    // Getters de conveniencia (Ley de Demeter)
    public UUID getIdPrograma() {
        return this.programa.getIdPrograma();
    }

    public String getNombrePrograma() {
        return this.programa.getNombrePrograma();
    }
}
