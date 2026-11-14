package edu.unimag.servicio;

import edu.unimag.dto.planestudiomateria.PlanEstudioMateriaActualizarDto;
import edu.unimag.dto.planestudiomateria.PlanEstudioMateriaCrearDto;
import edu.unimag.dto.planestudiomateria.PlanEstudioMateriaDto;
import edu.unimag.mapeador.MapeadorPlanEstudioMateria;
import edu.unimag.modelo.Materia;
import edu.unimag.modelo.PlanEstudio;
import edu.unimag.modelo.PlanEstudioMateria;
import edu.unimag.modelo.pk.PlanEstudioMateriaPk;
import edu.unimag.repositorio.FacultadRepositorio;
import edu.unimag.repositorio.MateriaRepositorio;
import edu.unimag.repositorio.PlanEstudioMateriaRepositorio;
import edu.unimag.repositorio.PlanEstudioRepositorio;
import edu.unimag.repositorio.ProgramaRepositorio;
import edu.unimag.repositorio.cargador.FacultadCargador;
import edu.unimag.repositorio.cargador.MateriaCargador;
import edu.unimag.repositorio.cargador.PlanEstudioCargador;
import edu.unimag.repositorio.cargador.ProgramaCargador;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class PlanEstudioMateriaServicio {

    private final PlanEstudioMateriaRepositorio repositorio;
    private final PlanEstudioRepositorio planEstudioRepositorio;
    private final MateriaRepositorio materiaRepositorio;
    private final ProgramaRepositorio programaRepositorio;
    private final FacultadRepositorio facultadRepositorio;
    private final MapeadorPlanEstudioMateria mapeador;

    public PlanEstudioMateriaServicio(
            PlanEstudioMateriaRepositorio repositorio,
            PlanEstudioRepositorio planEstudioRepositorio,
            MateriaRepositorio materiaRepositorio,
            ProgramaRepositorio programaRepositorio,
            FacultadRepositorio facultadRepositorio,
            MapeadorPlanEstudioMateria mapeador
    ) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de plan-materia no puede ser nulo");
        }
        if (planEstudioRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de planes de estudio no puede ser nulo");
        }
        if (materiaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de materias no puede ser nulo");
        }
        if (programaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de programas no puede ser nulo");
        }
        if (facultadRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de facultades no puede ser nulo");
        }
        if (mapeador == null) {
            throw new IllegalArgumentException("El mapeador de plan-materia no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.planEstudioRepositorio = planEstudioRepositorio;
        this.materiaRepositorio = materiaRepositorio;
        this.programaRepositorio = programaRepositorio;
        this.facultadRepositorio = facultadRepositorio;
        this.mapeador = mapeador;
    }

    // ------------------------------------------------------------------
    // Operaciones de negocio
    // ------------------------------------------------------------------
    public PlanEstudioMateriaDto asignar(PlanEstudioMateriaCrearDto dto) {
        PlanEstudio plan = planEstudioRepositorio.buscarPorId(dto.idPlanEstudio())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro plan de estudio con ID: " + dto.idPlanEstudio()
        ));
        Materia materia = materiaRepositorio.buscarPorId(dto.idMateria())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro materia con ID: " + dto.idMateria()
        ));

        if (planEstudioMateriaRepositorioContiene(plan.getIdPlanEstudio(), materia.getIdMateria())) {
            throw new IllegalStateException(
                    "La materia " + materia.getIdMateria()
                    + " ya esta asignada al plan " + plan.getIdPlanEstudio()
            );
        }

        PlanEstudioMateriaPk pk = new PlanEstudioMateriaPk(plan, materia);
        PlanEstudioMateria nueva = new PlanEstudioMateria(
                pk,
                dto.semestre(),
                dto.orden()
        );

        boolean insertado = repositorio.insertarSiNoExiste(nueva);
        if (!insertado) {
            throw new IllegalStateException(
                    "Ya existe la asignacion de la materia al plan de estudio indicado"
            );
        }
        return hidratarYMapearUnico(nueva);
    }

    public PlanEstudioMateriaDto actualizar(PlanEstudioMateriaActualizarDto dto) {
        PlanEstudioMateria asignacion = buscarAsignacion(dto.idPlanEstudio(), dto.idMateria());

        boolean huboCambio = false;
        if (dto.nuevoSemestre() != null
                && !dto.nuevoSemestre().equals(asignacion.getSemestrePlanEstudioMateria())) {
            asignacion.reubicarSemestre(dto.nuevoSemestre());
            huboCambio = true;
        }
        if (dto.nuevoOrden() != null
                && !dto.nuevoOrden().equals(asignacion.getOrdenPlanEstudioMateria())) {
            asignacion.actualizarOrden(dto.nuevoOrden());
            huboCambio = true;
        }
        if (!huboCambio) {
            throw new IllegalArgumentException(
                    "Los valores indicados son iguales a los actuales, no hay cambios que realizar"
            );
        }

        repositorio.actualizar(asignacion);
        return hidratarYMapearUnico(asignacion);
    }

    public void quitar(Long idPlanEstudio, Integer idMateria) {
        PlanEstudioMateria asignacion = buscarAsignacion(idPlanEstudio, idMateria);
        boolean eliminado = repositorio.eliminar(asignacion.getIdPlanEstudioMateria());
        if (!eliminado) {
            throw new IllegalStateException(
                    "No fue posible quitar la materia del plan de estudio indicado"
            );
        }
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------
    public PlanEstudioMateriaDto buscarPorId(Long idPlanEstudio, Integer idMateria) {
        return hidratarYMapearUnico(buscarAsignacion(idPlanEstudio, idMateria));
    }

    public List<PlanEstudioMateriaDto> listarTodos() {
        List<PlanEstudioMateria> asignaciones = ordenar(repositorio.listarTodos());
        return hidratarYMapearLista(asignaciones);
    }

    public List<PlanEstudioMateriaDto> listarPorPlan(Long idPlanEstudio) {
        if (planEstudioRepositorio.buscarPorId(idPlanEstudio).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe plan de estudio con ID: " + idPlanEstudio
            );
        }
        List<PlanEstudioMateria> asignaciones = ordenar(
                repositorio.listarPorPlanEstudio(idPlanEstudio)
        );
        return hidratarYMapearLista(asignaciones);
    }

    public List<PlanEstudioMateriaDto> listarPorMateria(Integer idMateria) {
        if (materiaRepositorio.buscarPorId(idMateria).isEmpty()) {
            throw new IllegalArgumentException("No existe materia con ID: " + idMateria);
        }
        List<PlanEstudioMateria> asignaciones = ordenar(repositorio.listarPorMateria(idMateria));
        return hidratarYMapearLista(asignaciones);
    }

    // ------------------------------------------------------------------
    // Apoyo
    // ------------------------------------------------------------------
    private boolean planEstudioMateriaRepositorioContiene(Long idPlanEstudio, Integer idMateria) {
        for (PlanEstudioMateria asignacion : repositorio.listarTodos()) {
            if (asignacion.getIdPlanEstudio().equals(idPlanEstudio)
                    && asignacion.getIdMateria().equals(idMateria)) {
                return true;
            }
        }
        return false;
    }

    private PlanEstudioMateria buscarAsignacion(Long idPlanEstudio, Integer idMateria) {
        PlanEstudio plan = planEstudioRepositorio.buscarPorId(idPlanEstudio)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro plan de estudio con ID: " + idPlanEstudio
        ));
        Materia materia = materiaRepositorio.buscarPorId(idMateria)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro materia con ID: " + idMateria
        ));
        PlanEstudioMateriaPk pk = new PlanEstudioMateriaPk(plan, materia);
        return repositorio.buscarPorId(pk)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe la asignacion de la materia " + idMateria
                + " al plan " + idPlanEstudio
        ));
    }

    private List<PlanEstudioMateria> ordenar(List<PlanEstudioMateria> asignaciones) {
        List<PlanEstudioMateria> ordenadas = new ArrayList<>(asignaciones);
        ordenadas.sort(
                Comparator.comparing(
                        PlanEstudioMateria::getSemestrePlanEstudioMateria,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ).thenComparing(
                        PlanEstudioMateria::getOrdenPlanEstudioMateria,
                        Comparator.nullsLast(Comparator.naturalOrder())
                )
        );
        return ordenadas;
    }

    private List<PlanEstudioMateriaDto> hidratarYMapearLista(List<PlanEstudioMateria> asignaciones) {
        if (asignaciones == null || asignaciones.isEmpty()) {
            return List.of();
        }

        Set<Long> idsPlanes = new HashSet<>();
        Set<Integer> idsMaterias = new HashSet<>();
        for (PlanEstudioMateria asignacion : asignaciones) {
            idsPlanes.add(asignacion.getIdPlanEstudio());
            idsMaterias.add(asignacion.getIdMateria());
        }

        PlanEstudioCargador planCargador = new PlanEstudioCargador(planEstudioRepositorio);
        planCargador.cargarPorIds(new ArrayList<>(idsPlanes));

        Set<UUID> idsProgramas = new HashSet<>();
        for (Long idPlan : idsPlanes) {
            planCargador.obtener(idPlan).ifPresent(plan
                    -> idsProgramas.add(plan.getIdPrograma())
            );
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

        MateriaCargador materiaCargador = new MateriaCargador(materiaRepositorio);
        materiaCargador.cargarPorIds(new ArrayList<>(idsMaterias));

        return mapeador.toDtoList(
                asignaciones,
                planCargador,
                programaCargador,
                facultadCargador,
                materiaCargador
        );
    }

    private PlanEstudioMateriaDto hidratarYMapearUnico(PlanEstudioMateria asignacion) {
        List<PlanEstudioMateriaDto> resultado = hidratarYMapearLista(List.of(asignacion));
        if (resultado.isEmpty()) {
            throw new IllegalStateException(
                    "Error de integridad al hidratar la asignacion del plan "
                    + asignacion.getIdPlanEstudio() + " y la materia " + asignacion.getIdMateria()
            );
        }
        return resultado.get(0);
    }
}
