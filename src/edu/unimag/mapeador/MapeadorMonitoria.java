package edu.unimag.mapeador;

import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.estudiante.EstudianteDto;
import edu.unimag.dto.monitoria.MonitoriaDto;
import edu.unimag.dto.profesor.ProfesorDto;
import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.modelo.Estudiante;
import edu.unimag.modelo.Monitoria;
import edu.unimag.modelo.Profesor;
import edu.unimag.repositorio.cargador.CursoPeriodoCargador;
import edu.unimag.repositorio.cargador.EstudianteCargador;
import edu.unimag.repositorio.cargador.MateriaCargador;
import edu.unimag.repositorio.cargador.PeriodoAcademicoCargador;
import edu.unimag.repositorio.cargador.ProfesorCargador;
import java.util.ArrayList;
import java.util.List;

public class MapeadorMonitoria {

    private final CursoPeriodoMapeador cursoPeriodoMapeador;
    private final EstudianteMapeador estudianteMapeador;
    private final ProfesorMapeador profesorMapeador;

    public MapeadorMonitoria(
            CursoPeriodoMapeador cursoPeriodoMapeador,
            EstudianteMapeador estudianteMapeador,
            ProfesorMapeador profesorMapeador
    ) {
        if (cursoPeriodoMapeador == null) {
            throw new IllegalArgumentException("El mapeador de curso periodo no puede ser nulo");
        }
        if (estudianteMapeador == null) {
            throw new IllegalArgumentException("El mapeador de estudiante no puede ser nulo");
        }
        if (profesorMapeador == null) {
            throw new IllegalArgumentException("El mapeador de profesor no puede ser nulo");
        }
        this.cursoPeriodoMapeador = cursoPeriodoMapeador;
        this.estudianteMapeador = estudianteMapeador;
        this.profesorMapeador = profesorMapeador;
    }

    public List<MonitoriaDto> toDtoList(
            List<Monitoria> entidades,
            CursoPeriodoCargador cursoCargador,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador,
            EstudianteCargador estudianteCargador,
            ProfesorCargador profesorCargador
    ) {
        if (entidades == null || entidades.isEmpty()) {
            return List.of();
        }
        validarCargadores(cursoCargador, materiaCargador, periodoCargador,
                estudianteCargador, profesorCargador);

        List<MonitoriaDto> resultado = new ArrayList<>(entidades.size());
        for (Monitoria entidad : entidades) {
            if (!validarIntegridad(entidad, cursoCargador, materiaCargador, periodoCargador,
                    estudianteCargador, profesorCargador)) {
                continue;
            }
            resultado.add(transformarADto(entidad, cursoCargador, materiaCargador, periodoCargador,
                    estudianteCargador, profesorCargador));
        }
        return resultado;
    }

    public MonitoriaDto toDto(
            Monitoria entidad,
            CursoPeriodoCargador cursoCargador,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador,
            EstudianteCargador estudianteCargador,
            ProfesorCargador profesorCargador
    ) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad monitoria no puede ser nula");
        }
        validarCargadores(cursoCargador, materiaCargador, periodoCargador,
                estudianteCargador, profesorCargador);

        if (!validarIntegridad(entidad, cursoCargador, materiaCargador, periodoCargador,
                estudianteCargador, profesorCargador)) {
            throw new IllegalStateException(
                    "Error de integridad en la monitoria del estudiante " + entidad.getIdEstudiante()
                    + " en el curso " + entidad.getIdCursoPeriodo()
                    + " con el profesor " + entidad.getIdProfesor()
            );
        }
        return transformarADto(entidad, cursoCargador, materiaCargador, periodoCargador,
                estudianteCargador, profesorCargador);
    }

    private void validarCargadores(
            CursoPeriodoCargador cursoCargador,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador,
            EstudianteCargador estudianteCargador,
            ProfesorCargador profesorCargador
    ) {
        if (cursoCargador == null) {
            throw new IllegalArgumentException("El cargador de cursos periodo no puede ser nulo");
        }
        if (materiaCargador == null) {
            throw new IllegalArgumentException("El cargador de materias no puede ser nulo");
        }
        if (periodoCargador == null) {
            throw new IllegalArgumentException("El cargador de periodos academicos no puede ser nulo");
        }
        if (estudianteCargador == null) {
            throw new IllegalArgumentException("El cargador de estudiantes no puede ser nulo");
        }
        if (profesorCargador == null) {
            throw new IllegalArgumentException("El cargador de profesores no puede ser nulo");
        }
    }

    private boolean validarIntegridad(
            Monitoria entidad,
            CursoPeriodoCargador cursoCargador,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador,
            EstudianteCargador estudianteCargador,
            ProfesorCargador profesorCargador
    ) {
        if (!cursoCargador.existe(entidad.getIdCursoPeriodo())) {
            return false;
        }
        if (!estudianteCargador.existe(entidad.getIdEstudiante())) {
            return false;
        }
        if (!profesorCargador.existe(entidad.getIdProfesor())) {
            return false;
        }
        CursoPeriodo curso = cursoCargador.obtener(entidad.getIdCursoPeriodo()).orElse(null);
        if (curso == null) {
            return false;
        }
        return materiaCargador.existe(curso.getIdMateria())
                && periodoCargador.existe(curso.getIdPeriodoAcademico());
    }

    private MonitoriaDto transformarADto(
            Monitoria entidad,
            CursoPeriodoCargador cursoCargador,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador,
            EstudianteCargador estudianteCargador,
            ProfesorCargador profesorCargador
    ) {
        CursoPeriodo curso = cursoCargador.obtener(entidad.getIdCursoPeriodo())
                .orElseThrow(() -> new IllegalStateException("Error de integridad: curso no encontrado"));
        Estudiante estudiante = estudianteCargador.obtener(entidad.getIdEstudiante())
                .orElseThrow(() -> new IllegalStateException("Error de integridad: estudiante no encontrado"));
        Profesor profesor = profesorCargador.obtener(entidad.getIdProfesor())
                .orElseThrow(() -> new IllegalStateException("Error de integridad: profesor no encontrado"));

        CursoPeriodoDto cursoDto = cursoPeriodoMapeador.toDto(curso, materiaCargador, periodoCargador);
        EstudianteDto estudianteDto = estudianteMapeador.toDto(estudiante);
        ProfesorDto profesorDto = profesorMapeador.toDto(profesor);

        return new MonitoriaDto(
                cursoDto,
                estudianteDto,
                profesorDto,
                entidad.getCalificacionMonitoria(),
                entidad.getComentarioMonitoria(),
                entidad.getEstadoMonitoria(),
                entidad.getFechaCreacionMonitoria(),
                entidad.getFechaEvaluacionMonitoria()
        );
    }
}
