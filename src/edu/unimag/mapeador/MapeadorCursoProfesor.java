package edu.unimag.mapeador;

import edu.unimag.dto.cursoPorfesor.cursoProfesorDto;
import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.profesor.ProfesorDto;
import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.modelo.CursoProfesor;
import edu.unimag.modelo.Profesor;
import edu.unimag.repositorio.cargador.CursoPeriodoCargador;
import edu.unimag.repositorio.cargador.MateriaCargador;
import edu.unimag.repositorio.cargador.PeriodoAcademicoCargador;
import edu.unimag.repositorio.cargador.ProfesorCargador;
import java.util.ArrayList;
import java.util.List;

public class MapeadorCursoProfesor{

    private final ProfesorMapeador profesorMapeador;
    private final CursoPeriodoMapeador cursoPeriodoMapeador;

    public MapeadorCursoProfesor(ProfesorMapeador profesorMapeador, CursoPeriodoMapeador cursoPeriodoMapeador) {
        if (profesorMapeador==null) {
            throw  new IllegalArgumentException("El mapeador de profesor no puede ser nulo");
        }
        if (cursoPeriodoMapeador==null) {
            throw  new IllegalArgumentException("El mapeador de curso periodo no puede ser nulo");
        }
        this.profesorMapeador = profesorMapeador;
        this.cursoPeriodoMapeador = cursoPeriodoMapeador;
    }
   

   
    public List<cursoProfesorDto> toDtoList(List<CursoProfesor> entidades, ProfesorCargador cargador, CursoPeriodoCargador cargaPeriodo, MateriaCargador materiaCargador, PeriodoAcademicoCargador periodoAcaCargador) {
        if (entidades==null||entidades.isEmpty()) {
            return  List.of();
        }
        if (cargador==null) {
            throw  new IllegalArgumentException("el cargador de profesor no puede  ser nulo");
        }
        if (cargaPeriodo==null) {
            throw  new IllegalArgumentException("el cargador de curso periodo no puede  ser nulo");
        }
        if (materiaCargador==null) {
            throw  new IllegalArgumentException("el cargador de materia no puede  ser nulo");
        }
        if (periodoAcaCargador==null) {
            throw  new IllegalArgumentException("el cargador de periodo academico no puede  ser nulo");
        }
        List<cursoProfesorDto> resultado = new ArrayList<>(entidades.size());
        for (CursoProfesor entidad : entidades) {
            if (!validarIntegridad(entidad,cargador, cargaPeriodo, materiaCargador, periodoAcaCargador)) {
                continue;
            }
            resultado.add(transformarADto(entidad, cargador, cargaPeriodo, materiaCargador, periodoAcaCargador));
            
        }
        return resultado;
    }

    private boolean validarIntegridad(CursoProfesor entidad, ProfesorCargador cargador, CursoPeriodoCargador cargaPeriodo, MateriaCargador materiaCargador, PeriodoAcademicoCargador periodoAcaCargador) {
        if (!cargador.existe(entidad.getIdProfesor())) {
            return false;
        }
        if (!cargaPeriodo.existe(entidad.getIdCurso())) {
            return false;
        }
        CursoPeriodo cursoPeriodo= cargaPeriodo.obtener(entidad.getIdCurso()). orElse(null);
        if (cursoPeriodo==null) {
            return  false;
        }
        if (!materiaCargador.existe(cursoPeriodo.getIdMateria())) {
            return false;
        }
        if (!periodoAcaCargador.existe(cursoPeriodo.getIdPeriodoAcademico())) {
           return false;
        }
        return true;
    }

    private cursoProfesorDto transformarADto(CursoProfesor entidad, ProfesorCargador cargador, CursoPeriodoCargador cargaPeriodo, MateriaCargador materiaCargador, PeriodoAcademicoCargador periodoAcaCargador) {
        Profesor profesor= cargador.obtener(entidad.getIdProfesor()).orElseThrow(()->new IllegalStateException("Error de integridad"));
        CursoPeriodo cursoPeriodo=cargaPeriodo.obtener(entidad.getIdCurso()).orElseThrow(()->new IllegalStateException("Error de integridad"));
        ProfesorDto profesorDto= profesorMapeador.toDto(profesor);
        CursoPeriodoDto cursoPeriodoDto= cursoPeriodoMapeador.toDto(cursoPeriodo, materiaCargador, periodoAcaCargador);
        return new cursoProfesorDto(profesorDto, cursoPeriodoDto,
                entidad.getTipoAsignacion(),
                entidad.getEstado(),
                entidad.getFechaAsisgnacion());
    }
    
    public cursoProfesorDto toDto(CursoProfesor entidad, ProfesorCargador cargador, PeriodoAcademicoCargador periodoAcademicoCargador, MateriaCargador materiaCargador, CursoPeriodoCargador cursoPeriodoCargador ){
        if (entidad== null) {
            throw new IllegalArgumentException("La entidad no puede ser nula");
        }
        if (cargador== null) {
            throw new IllegalArgumentException("El Cargador de profesor no puede ser nulo");
        }
        if (periodoAcademicoCargador== null) {
            throw new IllegalArgumentException("El Cargador del periodo academico no puede ser nulo");
        }
        if (materiaCargador== null) {
            throw new IllegalArgumentException("El Cargador de materia no puede ser nulo");
        }
        if (cursoPeriodoCargador== null) {
            throw new IllegalArgumentException("El Cargador de curso periodo no puede ser nulo");
        }
        if (!validarIntegridad(entidad, cargador, cursoPeriodoCargador, materiaCargador, periodoAcademicoCargador)) {
           throw new IllegalStateException("Error con el curso profesor");
        }
        return  transformarADto(entidad, cargador, cursoPeriodoCargador, materiaCargador, periodoAcademicoCargador);
    }
    
}
