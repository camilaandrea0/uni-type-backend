package edu.unimag.servicio;

import edu.unimag.dto.planestudio.PlanEstudioActualizarDto;
import edu.unimag.dto.planestudio.PlanEstudioCrearDto;
import edu.unimag.dto.planestudio.PlanEstudioDto;
import edu.unimag.mapeador.PlanEstudioMapeador;
import edu.unimag.modelo.Facultad;
import edu.unimag.modelo.PlanEstudio;
import edu.unimag.modelo.Programa;
import edu.unimag.repositorio.FacultadRepositorio;
import edu.unimag.repositorio.PlanEstudioRepositorio;
import edu.unimag.repositorio.ProgramaRepositorio;
import edu.unimag.repositorio.cargador.FacultadCargador;
import edu.unimag.repositorio.cargador.ProgramaCargador;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlanEstudioServicio {

    private final PlanEstudioRepositorio planEstudioRepositorio;
    private final ProgramaRepositorio programaRepositorio;
    private final FacultadRepositorio facultadRepositorio;
    private final PlanEstudioMapeador mapeador;

    public PlanEstudioServicio(
            PlanEstudioRepositorio planEstudioRepositorio,
            ProgramaRepositorio programaRepositorio,
            FacultadRepositorio facultadRepositorio,
            PlanEstudioMapeador mapeador
    ) {
        if (planEstudioRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de planes de estudio no puede ser nulo");
        }
        if (programaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de programas no puede ser nulo");
        }
        if (facultadRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de facultades no puede ser nulo");
        }
        if (mapeador == null) {
            throw new IllegalArgumentException("El mapeador no puede ser nulo");
        }

        this.planEstudioRepositorio = planEstudioRepositorio;
        this.programaRepositorio = programaRepositorio;
        this.facultadRepositorio = facultadRepositorio;
        this.mapeador = mapeador;
    }

    public PlanEstudioDto registrar(PlanEstudioCrearDto dto) {
        Programa programa = programaRepositorio.buscarPorId(dto.idPrograma())
                .orElseThrow(() -> new IllegalArgumentException("No se encontro programa con ID: " + dto.idPrograma()));

        PlanEstudio nuevoPlan = new PlanEstudio(dto.version(), programa);
        planEstudioRepositorio.guardar(nuevoPlan);

        return hidratarYMapearUnico(nuevoPlan, programa);
    }

    public PlanEstudioDto actualizar(PlanEstudioActualizarDto dto) {
        PlanEstudio plan = planEstudioRepositorio.buscarPorId(dto.id())
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe plan de estudio con ID: " + dto.id()
        ));

        plan.actualizarVersion(dto.nuevaVersion());

        planEstudioRepositorio.actualizar(plan);
        return hidratarYMapearUnico(plan);
    }

    public PlanEstudioDto activar(Long idPlanEstudio) {
        PlanEstudio plan = planEstudioRepositorio.buscarPorId(idPlanEstudio)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe plan de estudio con ID: " + idPlanEstudio
        ));

        plan.activar();
        PlanEstudio actualizado = planEstudioRepositorio.actualizar(plan);
        return hidratarYMapearUnico(actualizado);
    }

    public PlanEstudioDto inactivar(Long idPlanEstudio) {
        PlanEstudio plan = planEstudioRepositorio.buscarPorId(idPlanEstudio)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe plan de estudio con ID: " + idPlanEstudio
        ));

        plan.inactivar();

        PlanEstudio actualizado = planEstudioRepositorio.actualizar(plan);
        return hidratarYMapearUnico(actualizado);
    }

    public List<PlanEstudioDto> listarTodos() {
        List<PlanEstudio> planes = planEstudioRepositorio.listarTodos();

        if (planes.isEmpty()) {
            return List.of();
        }

        Set<UUID> idsProgramas = new HashSet<>();
        for (PlanEstudio plan : planes) {
            idsProgramas.add(plan.getIdPrograma());
        }

        ProgramaCargador programaCargador = new ProgramaCargador(programaRepositorio);
        programaCargador.cargarPorIds(new ArrayList<>(idsProgramas));

        Set<Long> idsFacultades = new HashSet<>();
        for (UUID idPrograma : idsProgramas) {
            programaCargador.obtener(idPrograma).ifPresent(programa
                    -> idsFacultades.add(programa.getIdFacultad())
            );
        }

        FacultadCargador facultadCargador = new FacultadCargador(facultadRepositorio);
        facultadCargador.cargarPorIds(new ArrayList<>(idsFacultades));

        return mapeador.toDtoList(planes, programaCargador, facultadCargador);
    }

    public PlanEstudioDto buscarPorId(Long idPlanEstudio) {
        PlanEstudio plan = planEstudioRepositorio.buscarPorId(idPlanEstudio)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe plan de estudio con ID: " + idPlanEstudio
        ));

        return hidratarYMapearUnico(plan);
    }

    public List<PlanEstudioDto> listarPorPrograma(UUID idPrograma) {
        Programa programa = programaRepositorio.buscarPorId(idPrograma)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro programa con ID: " + idPrograma
        ));

        List<PlanEstudio> todos = planEstudioRepositorio.listarTodos();
        List<PlanEstudio> delPrograma = new ArrayList<>();

        for (PlanEstudio plan : todos) {
            if (plan.getIdPrograma().equals(idPrograma)) {
                delPrograma.add(plan);
            }
        }

        if (delPrograma.isEmpty()) {
            return List.of();
        }

        // Este es un truco que les voy a pedir en el proyecto final: Ordenar por fecha
        delPrograma.sort(Comparator.comparing(PlanEstudio::getFechaCreacionPlanEstudio).reversed());

        ProgramaCargador programaCargador = new ProgramaCargador(programaRepositorio);
        programaCargador.registrarEnCache(programa);

        Facultad facultad = facultadRepositorio.buscarPorId(programa.getIdFacultad())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Facultad no encontrada con ID: " + programa.getIdFacultad()
        ));

        FacultadCargador facultadCargador = new FacultadCargador(facultadRepositorio);
        facultadCargador.registrarEnCache(facultad);
        return mapeador.toDtoList(delPrograma, programaCargador, facultadCargador);
    }

    private PlanEstudioDto hidratarYMapearUnico(PlanEstudio plan) {
        Programa programa = programaRepositorio.buscarPorId(plan.getIdPrograma())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Programa no encontrado con ID: " + plan.getIdPrograma()
        ));

        return hidratarYMapearUnico(plan, programa);
    }

    private PlanEstudioDto hidratarYMapearUnico(PlanEstudio plan, Programa programa) {
        Facultad facultad = facultadRepositorio.buscarPorId(programa.getIdFacultad())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Facultad no encontrada con ID: " + programa.getIdFacultad()
        ));

        return hidratarYMapearUnico(plan, programa, facultad);
    }

    private PlanEstudioDto hidratarYMapearUnico(PlanEstudio plan, Programa programa, Facultad facultad) {
        ProgramaCargador programaCargador = new ProgramaCargador(programaRepositorio);
        programaCargador.registrarEnCache(programa);

        FacultadCargador facultadCargador = new FacultadCargador(facultadRepositorio);
        facultadCargador.registrarEnCache(facultad);

        return mapeador.toDto(plan, programaCargador, facultadCargador);
    }
}
