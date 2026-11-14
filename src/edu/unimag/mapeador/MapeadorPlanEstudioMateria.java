package edu.unimag.mapeador;

import edu.unimag.dto.materia.MateriaDto;
import edu.unimag.dto.planestudio.PlanEstudioDto;
import edu.unimag.dto.planestudiomateria.PlanEstudioMateriaDto;
import edu.unimag.modelo.Materia;
import edu.unimag.modelo.PlanEstudio;
import edu.unimag.modelo.PlanEstudioMateria;
import edu.unimag.modelo.Programa;
import edu.unimag.repositorio.cargador.FacultadCargador;
import edu.unimag.repositorio.cargador.MateriaCargador;
import edu.unimag.repositorio.cargador.PlanEstudioCargador;
import edu.unimag.repositorio.cargador.ProgramaCargador;
import java.util.ArrayList;
import java.util.List;

public class MapeadorPlanEstudioMateria {

    private final PlanEstudioMapeador planEstudioMapeador;
    private final MateriaMapeador materiaMapeador;

    public MapeadorPlanEstudioMateria(
            PlanEstudioMapeador planEstudioMapeador,
            MateriaMapeador materiaMapeador
    ) {
        if (planEstudioMapeador == null) {
            throw new IllegalArgumentException("El mapeador de planes de estudio no puede ser nulo");
        }
        if (materiaMapeador == null) {
            throw new IllegalArgumentException("El mapeador de materias no puede ser nulo");
        }
        this.planEstudioMapeador = planEstudioMapeador;
        this.materiaMapeador = materiaMapeador;
    }

    public List<PlanEstudioMateriaDto> toDtoList(
            List<PlanEstudioMateria> entidades,
            PlanEstudioCargador planCargador,
            ProgramaCargador programaCargador,
            FacultadCargador facultadCargador,
            MateriaCargador materiaCargador
    ) {
        if (entidades == null || entidades.isEmpty()) {
            return List.of();
        }
        validarCargadores(planCargador, programaCargador, facultadCargador, materiaCargador);

        List<PlanEstudioMateriaDto> resultado = new ArrayList<>(entidades.size());
        for (PlanEstudioMateria entidad : entidades) {
            if (!validarIntegridad(entidad, planCargador, programaCargador,
                    facultadCargador, materiaCargador)) {
                continue;
            }
            resultado.add(transformarADto(entidad, planCargador, programaCargador,
                    facultadCargador, materiaCargador));
        }
        return resultado;
    }

    public PlanEstudioMateriaDto toDto(
            PlanEstudioMateria entidad,
            PlanEstudioCargador planCargador,
            ProgramaCargador programaCargador,
            FacultadCargador facultadCargador,
            MateriaCargador materiaCargador
    ) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad plan estudio materia no puede ser nula");
        }
        validarCargadores(planCargador, programaCargador, facultadCargador, materiaCargador);

        if (!validarIntegridad(entidad, planCargador, programaCargador,
                facultadCargador, materiaCargador)) {
            throw new IllegalStateException(
                    "Error de integridad en la asignacion del plan " + entidad.getIdPlanEstudio()
                    + " y la materia " + entidad.getIdMateria()
            );
        }
        return transformarADto(entidad, planCargador, programaCargador,
                facultadCargador, materiaCargador);
    }

    private void validarCargadores(
            PlanEstudioCargador planCargador,
            ProgramaCargador programaCargador,
            FacultadCargador facultadCargador,
            MateriaCargador materiaCargador
    ) {
        if (planCargador == null) {
            throw new IllegalArgumentException("El cargador de planes de estudio no puede ser nulo");
        }
        if (programaCargador == null) {
            throw new IllegalArgumentException("El cargador de programas no puede ser nulo");
        }
        if (facultadCargador == null) {
            throw new IllegalArgumentException("El cargador de facultades no puede ser nulo");
        }
        if (materiaCargador == null) {
            throw new IllegalArgumentException("El cargador de materias no puede ser nulo");
        }
    }

    private boolean validarIntegridad(
            PlanEstudioMateria entidad,
            PlanEstudioCargador planCargador,
            ProgramaCargador programaCargador,
            FacultadCargador facultadCargador,
            MateriaCargador materiaCargador
    ) {
        if (!planCargador.existe(entidad.getIdPlanEstudio())) {
            return false;
        }
        if (!materiaCargador.existe(entidad.getIdMateria())) {
            return false;
        }
        PlanEstudio plan = planCargador.obtener(entidad.getIdPlanEstudio()).orElse(null);
        if (plan == null) {
            return false;
        }
        if (!programaCargador.existe(plan.getIdPrograma())) {
            return false;
        }
        Programa programa = programaCargador.obtener(plan.getIdPrograma()).orElse(null);
        if (programa == null) {
            return false;
        }
        return facultadCargador.existe(programa.getIdFacultad());
    }

    private PlanEstudioMateriaDto transformarADto(
            PlanEstudioMateria entidad,
            PlanEstudioCargador planCargador,
            ProgramaCargador programaCargador,
            FacultadCargador facultadCargador,
            MateriaCargador materiaCargador
    ) {
        PlanEstudio plan = planCargador.obtener(entidad.getIdPlanEstudio())
                .orElseThrow(() -> new IllegalStateException("Error de integridad: plan no encontrado"));
        Materia materia = materiaCargador.obtener(entidad.getIdMateria())
                .orElseThrow(() -> new IllegalStateException("Error de integridad: materia no encontrada"));

        PlanEstudioDto planDto = planEstudioMapeador.toDto(plan, programaCargador, facultadCargador);
        MateriaDto materiaDto = materiaMapeador.toDto(materia);

        return new PlanEstudioMateriaDto(
                planDto,
                materiaDto,
                entidad.getSemestrePlanEstudioMateria(),
                entidad.getOrdenPlanEstudioMateria()
        );
    }
}
