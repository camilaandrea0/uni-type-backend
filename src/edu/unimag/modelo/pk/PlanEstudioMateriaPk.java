package edu.unimag.modelo.pk;

import edu.unimag.modelo.Materia;
import edu.unimag.modelo.PlanEstudio;
import java.util.Objects;

public class PlanEstudioMateriaPk {

    private final PlanEstudio planEstudio;
    private final Materia materia;

    protected PlanEstudioMateriaPk() {
        this.planEstudio = null;
        this.materia = null;
    }

    public PlanEstudioMateriaPk(PlanEstudio planEstudio, Materia materia) {
        if (planEstudio == null) {
            throw new IllegalArgumentException("El plan de estudio es obligatorio");
        }
        if (materia == null) {
            throw new IllegalArgumentException("La materia es obligatoria");
        }
        this.planEstudio = planEstudio;
        this.materia = materia;
    }

    public PlanEstudio getPlanEstudio() {
        return planEstudio;
    }

    public Materia getMateria() {
        return materia;
    }

    // Ley de Demeter
    public Long getIdPlanEstudio() {
        return planEstudio.getIdPlanEstudio();
    }

    public Integer getIdMateria() {
        return materia.getIdMateria();
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 97 * hash + Objects.hashCode(this.planEstudio);
        hash = 97 * hash + Objects.hashCode(this.materia);
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
        final PlanEstudioMateriaPk other = (PlanEstudioMateriaPk) obj;
        if (!Objects.equals(this.planEstudio, other.planEstudio)) {
            return false;
        }
        return Objects.equals(this.materia, other.materia);
    }

    @Override
    public String toString() {
        return "PlanEstudioMateriaPk \n planEstudio = " + planEstudio
                + "\n materia = " + materia;
    }
}
