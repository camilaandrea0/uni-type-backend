package edu.unimag.servicio;

import edu.unimag.dto.cursoPorfesor.cursoProfesorActualizar;
import edu.unimag.dto.cursoPorfesor.cursoProfesorCrearDto;
import edu.unimag.dto.cursoPorfesor.cursoProfesorDto;
import edu.unimag.mapeador.MapeadorCursoProfesor;
import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.modelo.CursoProfesor;
import edu.unimag.modelo.Materia;
import edu.unimag.modelo.PeriodoAcademico;
import edu.unimag.modelo.Profesor;
import edu.unimag.modelo.pk.CursoProfesorPk;
import edu.unimag.repositorio.CursoPeriodoRepositorio;
import edu.unimag.repositorio.CursoProfesorRepositorio;
import edu.unimag.repositorio.MateriaRepositorio;
import edu.unimag.repositorio.PeriodoAcademicoRepositorio;
import edu.unimag.repositorio.ProfesorRepositorio;
import edu.unimag.repositorio.cargador.CursoPeriodoCargador;
import edu.unimag.repositorio.cargador.MateriaCargador;
import edu.unimag.repositorio.cargador.PeriodoAcademicoCargador;
import edu.unimag.repositorio.cargador.ProfesorCargador;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class CursoProfesorServicio {
    
    private final CursoProfesorRepositorio repositorio;
    private final CursoPeriodoRepositorio periodoRepo;
    private final MapeadorCursoProfesor mapeador;
    private final PeriodoAcademicoRepositorio academicoRepo;
    private final MateriaRepositorio materiaRepo;
    private final ProfesorRepositorio profeRepo;
    
    public CursoProfesorServicio(CursoProfesorRepositorio repositorio, CursoPeriodoRepositorio periodoRepo, MapeadorCursoProfesor mapeador, PeriodoAcademicoRepositorio academicoRepo, MateriaRepositorio materiaRepo, ProfesorRepositorio profeRepo) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio del curso Profesor");
        }
        if (periodoRepo == null) {
            throw new IllegalArgumentException("El repositorio del curso Profesor");
        }
        if (mapeador == null) {
            throw new IllegalArgumentException("El repositorio del curso Profesor");
        }
        if (academicoRepo == null) {
            throw new IllegalArgumentException("El repositorio del curso Profesor");
        }
        if (materiaRepo == null) {
            throw new IllegalArgumentException("El repositorio del curso Profesor");
        }
        if (profeRepo == null) {
            throw new IllegalArgumentException("El repositorio del curso Profesor");
        }
        this.profeRepo = profeRepo;
        this.repositorio = repositorio;
        this.periodoRepo = periodoRepo;
        this.mapeador = mapeador;
        this.academicoRepo = academicoRepo;
        this.materiaRepo = materiaRepo;
    }

    // logica de negocio
    private List<cursoProfesorDto> hidratarYMapearListaGenerico(
            List<CursoProfesor> asignaciones,
            MateriaCargador materiaCargadorInicial,
            PeriodoAcademicoCargador periodoCargadorInicial
    ) {
        Set<Long> idProfesor = new HashSet<>();
        Set<UUID> idCursoPeriodo = new HashSet<>();
        for (CursoProfesor asignacion : asignaciones) {
            idProfesor.add(asignacion.getIdProfesor());
            idCursoPeriodo.add(asignacion.getIdCurso());
        }
        ProfesorCargador profesorCargador = new ProfesorCargador(profeRepo);
        profesorCargador.cargarPorIds(new ArrayList<>(idProfesor));
        
        CursoPeriodoCargador cursoPeriodoCargador = new CursoPeriodoCargador(periodoRepo);
        cursoPeriodoCargador.cargarPorIds(new ArrayList<>(idCursoPeriodo));
        
        Set<Integer> idsMateria = new HashSet<>();
        Set<Integer> idsPeriodo = new HashSet<>();
        
        for (UUID uuid : idCursoPeriodo) {
            cursoPeriodoCargador.obtener(uuid).ifPresent(
                    curso -> {
                        idsMateria.add(curso.getIdMateria());
                        idsPeriodo.add(curso.getIdPeriodoAcademico());
                    }
            );            
        }
        
        MateriaCargador materiaCargador = materiaCargadorInicial != null ? materiaCargadorInicial : new MateriaCargador(materiaRepo);
        
        materiaCargador.cargarPorIds(new ArrayList<>(idsMateria));
        
        PeriodoAcademicoCargador periodoAcademicoCargador = periodoCargadorInicial != null ? periodoCargadorInicial : new PeriodoAcademicoCargador(academicoRepo);
        periodoAcademicoCargador.cargarPorIds(new ArrayList<>(idsPeriodo));
        
        return mapeador.toDtoList(asignaciones, profesorCargador, cursoPeriodoCargador, materiaCargador, periodoAcademicoCargador);
    }
    
    private List<cursoProfesorDto> hidratearYMapearListaMateria(List<CursoProfesor> asignaciones, Materia materia) {
        MateriaCargador materiaCargador = new MateriaCargador(materiaRepo);
        materiaCargador.registrarEnCache(materia);
        return hidratarYMapearListaGenerico(asignaciones, materiaCargador, null);
    }
    
    private List<cursoProfesorDto> hidratarYMapearListaPeriodo(List<CursoProfesor> asignaciones, PeriodoAcademico periodoAcademico) {
        PeriodoAcademicoCargador periodoAcademicoCargador = new PeriodoAcademicoCargador(academicoRepo);
        periodoAcademicoCargador.registrarEnCache(periodoAcademico);
        return hidratarYMapearListaGenerico(asignaciones, null, periodoAcademicoCargador);
    }
    
    private List<cursoProfesorDto> hidratarYMapearListaPeriodoYMateria(List<CursoProfesor> asignaciones, PeriodoAcademico periodoAcademico, Materia materia) {
        MateriaCargador materiaCargador = new MateriaCargador(materiaRepo);
        materiaCargador.registrarEnCache(materia);
        PeriodoAcademicoCargador periodoAcademicoCargador = new PeriodoAcademicoCargador(academicoRepo);
        periodoAcademicoCargador.registrarEnCache(periodoAcademico);
        return hidratarYMapearListaGenerico(asignaciones, materiaCargador, periodoAcademicoCargador);
    }
    
    private cursoProfesorDto hidratarYMapearUnico(CursoProfesor asignacion, CursoPeriodo periodo, Profesor profesor) {
        Materia materia = materiaRepo.buscarPorId(periodo.getIdMateria()).orElseThrow(() -> new IllegalStateException("Materia Incorrecta"));
        PeriodoAcademico periodoAcademico = academicoRepo.buscarPorId(periodo.getIdPeriodoAcademico()).orElseThrow(()
                -> new IllegalStateException("Periodo incorrecto"));
        ProfesorCargador opCargador = new ProfesorCargador(profeRepo);
        opCargador.registrarEnCache(profesor);
        
        CursoPeriodoCargador cursoPeriodoCargador = new CursoPeriodoCargador(periodoRepo);
        cursoPeriodoCargador.registrarEnCache(periodo);
        MateriaCargador materiaCargador = new MateriaCargador(materiaRepo);
        materiaCargador.registrarEnCache(materia);
        PeriodoAcademicoCargador periodoAcademicoCargador = new PeriodoAcademicoCargador(academicoRepo);
        periodoAcademicoCargador.registrarEnCache(periodoAcademico);
        
        return mapeador.toDto(asignacion, opCargador, periodoAcademicoCargador, materiaCargador, cursoPeriodoCargador);
    }

    private List<cursoProfesorDto> hidratarYmapearLista(List<CursoProfesor> asignaciones, Profesor profesor) {
        ProfesorCargador profesorCargador = new ProfesorCargador(profeRepo);
        profesorCargador.registrarEnCache(profesor);
        
        Set<UUID> idsCursoPeriodo = new HashSet<>();
        for (CursoProfesor cursoProfesor : asignaciones) {
            idsCursoPeriodo.add(cursoProfesor.getIdCurso());
        }
        CursoPeriodoCargador cursoPeriodoCargador = new CursoPeriodoCargador(periodoRepo);
        cursoPeriodoCargador.cargarPorIds(new ArrayList<>(idsCursoPeriodo));
        Set<Integer> idsMateria = new HashSet<>();
        Set<Integer> idsPeriodoacademico = new HashSet<>();
        for (UUID id : idsCursoPeriodo) {
            cursoPeriodoCargador.obtener(id).ifPresent(curso -> {
                idsMateria.add(curso.getIdMateria());
                idsPeriodoacademico.add(curso.getIdPeriodoAcademico());
            }
            );
        }
        MateriaCargador materiaCargador = new MateriaCargador(materiaRepo);
        materiaCargador.cargarPorIds(new ArrayList<>(idsMateria));
        PeriodoAcademicoCargador periodoAcademicoCargador = new PeriodoAcademicoCargador(academicoRepo);
        periodoAcademicoCargador.cargarPorIds(new ArrayList<>(idsPeriodoacademico));
      
        return  mapeador.toDtoList(asignaciones, profesorCargador, cursoPeriodoCargador, materiaCargador, periodoAcademicoCargador);
    }
    
    public cursoProfesorDto asignar(cursoProfesorCrearDto Dto){
     Profesor profesor=profeRepo.buscarPorId(Dto.idProfesor()).orElseThrow(()-> new IllegalArgumentException("Profe no econtrado"));
     
     CursoPeriodo cursoPeriodo= periodoRepo.buscarPorId(Dto.idCursoPeriodo()).orElseThrow(()-> new IllegalArgumentException("Curso periodo no encontrado"));
      CursoProfesorPk pk = new CursoProfesorPk(cursoPeriodo, profesor);
        CursoProfesor cursoProfesor= new CursoProfesor(pk, Dto.tipoAsignacion());
      boolean insertado= repositorio.insertarSinombrenoExiste(cursoProfesor);
        if (!insertado) {
            throw  new IllegalStateException("Ya existe la llave o error al grabar");
        }
        return  hidratarYMapearUnico(cursoProfesor, cursoPeriodo, profesor);
    }
    
    public cursoProfesorDto cambiarTipoAsignacion(cursoProfesorActualizar dto){
        CursoPeriodo cursoPeriodo= periodoRepo.buscarPorId(dto.idCursoPeriodo()).orElseThrow(()-> new IllegalArgumentException("Curso Periodo no esncontrado"));
        Profesor profesor = profeRepo.buscarPorId(dto.idProfesor()).orElseThrow(()-> new IllegalArgumentException("Profesor no encontrado"));
        
        CursoProfesorPk  pk= new CursoProfesorPk(cursoPeriodo, profesor);
        CursoProfesor asignacion = repositorio.buscarPorId(pk).orElseThrow(()->new IllegalArgumentException("no se encontro la asignacion"));
        asignacion.actualizarTipoAsig(dto.tipoAsignacion());
        CursoProfesor actualizado = repositorio.actualizar(asignacion);
        
        return hidratarYMapearUnico(actualizado, cursoPeriodo, profesor);
    }
    public cursoProfesorDto retirarProfesor(UUID idCursoPeriodo, Long idProfesor){
        CursoPeriodo cursoPeriodo= periodoRepo.buscarPorId(idCursoPeriodo).orElseThrow(()->new IllegalArgumentException("Curso periodo no existe"));
        Profesor profesor= profeRepo.buscarPorId(idProfesor).orElseThrow(()-> new IllegalArgumentException("Profesor No existe"));
       
        CursoProfesorPk pk= new CursoProfesorPk(cursoPeriodo, profesor);
        CursoProfesor asignacion= repositorio.buscarPorId(pk).orElseThrow(()-> new IllegalArgumentException("Asignacion no encontrada"));
        asignacion.retirar();
        CursoProfesor actualizado= repositorio.actualizar(asignacion);
        
        return hidratarYMapearUnico(actualizado, cursoPeriodo, profesor);
    }   
    
    public  cursoProfesorDto reactivar (UUID idCursoPeriodo, Long idProfesor){
        CursoPeriodo cursoPeriodo= periodoRepo.buscarPorId(idCursoPeriodo).orElseThrow(()->new IllegalArgumentException("Curso periodo no existe"));
        Profesor profesor= profeRepo.buscarPorId(idProfesor).orElseThrow(()-> new IllegalArgumentException("Profesor No existe"));
       
        CursoProfesorPk pk= new CursoProfesorPk(cursoPeriodo, profesor);
        CursoProfesor asignacion= repositorio.buscarPorId(pk).orElseThrow(()-> new IllegalArgumentException("Asignacion no encontrada"));
        asignacion.reactivar();
        CursoProfesor actualizado= repositorio.actualizar(asignacion);
        
        return hidratarYMapearUnico(actualizado, cursoPeriodo, profesor);
    }
    
    public List<cursoProfesorDto> listarPorProfesor(Long idProfesor){
        Profesor profesor= profeRepo.buscarPorId(idProfesor).orElseThrow(()-> new IllegalArgumentException("No existe ese Profe") );
        
        List<CursoProfesor> todos= repositorio.listarTodos();
        List<CursoProfesor> cursosDelProfesor= new ArrayList<>();
        for (CursoProfesor cursos : todos) {
            if (cursos.getIdProfesor().equals(idProfesor)) {
                cursosDelProfesor.add(cursos);
            }
        }
        if (cursosDelProfesor.isEmpty()) {
            return new ArrayList<>();
        }
        
        cursosDelProfesor.sort(Comparator.comparing(CursoProfesor::getFechaAsisgnacion).reversed());
        return hidratarYmapearLista(cursosDelProfesor, profesor);
    }
    public List<cursoProfesorDto> listarPorMateria(Integer idMateria){
        Materia materia= materiaRepo.buscarPorId(idMateria).orElseThrow(()-> new IllegalArgumentException("La Materia no Existe"));
        Set<UUID> idCursosValidos=new HashSet<>();
        
        for (CursoPeriodo cursop : periodoRepo.listarTodos()) {
            if (cursop.getIdMateria().equals(idMateria)) {
                idCursosValidos.add(cursop.getIdCursoPeriodo());
            }
        }
        List<CursoProfesor> delaMateria = new ArrayList<>();
        for (CursoProfesor asignacion : repositorio.listarTodos()) {
            if (idCursosValidos.contains(asignacion.getIdCurso())) {
                delaMateria.add(asignacion);
            }
        }
        if (delaMateria.isEmpty()) {
            return List.of();
        }
        delaMateria.sort(Comparator.comparing(CursoProfesor::getFechaAsisgnacion).reversed());
        return hidratearYMapearListaMateria(delaMateria, materia);
    }
    
    public List<cursoProfesorDto> listarPorPeriodoAc(Integer idPeriodo){
        PeriodoAcademico periodoAcademico= academicoRepo.buscarPorId(idPeriodo).orElseThrow(()-> new IllegalArgumentException("No existe ese periodo: "+ idPeriodo));
        Set<UUID> idcursosValidos =new HashSet<>();
        
        for (CursoPeriodo cp : periodoRepo.listarTodos()) {
            if (cp.getIdPeriodoAcademico().equals(idPeriodo)) {
                idcursosValidos.add(cp.getIdCursoPeriodo());
            }
        }
        List<CursoProfesor> delPeriodo= new ArrayList<>();
        for (CursoProfesor cursop : repositorio.listarTodos()) {
            if (idcursosValidos.contains(cursop.getIdCurso())) {
                delPeriodo.add(cursop);
            }
        }
        if (delPeriodo.isEmpty()) {
            return List.of();
        }
        delPeriodo.sort(Comparator.comparing(CursoProfesor::getFechaAsisgnacion).reversed());
        return hidratarYMapearListaPeriodo(delPeriodo, periodoAcademico);
    }
    
    public List<cursoProfesorDto> listarPorMateriaPeriodo(Integer idMateria, Integer idPeriodo){
      Materia materia =materiaRepo.buscarPorId(idMateria).orElseThrow(()->new IllegalArgumentException("No se encontro materia"));
      PeriodoAcademico periodoAcademico= academicoRepo.buscarPorId(idPeriodo).orElseThrow(()->new IllegalArgumentException("No se encontro Periodo"));
      Set<UUID>idsCursosValidos = new HashSet<>();
        for (CursoPeriodo cp : periodoRepo.listarTodos()) {
            if (cp.getIdMateria().equals(idMateria)&& cp.getIdPeriodoAcademico().equals(idPeriodo)) {
                idsCursosValidos.add(cp.getIdCursoPeriodo());
            }
        }
        List<CursoProfesor> filtradas = new ArrayList<>();
        for (CursoProfesor cursop :repositorio.listarTodos()) {
            if (idsCursosValidos.contains(cursop.getIdCurso())) {
                filtradas.add(cursop);
            }
        }
        if (filtradas.isEmpty()) {
            return  List.of();
        }
        filtradas.sort(Comparator.comparing(CursoProfesor::getFechaAsisgnacion).reversed());
        return hidratarYMapearListaPeriodoYMateria(filtradas, periodoAcademico, materia);
    }
}
