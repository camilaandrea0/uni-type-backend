package edu.unimag.controlador;

import edu.unimag.dto.planestudio.PlanEstudioActualizarDto;
import edu.unimag.dto.planestudio.PlanEstudioCrearDto;
import edu.unimag.dto.planestudio.PlanEstudioDto;
import edu.unimag.dto.programa.ProgramaDto;
import edu.unimag.servicio.PlanEstudioServicio;
import edu.unimag.servicio.ProgramaServicio;

import java.util.List;
import java.util.UUID;

public class PlanEstudioControlador {

    private final PlanEstudioServicio servicio;
    private final ProgramaServicio programaServicio;

    public PlanEstudioControlador(
            PlanEstudioServicio servicio,
            ProgramaServicio programaServicio
    ) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio de planes de estudio no puede ser nulo");
        }
        if (programaServicio == null) {
            throw new IllegalArgumentException("El servicio de programas no puede ser nulo");
        }
        this.servicio = servicio;
        this.programaServicio = programaServicio;
    }

    public PlanEstudioDto registrar(PlanEstudioCrearDto request) {
        return servicio.registrar(request);
    }

    public PlanEstudioDto actualizar(PlanEstudioActualizarDto request) {
        return servicio.actualizar(request);
    }

    public PlanEstudioDto activar(Long idPlanEstudio) {
        return servicio.activar(idPlanEstudio);
    }

    public PlanEstudioDto inactivar(Long idPlanEstudio) {
        return servicio.inactivar(idPlanEstudio);
    }

    public List<PlanEstudioDto> listarTodos() {
        return servicio.listarTodos();
    }

    public PlanEstudioDto buscarPorId(Long idPlanEstudio) {
        return servicio.buscarPorId(idPlanEstudio);
    }

    public List<PlanEstudioDto> listarPorPrograma(UUID idPrograma) {
        return servicio.listarPorPrograma(idPrograma);
    }

    public ProgramaDto buscarPrograma(UUID idPrograma) {
        return programaServicio.buscarPorId(idPrograma);
    }

}
