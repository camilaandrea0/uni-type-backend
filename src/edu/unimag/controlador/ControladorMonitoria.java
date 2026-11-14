package edu.unimag.controlador;

import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.estudiante.EstudianteDto;
import edu.unimag.dto.monitoria.MonitoriaCrearDto;
import edu.unimag.dto.monitoria.MonitoriaDto;
import edu.unimag.dto.monitoria.MonitoriaEvaluarDto;
import edu.unimag.dto.profesor.ProfesorDto;
import edu.unimag.servicio.CursoPeriodoServicio;
import edu.unimag.servicio.EstudianteServicio;
import edu.unimag.servicio.MonitoriaServicio;
import edu.unimag.servicio.ProfesorServicio;
import java.util.List;
import java.util.UUID;

public class ControladorMonitoria {

    private final MonitoriaServicio servicio;
    private final CursoPeriodoServicio cursoPeriodoServicio;
    private final EstudianteServicio estudianteServicio;
    private final ProfesorServicio profesorServicio;

    public ControladorMonitoria(
            MonitoriaServicio servicio,
            CursoPeriodoServicio cursoPeriodoServicio,
            EstudianteServicio estudianteServicio,
            ProfesorServicio profesorServicio
    ) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio de monitorias no puede ser nulo");
        }
        if (cursoPeriodoServicio == null) {
            throw new IllegalArgumentException("El servicio de cursos periodo no puede ser nulo");
        }
        if (estudianteServicio == null) {
            throw new IllegalArgumentException("El servicio de estudiantes no puede ser nulo");
        }
        if (profesorServicio == null) {
            throw new IllegalArgumentException("El servicio de profesores no puede ser nulo");
        }
        this.servicio = servicio;
        this.cursoPeriodoServicio = cursoPeriodoServicio;
        this.estudianteServicio = estudianteServicio;
        this.profesorServicio = profesorServicio;
    }

    public MonitoriaDto crear(MonitoriaCrearDto dto) {
        return servicio.crear(dto);
    }

    public MonitoriaDto evaluar(MonitoriaEvaluarDto dto) {
        return servicio.evaluar(dto);
    }

    public void eliminar(UUID idCursoPeriodo, UUID idEstudiante, Long idProfesor) {
        servicio.eliminar(idCursoPeriodo, idEstudiante, idProfesor);
    }

    public MonitoriaDto buscarPorId(UUID idCursoPeriodo, UUID idEstudiante, Long idProfesor) {
        return servicio.buscarPorId(idCursoPeriodo, idEstudiante, idProfesor);
    }

    public List<MonitoriaDto> listarTodos() {
        return servicio.listarTodos();
    }

    public List<MonitoriaDto> listarPorCursoPeriodo(UUID idCursoPeriodo) {
        return servicio.listarPorCursoPeriodo(idCursoPeriodo);
    }

    public List<MonitoriaDto> listarPorEstudiante(UUID idEstudiante) {
        return servicio.listarPorEstudiante(idEstudiante);
    }

    public List<MonitoriaDto> listarPorProfesor(Long idProfesor) {
        return servicio.listarPorProfesor(idProfesor);
    }

    public CursoPeriodoDto buscarCursoPeriodo(UUID idCursoPeriodo) {
        return cursoPeriodoServicio.buscarPorId(idCursoPeriodo);
    }

    public EstudianteDto buscarEstudiante(UUID idEstudiante) {
        return estudianteServicio.buscarPorId(idEstudiante);
    }

    public ProfesorDto buscarProfesor(Long idProfesor) {
        return profesorServicio.buscarPorId(idProfesor);
    }
}
