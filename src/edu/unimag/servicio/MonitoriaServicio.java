package edu.unimag.servicio;

import edu.unimag.dto.monitoria.MonitoriaCrearDto;
import edu.unimag.dto.monitoria.MonitoriaDto;
import edu.unimag.dto.monitoria.MonitoriaEvaluarDto;
import edu.unimag.mapeador.MapeadorMonitoria;
import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.modelo.Estudiante;
import edu.unimag.modelo.Monitoria;
import edu.unimag.modelo.Profesor;
import edu.unimag.modelo.pk.MonitoriaPk;
import edu.unimag.repositorio.CursoPeriodoRepositorio;
import edu.unimag.repositorio.EstudianteRepositorio;
import edu.unimag.repositorio.MateriaRepositorio;
import edu.unimag.repositorio.MonitoriaRepositorio;
import edu.unimag.repositorio.PeriodoAcademicoRepositorio;
import edu.unimag.repositorio.ProfesorRepositorio;
import edu.unimag.repositorio.cargador.CursoPeriodoCargador;
import edu.unimag.repositorio.cargador.EstudianteCargador;
import edu.unimag.repositorio.cargador.MateriaCargador;
import edu.unimag.repositorio.cargador.PeriodoAcademicoCargador;
import edu.unimag.repositorio.cargador.ProfesorCargador;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class MonitoriaServicio {

    private final MonitoriaRepositorio repositorio;
    private final CursoPeriodoRepositorio cursoPeriodoRepositorio;
    private final EstudianteRepositorio estudianteRepositorio;
    private final ProfesorRepositorio profesorRepositorio;
    private final MateriaRepositorio materiaRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;
    private final MapeadorMonitoria mapeador;

    public MonitoriaServicio(
            MonitoriaRepositorio repositorio,
            CursoPeriodoRepositorio cursoPeriodoRepositorio,
            EstudianteRepositorio estudianteRepositorio,
            ProfesorRepositorio profesorRepositorio,
            MateriaRepositorio materiaRepositorio,
            PeriodoAcademicoRepositorio periodoAcademicoRepositorio,
            MapeadorMonitoria mapeador
    ) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de monitorias no puede ser nulo");
        }
        if (cursoPeriodoRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de cursos periodo no puede ser nulo");
        }
        if (estudianteRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de estudiantes no puede ser nulo");
        }
        if (profesorRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de profesores no puede ser nulo");
        }
        if (materiaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de materias no puede ser nulo");
        }
        if (periodoAcademicoRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de periodos academicos no puede ser nulo");
        }
        if (mapeador == null) {
            throw new IllegalArgumentException("El mapeador de monitorias no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.cursoPeriodoRepositorio = cursoPeriodoRepositorio;
        this.estudianteRepositorio = estudianteRepositorio;
        this.profesorRepositorio = profesorRepositorio;
        this.materiaRepositorio = materiaRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
        this.mapeador = mapeador;
    }

    // ------------------------------------------------------------------
    // Operaciones de negocio
    // ------------------------------------------------------------------
    public MonitoriaDto crear(MonitoriaCrearDto dto) {
        Estudiante estudiante = estudianteRepositorio.buscarPorId(dto.idEstudiante())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro estudiante con ID: " + dto.idEstudiante()
        ));
        if (!estudiante.estaActivo()) {
            throw new IllegalStateException("El estudiante no esta activo y no puede ser monitor");
        }

        Profesor profesor = profesorRepositorio.buscarPorId(dto.idProfesor())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro profesor con ID: " + dto.idProfesor()
        ));
        if (!profesor.estaActivo()) {
            throw new IllegalStateException("El profesor no esta activo y no puede supervisar monitorias");
        }

        CursoPeriodo curso = cursoPeriodoRepositorio.buscarPorId(dto.idCursoPeriodo())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro curso periodo con ID: " + dto.idCursoPeriodo()
        ));

        MonitoriaPk pk = new MonitoriaPk(curso, estudiante, profesor);
        if (repositorio.buscarPorId(pk).isPresent()) {
            throw new IllegalStateException(
                    "Ya existe una monitoria para el estudiante y curso indicados con ese profesor"
            );
        }

        Monitoria nueva = new Monitoria(pk);
        boolean insertado = repositorio.insertarSiNoExiste(nueva);
        if (!insertado) {
            throw new IllegalStateException(
                    "Ya existe una monitoria para el estudiante, curso y profesor indicados"
            );
        }
        return hidratarYMapearUnico(nueva);
    }

    public MonitoriaDto evaluar(MonitoriaEvaluarDto dto) {
        Monitoria monitoria = buscarMonitoria(
                dto.idCursoPeriodo(),
                dto.idEstudiante(),
                dto.idProfesor()
        );
        monitoria.evaluar(dto.calificacion(), dto.comentario());
        repositorio.actualizar(monitoria);
        return hidratarYMapearUnico(monitoria);
    }

    public void eliminar(UUID idCursoPeriodo, UUID idEstudiante, Long idProfesor) {
        Monitoria monitoria = buscarMonitoria(idCursoPeriodo, idEstudiante, idProfesor);
        boolean eliminado = repositorio.eliminar(monitoria.getIdMonitoria());
        if (!eliminado) {
            throw new IllegalStateException(
                    "No fue posible eliminar la monitoria indicada"
            );
        }
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------
    public MonitoriaDto buscarPorId(UUID idCursoPeriodo, UUID idEstudiante, Long idProfesor) {
        return hidratarYMapearUnico(
                buscarMonitoria(idCursoPeriodo, idEstudiante, idProfesor)
        );
    }

    public List<MonitoriaDto> listarTodos() {
        List<Monitoria> monitorias = ordenar(repositorio.listarTodos());
        return hidratarYMapearLista(monitorias);
    }

    public List<MonitoriaDto> listarPorCursoPeriodo(UUID idCursoPeriodo) {
        if (cursoPeriodoRepositorio.buscarPorId(idCursoPeriodo).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe curso periodo con ID: " + idCursoPeriodo
            );
        }
        List<Monitoria> monitorias = ordenar(repositorio.listarPorCursoPeriodo(idCursoPeriodo));
        return hidratarYMapearLista(monitorias);
    }

    public List<MonitoriaDto> listarPorEstudiante(UUID idEstudiante) {
        if (estudianteRepositorio.buscarPorId(idEstudiante).isEmpty()) {
            throw new IllegalArgumentException(
                    "No existe estudiante con ID: " + idEstudiante
            );
        }
        List<Monitoria> monitorias = ordenar(repositorio.listarPorEstudiante(idEstudiante));
        return hidratarYMapearLista(monitorias);
    }

    public List<MonitoriaDto> listarPorProfesor(Long idProfesor) {
        if (profesorRepositorio.buscarPorId(idProfesor).isEmpty()) {
            throw new IllegalArgumentException("No existe profesor con ID: " + idProfesor);
        }
        List<Monitoria> monitorias = ordenar(repositorio.listarPorProfesor(idProfesor));
        return hidratarYMapearLista(monitorias);
    }

    // ------------------------------------------------------------------
    // Apoyo
    // ------------------------------------------------------------------
    private Monitoria buscarMonitoria(UUID idCursoPeriodo, UUID idEstudiante, Long idProfesor) {
        CursoPeriodo curso = cursoPeriodoRepositorio.buscarPorId(idCursoPeriodo)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro curso periodo con ID: " + idCursoPeriodo
        ));
        Estudiante estudiante = estudianteRepositorio.buscarPorId(idEstudiante)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro estudiante con ID: " + idEstudiante
        ));
        Profesor profesor = profesorRepositorio.buscarPorId(idProfesor)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro profesor con ID: " + idProfesor
        ));

        MonitoriaPk pk = new MonitoriaPk(curso, estudiante, profesor);
        return repositorio.buscarPorId(pk)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe la monitoria del estudiante " + idEstudiante
                + " en el curso " + idCursoPeriodo
                + " con el profesor " + idProfesor
        ));
    }

    private List<Monitoria> ordenar(List<Monitoria> monitorias) {
        List<Monitoria> ordenadas = new ArrayList<>(monitorias);
        ordenadas.sort(Comparator.comparing(
                Monitoria::getFechaCreacionMonitoria,
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed());
        return ordenadas;
    }

    private List<MonitoriaDto> hidratarYMapearLista(List<Monitoria> monitorias) {
        if (monitorias == null || monitorias.isEmpty()) {
            return List.of();
        }

        Set<UUID> idsCursos = new HashSet<>();
        Set<UUID> idsEstudiantes = new HashSet<>();
        Set<Long> idsProfesores = new HashSet<>();
        for (Monitoria monitoria : monitorias) {
            idsCursos.add(monitoria.getIdCursoPeriodo());
            idsEstudiantes.add(monitoria.getIdEstudiante());
            idsProfesores.add(monitoria.getIdProfesor());
        }

        CursoPeriodoCargador cursoCargador = new CursoPeriodoCargador(cursoPeriodoRepositorio);
        cursoCargador.cargarPorIds(new ArrayList<>(idsCursos));

        EstudianteCargador estudianteCargador = new EstudianteCargador(estudianteRepositorio);
        estudianteCargador.cargarPorIds(new ArrayList<>(idsEstudiantes));

        ProfesorCargador profesorCargador = new ProfesorCargador(profesorRepositorio);
        profesorCargador.cargarPorIds(new ArrayList<>(idsProfesores));

        Set<Integer> idsMaterias = new HashSet<>();
        Set<Integer> idsPeriodos = new HashSet<>();
        for (UUID idCurso : idsCursos) {
            cursoCargador.obtener(idCurso).ifPresent(curso -> {
                idsMaterias.add(curso.getIdMateria());
                idsPeriodos.add(curso.getIdPeriodoAcademico());
            });
        }

        MateriaCargador materiaCargador = new MateriaCargador(materiaRepositorio);
        materiaCargador.cargarPorIds(new ArrayList<>(idsMaterias));

        PeriodoAcademicoCargador periodoCargador = new PeriodoAcademicoCargador(periodoAcademicoRepositorio);
        periodoCargador.cargarPorIds(new ArrayList<>(idsPeriodos));

        return mapeador.toDtoList(
                monitorias,
                cursoCargador,
                materiaCargador,
                periodoCargador,
                estudianteCargador,
                profesorCargador
        );
    }

    private MonitoriaDto hidratarYMapearUnico(Monitoria monitoria) {
        List<MonitoriaDto> resultado = hidratarYMapearLista(List.of(monitoria));
        if (resultado.isEmpty()) {
            throw new IllegalStateException(
                    "Error de integridad al hidratar la monitoria del estudiante "
                    + monitoria.getIdEstudiante() + " en el curso " + monitoria.getIdCursoPeriodo()
            );
        }
        return resultado.get(0);
    }
}
