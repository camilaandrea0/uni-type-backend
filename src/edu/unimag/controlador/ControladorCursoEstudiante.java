package edu.unimag.controlador;

import edu.unimag.dto.cursoestudiante.CursoEstudianteCrearDto;
import edu.unimag.dto.cursoestudiante.CursoEstudianteDto;
import edu.unimag.dto.cursoestudiante.CursoEstudianteEvaluarDto;
import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.estudiante.EstudianteDto;
import edu.unimag.servicio.CursoEstudianteServicio;
import edu.unimag.servicio.CursoPeriodoServicio;
import edu.unimag.servicio.EstudianteServicio;
import java.util.List;
import java.util.UUID;

public class ControladorCursoEstudiante {

    private final CursoEstudianteServicio servicio;
    private final EstudianteServicio estudianteServicio;
    private final CursoPeriodoServicio cursoPeriodoServicio;

    public ControladorCursoEstudiante(
            CursoEstudianteServicio servicio,
            EstudianteServicio estudianteServicio,
            CursoPeriodoServicio cursoPeriodoServicio
    ) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio de inscripciones no puede ser nulo");
        }
        if (estudianteServicio == null) {
            throw new IllegalArgumentException("El servicio de estudiantes no puede ser nulo");
        }
        if (cursoPeriodoServicio == null) {
            throw new IllegalArgumentException("El servicio de cursos periodo no puede ser nulo");
        }
        this.servicio = servicio;
        this.estudianteServicio = estudianteServicio;
        this.cursoPeriodoServicio = cursoPeriodoServicio;
    }

    public CursoEstudianteDto inscribir(CursoEstudianteCrearDto dto) {
        return servicio.inscribir(dto);
    }

    public CursoEstudianteDto admitir(UUID idEstudiante, UUID idCursoPeriodo) {
        return servicio.admitir(idEstudiante, idCursoPeriodo);
    }

    public CursoEstudianteDto rechazar(UUID idEstudiante, UUID idCursoPeriodo) {
        return servicio.rechazar(idEstudiante, idCursoPeriodo);
    }

    public CursoEstudianteDto retirar(UUID idEstudiante, UUID idCursoPeriodo) {
        return servicio.retirar(idEstudiante, idCursoPeriodo);
    }

    public CursoEstudianteDto evaluar(CursoEstudianteEvaluarDto dto) {
        return servicio.evaluar(dto);
    }

    public CursoEstudianteDto buscarPorId(UUID idEstudiante, UUID idCursoPeriodo) {
        return servicio.buscarPorId(idEstudiante, idCursoPeriodo);
    }

    public List<CursoEstudianteDto> listarTodos() {
        return servicio.listarTodos();
    }

    public List<CursoEstudianteDto> listarPorEstudiante(UUID idEstudiante) {
        return servicio.listarPorEstudiante(idEstudiante);
    }

    public List<CursoEstudianteDto> listarPorCursoPeriodo(UUID idCursoPeriodo) {
        return servicio.listarPorCursoPeriodo(idCursoPeriodo);
    }

    public List<CursoEstudianteDto> listarPorMateria(Integer idMateria) {
        return servicio.listarPorMateria(idMateria);
    }

    public List<CursoEstudianteDto> listarPorMateriaYPeriodo(Integer idMateria, Integer idPeriodo) {
        return servicio.listarPorMateriaYPeriodo(idMateria, idPeriodo);
    }

    public List<CursoEstudianteDto> listarPorPeriodo(Integer idPeriodo) {
        return servicio.listarPorPeriodo(idPeriodo);
    }

    public EstudianteDto buscarEstudiante(UUID idEstudiante) {
        return estudianteServicio.buscarPorId(idEstudiante);
    }

    public CursoPeriodoDto buscarCursoPeriodo(UUID idCursoPeriodo) {
        return cursoPeriodoServicio.buscarPorId(idCursoPeriodo);
    }
}
