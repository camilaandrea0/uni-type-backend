package edu.unimag.servicio;

import edu.unimag.dto.cursoestudiante.CursoEstudianteCrearDto;
import edu.unimag.dto.cursoestudiante.CursoEstudianteDto;
import edu.unimag.dto.cursoestudiante.CursoEstudianteEvaluarDto;
import edu.unimag.mapeador.MapeadorCursoEstudiante;
import edu.unimag.modelo.CursoEstudiante;
import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.modelo.Estudiante;
import edu.unimag.modelo.pk.CursoEstudiantePk;
import edu.unimag.repositorio.CursoEstudianteRepositorio;
import edu.unimag.repositorio.CursoPeriodoRepositorio;
import edu.unimag.repositorio.EstudianteRepositorio;
import edu.unimag.repositorio.MateriaRepositorio;
import edu.unimag.repositorio.PeriodoAcademicoRepositorio;
import edu.unimag.repositorio.cargador.CursoPeriodoCargador;
import edu.unimag.repositorio.cargador.EstudianteCargador;
import edu.unimag.repositorio.cargador.MateriaCargador;
import edu.unimag.repositorio.cargador.PeriodoAcademicoCargador;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CursoEstudianteServicio {

    private final CursoEstudianteRepositorio repositorio;
    private final EstudianteRepositorio estudianteRepositorio;
    private final CursoPeriodoRepositorio cursoPeriodoRepositorio;
    private final MateriaRepositorio materiaRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;
    private final MapeadorCursoEstudiante mapeador;

    public CursoEstudianteServicio(
            CursoEstudianteRepositorio repositorio,
            EstudianteRepositorio estudianteRepositorio,
            CursoPeriodoRepositorio cursoPeriodoRepositorio,
            MateriaRepositorio materiaRepositorio,
            PeriodoAcademicoRepositorio periodoAcademicoRepositorio,
            MapeadorCursoEstudiante mapeador
    ) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de inscripciones no puede ser nulo");
        }
        if (estudianteRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de estudiantes no puede ser nulo");
        }
        if (cursoPeriodoRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de cursos periodo no puede ser nulo");
        }
        if (materiaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de materias no puede ser nulo");
        }
        if (periodoAcademicoRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de periodos academicos no puede ser nulo");
        }
        if (mapeador == null) {
            throw new IllegalArgumentException("El mapeador de inscripciones no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.estudianteRepositorio = estudianteRepositorio;
        this.cursoPeriodoRepositorio = cursoPeriodoRepositorio;
        this.materiaRepositorio = materiaRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
        this.mapeador = mapeador;
    }

    // ------------------------------------------------------------------
    // Operaciones de negocio
    // ------------------------------------------------------------------
    public CursoEstudianteDto inscribir(CursoEstudianteCrearDto dto) {
        Estudiante estudiante = estudianteRepositorio.buscarPorId(dto.idEstudiante())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro estudiante con ID: " + dto.idEstudiante()
        ));
        if (!estudiante.estaActivo()) {
            throw new IllegalStateException("El estudiante no esta activo y no puede inscribirse");
        }

        CursoPeriodo curso = cursoPeriodoRepositorio.buscarPorId(dto.idCursoPeriodo())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro curso periodo con ID: " + dto.idCursoPeriodo()
        ));

        int ocupantes = repositorio.contarOcupantesPorCursoPeriodo(curso.getIdCursoPeriodo());
        if (!curso.tieneCupoDisponible(ocupantes)) {
            throw new IllegalStateException(
                    "El curso no tiene cupo disponible. Cupo: " + curso.getCupoCursoPeriodo()
                    + ", ocupantes: " + ocupantes
            );
        }

        CursoEstudiantePk pk = new CursoEstudiantePk(curso, estudiante);
        if (repositorio.buscarPorId(pk).isPresent()) {
            throw new IllegalStateException(
                    "El estudiante ya tiene una inscripcion en este curso periodo"
            );
        }

        CursoEstudiante nueva = new CursoEstudiante(pk);
        boolean insertado = repositorio.insertarSiNoExiste(nueva);
        if (!insertado) {
            throw new IllegalStateException(
                    "Ya existe una inscripcion para el estudiante y curso indicados"
            );
        }
        return hidratarYMapearUnico(nueva);
    }

    public CursoEstudianteDto admitir(UUID idEstudiante, UUID idCursoPeriodo) {
        CursoEstudiante inscripcion = buscarInscripcion(idEstudiante, idCursoPeriodo);
        inscripcion.admitir();
        repositorio.actualizar(inscripcion);
        return hidratarYMapearUnico(inscripcion);
    }

    public CursoEstudianteDto rechazar(UUID idEstudiante, UUID idCursoPeriodo) {
        CursoEstudiante inscripcion = buscarInscripcion(idEstudiante, idCursoPeriodo);
        inscripcion.rechazar();
        repositorio.actualizar(inscripcion);
        return hidratarYMapearUnico(inscripcion);
    }

    public CursoEstudianteDto retirar(UUID idEstudiante, UUID idCursoPeriodo) {
        CursoEstudiante inscripcion = buscarInscripcion(idEstudiante, idCursoPeriodo);
        inscripcion.retirar();
        repositorio.actualizar(inscripcion);
        return hidratarYMapearUnico(inscripcion);
    }

    public CursoEstudianteDto evaluar(CursoEstudianteEvaluarDto dto) {
        CursoEstudiante inscripcion = buscarInscripcion(dto.idEstudiante(), dto.idCursoPeriodo());
        inscripcion.evaluar(dto.calificacion(), dto.observacion());
        repositorio.actualizar(inscripcion);
        return hidratarYMapearUnico(inscripcion);
    }

    // ------------------------------------------------------------------
    // Consultas
    // ------------------------------------------------------------------
    public CursoEstudianteDto buscarPorId(UUID idEstudiante, UUID idCursoPeriodo) {
        return hidratarYMapearUnico(buscarInscripcion(idEstudiante, idCursoPeriodo));
    }

    public List<CursoEstudianteDto> listarTodos() {
        return hidratarYMapearLista(repositorio.listarTodos());
    }

    public List<CursoEstudianteDto> listarPorEstudiante(UUID idEstudiante) {
        if (estudianteRepositorio.buscarPorId(idEstudiante).isEmpty()) {
            throw new IllegalArgumentException("No existe estudiante con ID: " + idEstudiante);
        }
        List<CursoEstudiante> inscripciones = repositorio.listarPorEstudiante(idEstudiante);
        ordenarPorFechaDesc(inscripciones);
        return hidratarYMapearLista(inscripciones);
    }

    public List<CursoEstudianteDto> listarPorCursoPeriodo(UUID idCursoPeriodo) {
        if (cursoPeriodoRepositorio.buscarPorId(idCursoPeriodo).isEmpty()) {
            throw new IllegalArgumentException("No existe curso periodo con ID: " + idCursoPeriodo);
        }
        List<CursoEstudiante> inscripciones = repositorio.listarPorCursoPeriodo(idCursoPeriodo);
        ordenarPorFechaDesc(inscripciones);
        return hidratarYMapearLista(inscripciones);
    }

    public List<CursoEstudianteDto> listarPorMateria(Integer idMateria) {
        if (materiaRepositorio.buscarPorId(idMateria).isEmpty()) {
            throw new IllegalArgumentException("No existe materia con ID: " + idMateria);
        }
        Set<UUID> cursos = new HashSet<>();
        for (CursoPeriodo curso : cursoPeriodoRepositorio.listarTodos()) {
            if (curso.getIdMateria().equals(idMateria)) {
                cursos.add(curso.getIdCursoPeriodo());
            }
        }
        return listarFiltradoPorCursos(cursos);
    }

    public List<CursoEstudianteDto> listarPorMateriaYPeriodo(Integer idMateria, Integer idPeriodo) {
        if (materiaRepositorio.buscarPorId(idMateria).isEmpty()) {
            throw new IllegalArgumentException("No existe materia con ID: " + idMateria);
        }
        if (periodoAcademicoRepositorio.buscarPorId(idPeriodo).isEmpty()) {
            throw new IllegalArgumentException("No existe periodo academico con ID: " + idPeriodo);
        }
        Set<UUID> cursos = new HashSet<>();
        for (CursoPeriodo curso : cursoPeriodoRepositorio.listarTodos()) {
            if (curso.getIdMateria().equals(idMateria)
                    && curso.getIdPeriodoAcademico().equals(idPeriodo)) {
                cursos.add(curso.getIdCursoPeriodo());
            }
        }
        return listarFiltradoPorCursos(cursos);
    }

    public List<CursoEstudianteDto> listarPorPeriodo(Integer idPeriodo) {
        if (periodoAcademicoRepositorio.buscarPorId(idPeriodo).isEmpty()) {
            throw new IllegalArgumentException("No existe periodo academico con ID: " + idPeriodo);
        }
        Set<UUID> cursos = new HashSet<>();
        for (CursoPeriodo curso : cursoPeriodoRepositorio.listarTodos()) {
            if (curso.getIdPeriodoAcademico().equals(idPeriodo)) {
                cursos.add(curso.getIdCursoPeriodo());
            }
        }
        return listarFiltradoPorCursos(cursos);
    }

    // ------------------------------------------------------------------
    // Apoyo
    // ------------------------------------------------------------------
    private CursoEstudiante buscarInscripcion(UUID idEstudiante, UUID idCursoPeriodo) {
        Estudiante estudiante = estudianteRepositorio.buscarPorId(idEstudiante)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro estudiante con ID: " + idEstudiante
        ));
        CursoPeriodo curso = cursoPeriodoRepositorio.buscarPorId(idCursoPeriodo)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro curso periodo con ID: " + idCursoPeriodo
        ));
        CursoEstudiantePk pk = new CursoEstudiantePk(curso, estudiante);
        return repositorio.buscarPorId(pk)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe inscripcion del estudiante " + idEstudiante
                + " en el curso " + idCursoPeriodo
        ));
    }

    private List<CursoEstudianteDto> listarFiltradoPorCursos(Set<UUID> idsCursos) {
        if (idsCursos.isEmpty()) {
            return List.of();
        }
        List<CursoEstudiante> filtradas = new ArrayList<>();
        for (CursoEstudiante inscripcion : repositorio.listarTodos()) {
            if (idsCursos.contains(inscripcion.getIdCursoPeriodo())) {
                filtradas.add(inscripcion);
            }
        }
        ordenarPorFechaDesc(filtradas);
        return hidratarYMapearLista(filtradas);
    }

    private void ordenarPorFechaDesc(List<CursoEstudiante> inscripciones) {
        inscripciones.sort(Comparator.comparing(
                CursoEstudiante::getFechaInscripcion,
                Comparator.nullsLast(Comparator.naturalOrder())
        ).reversed());
    }

    private List<CursoEstudianteDto> hidratarYMapearLista(List<CursoEstudiante> inscripciones) {
        if (inscripciones == null || inscripciones.isEmpty()) {
            return List.of();
        }
        Set<UUID> idsEstudiantes = new HashSet<>();
        Set<UUID> idsCursos = new HashSet<>();
        for (CursoEstudiante inscripcion : inscripciones) {
            idsEstudiantes.add(inscripcion.getIdEstudiante());
            idsCursos.add(inscripcion.getIdCursoPeriodo());
        }

        EstudianteCargador estudianteCargador = new EstudianteCargador(estudianteRepositorio);
        estudianteCargador.cargarPorIds(new ArrayList<>(idsEstudiantes));

        CursoPeriodoCargador cursoCargador = new CursoPeriodoCargador(cursoPeriodoRepositorio);
        cursoCargador.cargarPorIds(new ArrayList<>(idsCursos));

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

        return mapeador.toDtoList(inscripciones, estudianteCargador, cursoCargador,
                materiaCargador, periodoCargador);
    }

    private CursoEstudianteDto hidratarYMapearUnico(CursoEstudiante inscripcion) {
        List<CursoEstudianteDto> resultado = hidratarYMapearLista(List.of(inscripcion));
        if (resultado.isEmpty()) {
            throw new IllegalStateException(
                    "Error de integridad al hidratar la inscripcion del estudiante "
                    + inscripcion.getIdEstudiante()
            );
        }
        return resultado.get(0);
    }
}
