package edu.unimag.servicio;

import edu.unimag.dto.cursoperiodo.CursoPeriodoActualizarDto;
import edu.unimag.dto.cursoperiodo.CursoPeriodoCrearDto;
import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.mapeador.CursoPeriodoMapeador;
import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.modelo.Materia;
import edu.unimag.modelo.PeriodoAcademico;
import edu.unimag.repositorio.CursoPeriodoRepositorio;
import edu.unimag.repositorio.MateriaRepositorio;
import edu.unimag.repositorio.PeriodoAcademicoRepositorio;
import edu.unimag.repositorio.cargador.MateriaCargador;
import edu.unimag.repositorio.cargador.PeriodoAcademicoCargador;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CursoPeriodoServicio {

    private final CursoPeriodoRepositorio cursoPeriodoRepositorio;
    private final MateriaRepositorio materiaRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;
    private final CursoPeriodoMapeador mapeador;

    public CursoPeriodoServicio(
            CursoPeriodoRepositorio cursoPeriodoRepositorio,
            MateriaRepositorio materiaRepositorio,
            PeriodoAcademicoRepositorio periodoAcademicoRepositorio,
            CursoPeriodoMapeador mapeador) {

        if (cursoPeriodoRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de cursos por periodo no puede ser nulo");
        }
        if (materiaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de materias no puede ser nulo");
        }
        if (periodoAcademicoRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de periodos academicos no puede ser nulo");
        }
        if (mapeador == null) {
            throw new IllegalArgumentException("El mapeador no puede ser nulo");
        }

        this.cursoPeriodoRepositorio = cursoPeriodoRepositorio;
        this.materiaRepositorio = materiaRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;
        this.mapeador = mapeador;
    }

    public CursoPeriodoDto registrar(CursoPeriodoCrearDto dto) {
        Materia materia = materiaRepositorio.buscarPorId(dto.idMateria())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro materia con ID: " + dto.idMateria()
        ));

        PeriodoAcademico periodoAcademico = periodoAcademicoRepositorio.buscarPorId(dto.idPeriodoAcademico())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro periodo academico con ID: " + dto.idPeriodoAcademico()
        ));

        CursoPeriodo nuevoCurso = new CursoPeriodo(dto.nombre(), materia, periodoAcademico, dto.cupo());
        boolean insertado = cursoPeriodoRepositorio.insertarSiCombinacionNoExiste(
                dto.idMateria(),
                dto.idPeriodoAcademico(),
                dto.nombre(),
                nuevoCurso
        );

        if (!insertado) {
            throw new IllegalStateException(
                    "Ya existe un grupo con el nombre '" + dto.nombre()
                    + "' para la materia y periodo academico especificados"
            );
        }

        // Crear cargadores efímeros y registrar las entidades en caché
        MateriaCargador materiaCargador = new MateriaCargador(materiaRepositorio);
        materiaCargador.registrarEnCache(materia);

        PeriodoAcademicoCargador periodoCargador = new PeriodoAcademicoCargador(periodoAcademicoRepositorio);
        periodoCargador.registrarEnCache(periodoAcademico);

        return mapeador.toDto(nuevoCurso, materiaCargador, periodoCargador);
    }

    public List<CursoPeriodoDto> listarTodos() {
        List<CursoPeriodo> cursos = cursoPeriodoRepositorio.listarTodos();
        if (cursos.isEmpty()) {
            return List.of();
        }

        Set<Integer> idsMaterias = new HashSet<>();
        Set<Integer> idsPeriodos = new HashSet<>();

        for (CursoPeriodo curso : cursos) {
            idsMaterias.add(curso.getIdMateria());
            idsPeriodos.add(curso.getIdPeriodoAcademico());
        }

        MateriaCargador materiaCargador = new MateriaCargador(materiaRepositorio);
        materiaCargador.cargarPorIds(new ArrayList<>(idsMaterias));

        PeriodoAcademicoCargador periodoCargador = new PeriodoAcademicoCargador(periodoAcademicoRepositorio);
        periodoCargador.cargarPorIds(new ArrayList<>(idsPeriodos));
        return mapeador.toDtoList(cursos, materiaCargador, periodoCargador);
    }

    public CursoPeriodoDto actualizar(CursoPeriodoActualizarDto dto) {
        CursoPeriodo cursoPeriodo = cursoPeriodoRepositorio.buscarPorId(dto.id())
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe curso periodo con ID: " + dto.id()
        ));

        boolean cambioNombre = dto.nuevoNombre() != null;
        boolean cambioCupo = dto.nuevoCupo() != null;

        if (!cambioNombre && !cambioCupo) {
            return hidratarYMapearUnico(cursoPeriodo);
        }

        if (cambioCupo) {
            cursoPeriodo.actualizarCupo(dto.nuevoCupo());
        }
        if (cambioNombre) {
            cursoPeriodo.actualizarNombre(dto.nuevoNombre());
        }

        boolean actualizado = cursoPeriodoRepositorio.actualizarSiNoExisteEnOtra(
                cursoPeriodo,
                cambioNombre
        );

        if (!actualizado) {
            throw new IllegalStateException(
                    "Ya existe otro grupo con el nombre '" + dto.nuevoNombre()
                    + "' para la misma materia y periodo academico"
            );
        }
        return hidratarYMapearUnico(cursoPeriodo);
    }

    public boolean eliminar(UUID id) {
        if (!cursoPeriodoRepositorio.buscarPorId(id).isPresent()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar: Curso periodo no encontrado con ID: " + id
            );
        }
        return cursoPeriodoRepositorio.eliminar(id);
    }

    private CursoPeriodoDto hidratarYMapearUnico(CursoPeriodo cursoPeriodo) {
        Materia materia = materiaRepositorio.buscarPorId(cursoPeriodo.getIdMateria())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Materia no encontrada con ID: " + cursoPeriodo.getIdMateria()
        ));

        PeriodoAcademico periodo = periodoAcademicoRepositorio.buscarPorId(cursoPeriodo.getIdPeriodoAcademico())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Periodo academico no encontrado con ID: "
                + cursoPeriodo.getIdPeriodoAcademico()
        ));

        // Crear cargadores efímeros y registrar las entidades en caché
        MateriaCargador materiaCargador = new MateriaCargador(materiaRepositorio);
        materiaCargador.registrarEnCache(materia);

        PeriodoAcademicoCargador periodoCargador = new PeriodoAcademicoCargador(periodoAcademicoRepositorio);
        periodoCargador.registrarEnCache(periodo);

        return mapeador.toDto(cursoPeriodo, materiaCargador, periodoCargador);
    }

    public CursoPeriodoDto buscarPorId(UUID id) {
        CursoPeriodo cursoPeriodo = cursoPeriodoRepositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe curso periodo con ID: " + id
        ));

        // Reutilizamos la hidratación profunda
        return hidratarYMapearUnico(cursoPeriodo);
    }
}
