package edu.unimag.controlador;

import edu.unimag.dto.cursoPorfesor.cursoProfesorActualizar;
import edu.unimag.dto.cursoPorfesor.cursoProfesorCrearDto;
import edu.unimag.dto.cursoPorfesor.cursoProfesorDto;
import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.profesor.ProfesorDto;
import edu.unimag.servicio.CursoPeriodoServicio;
import edu.unimag.servicio.CursoProfesorServicio;
import edu.unimag.servicio.ProfesorServicio;
import java.util.List;
import java.util.UUID;

public class ControladorCursoProfesor {

private final CursoPeriodoServicio cursoPeriodoServicio;
private final ProfesorServicio profesorServicio;
private final CursoProfesorServicio cursoProfesorServicio;

    public ControladorCursoProfesor(CursoPeriodoServicio cursoPeriodoServicio, ProfesorServicio profesorServicio, CursoProfesorServicio cursoProfesorServicio) {
         if (cursoPeriodoServicio==null) {
            throw  new  IllegalArgumentException("El servicio de curso no puede ser nulo");
        }
          if (cursoProfesorServicio==null) {
            throw  new  IllegalArgumentException("El servicio de curso profesor no puede ser nulo");
        }
           if (profesorServicio==null) {
            throw  new  IllegalArgumentException("El servicio de profesor no puede ser nulo");
        }
        this.cursoPeriodoServicio = cursoPeriodoServicio;
        this.profesorServicio = profesorServicio;
        this.cursoProfesorServicio = cursoProfesorServicio;
    }
    public cursoProfesorDto asignar(cursoProfesorCrearDto dto){
        return cursoProfesorServicio.asignar(dto);
    }
    public cursoProfesorDto cambiarTipoAsignacion(cursoProfesorActualizar dto){
        return cursoProfesorServicio.cambiarTipoAsignacion(dto);
    }
    public cursoProfesorDto retirar(UUID idCursoPeriodo, Long IdProfesor){
        return cursoProfesorServicio.retirarProfesor(idCursoPeriodo, IdProfesor);
    }
    public cursoProfesorDto reactivar(UUID idCursoPeriodo, Long IdProfesor){
        return cursoProfesorServicio.reactivar(idCursoPeriodo, IdProfesor);
    }
    public List<cursoProfesorDto> listarPorProfesor(Long idprofesor){
        return cursoProfesorServicio.listarPorProfesor(idprofesor);
    }
    public List<cursoProfesorDto> listarPorMateria(Integer idMateria){
        return cursoProfesorServicio.listarPorMateria(idMateria);
    }
    public List<cursoProfesorDto> listarPorPeriodoAcademico(Integer idPerido){
        return cursoProfesorServicio.listarPorPeriodoAc(idPerido);
    }
    public List<cursoProfesorDto> listarPorMateriaYperiodo(Integer idMateria, Integer idPeriodo){
        return cursoProfesorServicio.listarPorMateriaPeriodo(idMateria, idPeriodo);
    }
    public ProfesorDto buscarProfesor(Long idProfesor){
        return profesorServicio.buscarPorId(idProfesor);
    }
    public CursoPeriodoDto buscarCursoPeriodo(UUID idperiodo){
        return cursoPeriodoServicio.buscarPorId(idperiodo);
    }
}
