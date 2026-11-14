package edu.unimag.mapeador;

import edu.unimag.dto.cursoestudiante.CursoEstudianteDto;
import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.estudiante.EstudianteDto;
import edu.unimag.modelo.CursoEstudiante;
import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.modelo.Estudiante;
import edu.unimag.repositorio.cargador.CursoPeriodoCargador;
import edu.unimag.repositorio.cargador.EstudianteCargador;
import edu.unimag.repositorio.cargador.MateriaCargador;
import edu.unimag.repositorio.cargador.PeriodoAcademicoCargador;
import java.util.ArrayList;
import java.util.List;

public class MapeadorCursoEstudiante {

    private final EstudianteMapeador estudianteMapeador;
    private final CursoPeriodoMapeador cursoPeriodoMapeador;

    public MapeadorCursoEstudiante(
            EstudianteMapeador estudianteMapeador,
            CursoPeriodoMapeador cursoPeriodoMapeador
    ) {
        if (estudianteMapeador == null) {
            throw new IllegalArgumentException("El mapeador de estudiante no puede ser nulo");
        }
        if (cursoPeriodoMapeador == null) {
            throw new IllegalArgumentException("El mapeador de curso periodo no puede ser nulo");
        }
        this.estudianteMapeador = estudianteMapeador;
        this.cursoPeriodoMapeador = cursoPeriodoMapeador;
    }

    public List<CursoEstudianteDto> toDtoList(
            List<CursoEstudiante> entidades,
            EstudianteCargador estudianteCargador,
            CursoPeriodoCargador cursoCargador,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador
    ) {
        if (entidades == null || entidades.isEmpty()) {
            return List.of();
        }
        validarCargadores(estudianteCargador, cursoCargador, materiaCargador, periodoCargador);

        List<CursoEstudianteDto> resultado = new ArrayList<>(entidades.size());
        for (CursoEstudiante entidad : entidades) {
            if (!validarIntegridad(entidad, estudianteCargador, cursoCargador,
                    materiaCargador, periodoCargador)) {
                continue;
            }
            resultado.add(transformarADto(entidad, estudianteCargador, cursoCargador,
                    materiaCargador, periodoCargador));
        }
        return resultado;
    }

    public CursoEstudianteDto toDto(
            CursoEstudiante entidad,
            EstudianteCargador estudianteCargador,
            CursoPeriodoCargador cursoCargador,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador
    ) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad curso estudiante no puede ser nula");
        }
        validarCargadores(estudianteCargador, cursoCargador, materiaCargador, periodoCargador);

        if (!validarIntegridad(entidad, estudianteCargador, cursoCargador,
                materiaCargador, periodoCargador)) {
            throw new IllegalStateException(
                    "Error de integridad en la inscripcion del estudiante "
                    + entidad.getIdEstudiante() + " en el curso " + entidad.getIdCursoPeriodo()
            );
        }
        return transformarADto(entidad, estudianteCargador, cursoCargador,
                materiaCargador, periodoCargador);
    }

    private void validarCargadores(
            EstudianteCargador estudianteCargador,
            CursoPeriodoCargador cursoCargador,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador
    ) {
        if (estudianteCargador == null) {
            throw new IllegalArgumentException("El cargador de estudiantes no puede ser nulo");
        }
        if (cursoCargador == null) {
            throw new IllegalArgumentException("El cargador de cursos periodo no puede ser nulo");
        }
        if (materiaCargador == null) {
            throw new IllegalArgumentException("El cargador de materias no puede ser nulo");
        }
        if (periodoCargador == null) {
            throw new IllegalArgumentException("El cargador de periodos academicos no puede ser nulo");
        }
    }

    private boolean validarIntegridad(
            CursoEstudiante entidad,
            EstudianteCargador estudianteCargador,
            CursoPeriodoCargador cursoCargador,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador
    ) {
        if (!estudianteCargador.existe(entidad.getIdEstudiante())) {
            return false;
        }
        if (!cursoCargador.existe(entidad.getIdCursoPeriodo())) {
            return false;
        }
        CursoPeriodo curso = cursoCargador.obtener(entidad.getIdCursoPeriodo()).orElse(null);
        if (curso == null) {
            return false;
        }
        return materiaCargador.existe(curso.getIdMateria())
                && periodoCargador.existe(curso.getIdPeriodoAcademico());
    }

    private CursoEstudianteDto transformarADto(
            CursoEstudiante entidad,
            EstudianteCargador estudianteCargador,
            CursoPeriodoCargador cursoCargador,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador
    ) {
        Estudiante estudiante = estudianteCargador.obtener(entidad.getIdEstudiante())
                .orElseThrow(() -> new IllegalStateException("Error de integridad: estudiante no encontrado"));
        CursoPeriodo curso = cursoCargador.obtener(entidad.getIdCursoPeriodo())
                .orElseThrow(() -> new IllegalStateException("Error de integridad: curso periodo no encontrado"));

        EstudianteDto estudianteDto = estudianteMapeador.toDto(estudiante);
        CursoPeriodoDto cursoDto = cursoPeriodoMapeador.toDto(curso, materiaCargador, periodoCargador);

        return new CursoEstudianteDto(
                estudianteDto,
                cursoDto,
                entidad.getEstadoInscripcion(),
                entidad.getCalificacionCursoEstudiante(),
                entidad.getObservacionCursoEstudiante(),
                entidad.getFechaInscripcion(),
                entidad.getFechaEvaluacion()
        );
    }
}
