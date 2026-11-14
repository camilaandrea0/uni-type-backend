package edu.unimag.controlador;

import edu.unimag.dto.materia.MateriaDto;
import edu.unimag.dto.planestudio.PlanEstudioDto;
import edu.unimag.dto.planestudiomateria.PlanEstudioMateriaActualizarDto;
import edu.unimag.dto.planestudiomateria.PlanEstudioMateriaCrearDto;
import edu.unimag.dto.planestudiomateria.PlanEstudioMateriaDto;
import edu.unimag.servicio.MateriaServicio;
import edu.unimag.servicio.PlanEstudioMateriaServicio;
import edu.unimag.servicio.PlanEstudioServicio;
import java.util.List;

public class ControladorPlanEstudioMateria {

    private final PlanEstudioMateriaServicio servicio;
    private final PlanEstudioServicio planEstudioServicio;
    private final MateriaServicio materiaServicio;

    public ControladorPlanEstudioMateria(
            PlanEstudioMateriaServicio servicio,
            PlanEstudioServicio planEstudioServicio,
            MateriaServicio materiaServicio
    ) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio de plan-materia no puede ser nulo");
        }
        if (planEstudioServicio == null) {
            throw new IllegalArgumentException("El servicio de planes de estudio no puede ser nulo");
        }
        if (materiaServicio == null) {
            throw new IllegalArgumentException("El servicio de materias no puede ser nulo");
        }
        this.servicio = servicio;
        this.planEstudioServicio = planEstudioServicio;
        this.materiaServicio = materiaServicio;
    }

    public PlanEstudioMateriaDto asignar(PlanEstudioMateriaCrearDto dto) {
        return servicio.asignar(dto);
    }

    public PlanEstudioMateriaDto actualizar(PlanEstudioMateriaActualizarDto dto) {
        return servicio.actualizar(dto);
    }

    public void quitar(Long idPlanEstudio, Integer idMateria) {
        servicio.quitar(idPlanEstudio, idMateria);
    }

    public PlanEstudioMateriaDto buscarPorId(Long idPlanEstudio, Integer idMateria) {
        return servicio.buscarPorId(idPlanEstudio, idMateria);
    }

    public List<PlanEstudioMateriaDto> listarTodos() {
        return servicio.listarTodos();
    }

    public List<PlanEstudioMateriaDto> listarPorPlan(Long idPlanEstudio) {
        return servicio.listarPorPlan(idPlanEstudio);
    }

    public List<PlanEstudioMateriaDto> listarPorMateria(Integer idMateria) {
        return servicio.listarPorMateria(idMateria);
    }

    public PlanEstudioDto buscarPlanEstudio(Long idPlanEstudio) {
        return planEstudioServicio.buscarPorId(idPlanEstudio);
    }

    public MateriaDto buscarMateria(Integer idMateria) {
        return materiaServicio.buscarPorId(idMateria);
    }
}
