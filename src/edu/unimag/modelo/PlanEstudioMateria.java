package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaId;
import edu.unimag.modelo.constante.PoliticasAcademicas;
import edu.unimag.modelo.pk.PlanEstudioMateriaPk;

public class PlanEstudioMateria {

    @TpaId
    private PlanEstudioMateriaPk idPlanEstudioMateria;
    private Integer semestrePlanEstudioMateria;
    private Integer ordenPlanEstudioMateria;

    // Reflexion
    protected PlanEstudioMateria() {
    }

    // Creacion e hidratacion
    public PlanEstudioMateria(PlanEstudioMateriaPk id, Integer semestre, Integer orden) {
        if (id == null) {
            throw new IllegalArgumentException("La clave del plan-materia es obligatoria");
        }
        validarSemestre(semestre);
        validarOrden(orden);
        this.idPlanEstudioMateria = id;
        this.semestrePlanEstudioMateria = semestre;
        this.ordenPlanEstudioMateria = orden;
    }

    // Metodos de comportamiento
    public void reubicarSemestre(Integer nuevoSemestre) {
        validarSemestre(nuevoSemestre);
        if (nuevoSemestre.equals(this.semestrePlanEstudioMateria)) {
            throw new IllegalArgumentException(
                    "La materia ya se encuentra en el semestre " + nuevoSemestre
            );
        }
        this.semestrePlanEstudioMateria = nuevoSemestre;
    }

    public void actualizarOrden(Integer nuevoOrden) {
        validarOrden(nuevoOrden);
        if (nuevoOrden.equals(this.ordenPlanEstudioMateria)) {
            throw new IllegalArgumentException(
                    "La materia ya tiene el orden " + nuevoOrden
            );
        }
        this.ordenPlanEstudioMateria = nuevoOrden;
    }

    private static void validarSemestre(Integer semestre) {
        if (semestre == null) {
            throw new IllegalArgumentException("El semestre es obligatorio");
        }
        if (semestre < PoliticasAcademicas.MIN_SEMESTRE_PLAN
                || semestre > PoliticasAcademicas.MAX_SEMESTRE_PLAN) {
            throw new IllegalArgumentException(
                    "El semestre debe estar entre " + PoliticasAcademicas.MIN_SEMESTRE_PLAN
                    + " y " + PoliticasAcademicas.MAX_SEMESTRE_PLAN
            );
        }
    }

    private static void validarOrden(Integer orden) {
        if (orden == null) {
            throw new IllegalArgumentException("El orden es obligatorio");
        }
        if (orden < PoliticasAcademicas.MIN_ORDEN_MATERIA
                || orden > PoliticasAcademicas.MAX_ORDEN_MATERIA) {
            throw new IllegalArgumentException(
                    "El orden debe estar entre " + PoliticasAcademicas.MIN_ORDEN_MATERIA
                    + " y " + PoliticasAcademicas.MAX_ORDEN_MATERIA
            );
        }
    }

    // Getters
    public PlanEstudioMateriaPk getIdPlanEstudioMateria() {
        return idPlanEstudioMateria;
    }

    public Integer getSemestrePlanEstudioMateria() {
        return semestrePlanEstudioMateria;
    }

    public Integer getOrdenPlanEstudioMateria() {
        return ordenPlanEstudioMateria;
    }

    // Getters de conveniencia (Ley de Demeter)
    public Long getIdPlanEstudio() {
        return idPlanEstudioMateria.getIdPlanEstudio();
    }

    public Integer getIdMateria() {
        return idPlanEstudioMateria.getIdMateria();
    }

    public PlanEstudio getPlanEstudio() {
        return idPlanEstudioMateria.getPlanEstudio();
    }

    public Materia getMateria() {
        return idPlanEstudioMateria.getMateria();
    }
}
