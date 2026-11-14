package edu.unimag.config.modulos;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.tpa.api.TpaRepository;
import com.cleandev.tpa.api.TpaRepositoryFactory;
import edu.unimag.config.RutaPersistencia;
import edu.unimag.config.modulos.api.ModuloConfigurable;
import edu.unimag.controlador.ControladorCursoProfesor;
import edu.unimag.mapeador.CursoPeriodoMapeador;
import edu.unimag.mapeador.MapeadorCursoProfesor;
import edu.unimag.mapeador.MateriaMapeador;
import edu.unimag.mapeador.PeriodoAcademicoMapeador;
import edu.unimag.mapeador.ProfesorMapeador;
import edu.unimag.modelo.CursoProfesor;
import edu.unimag.modelo.pk.CursoProfesorPk;
import edu.unimag.persistencia.CursoProfesorRepositorioImpl;
import edu.unimag.repositorio.CursoPeriodoRepositorio;
import edu.unimag.repositorio.CursoProfesorRepositorio;
import edu.unimag.repositorio.MateriaRepositorio;
import edu.unimag.repositorio.PeriodoAcademicoRepositorio;
import edu.unimag.repositorio.ProfesorRepositorio;
import edu.unimag.servicio.CursoPeriodoServicio;
import edu.unimag.servicio.CursoProfesorServicio;
import edu.unimag.servicio.ProfesorServicio;
import edu.unimag.vista.CursoProfesorVista;

public class ConfiguracionModuloCursoProfesor implements ModuloConfigurable{

    private final CursoProfesorRepositorio cursoProfesorRepositorio;
    private final CursoPeriodoRepositorio cursoPeriodoRepositorio; 
    private final CursoPeriodoServicio cursoPeriodoServicio;
    private final ProfesorRepositorio profesorRepositorio;
    private final ProfesorServicio profesorServicio;
    private final MateriaRepositorio materiaRepositorio;
    private final PeriodoAcademicoRepositorio academicoRepositorio;
    private final CursoProfesorServicio cursoProfesorServicio;

    public ConfiguracionModuloCursoProfesor(CursoPeriodoRepositorio cursoPeriodoRepositorio, CursoPeriodoServicio cursoPeriodoServicio, ProfesorRepositorio profesorRepositorio, ProfesorServicio profesorServicio, MateriaRepositorio materiaRepositorio, PeriodoAcademicoRepositorio academicoRepositorio) {
        
        if (cursoPeriodoRepositorio==null) {
            throw new IllegalArgumentException("El repositorio del curso periodo no puede ser nulo");
        }if (cursoPeriodoServicio==null) {
            throw new IllegalArgumentException("El servicio del curso periodo no puede ser nulo");
        }if (profesorRepositorio==null) {
            throw new IllegalArgumentException("El repositorio del profesor no puede ser nulo");
        }if (profesorServicio==null) {
            throw new IllegalArgumentException("El servicio del profesor no puede ser nulo");
        }if (materiaRepositorio==null) {
            throw new IllegalArgumentException("El repositorio de la materia no puede ser nulo");
        }if (academicoRepositorio==null) {
            throw new IllegalArgumentException("El repositorio del periodo academico no puede ser nulo");
        }
        this.cursoPeriodoRepositorio = cursoPeriodoRepositorio;
        this.cursoPeriodoServicio = cursoPeriodoServicio;
        this.profesorRepositorio = profesorRepositorio;
        this.profesorServicio = profesorServicio;
        this.materiaRepositorio = materiaRepositorio;
        this.academicoRepositorio = academicoRepositorio;
        
        boolean necesitaUUID= false;
        TpaRepository<CursoProfesor, CursoProfesorPk> tpaEngine= TpaRepositoryFactory.create(CursoProfesor.class, RutaPersistencia.CURSOS_PROFESORES.obtenerRuta(), necesitaUUID);
        this.cursoProfesorRepositorio = new CursoProfesorRepositorioImpl(tpaEngine);
        
        MateriaMapeador materiaMapeador= new MateriaMapeador();
        PeriodoAcademicoMapeador periodoAcademicoMapeador=new PeriodoAcademicoMapeador();
        CursoPeriodoMapeador cursoPeriodoMapeador= new CursoPeriodoMapeador(materiaMapeador, periodoAcademicoMapeador);
        ProfesorMapeador profesorMapeador= new ProfesorMapeador();
        MapeadorCursoProfesor mapeadorCursoProfesor= new MapeadorCursoProfesor(profesorMapeador, cursoPeriodoMapeador);
        this.cursoProfesorServicio = new CursoProfesorServicio(this.cursoProfesorRepositorio,
               this.cursoPeriodoRepositorio,
                mapeadorCursoProfesor,
                this.academicoRepositorio,
                this.materiaRepositorio,
                this.profesorRepositorio);
    }   
        

    @Override
    public SystemModule construirVista() {
       
     ControladorCursoProfesor controller= new ControladorCursoProfesor(cursoPeriodoServicio,profesorServicio,cursoProfesorServicio );
        
        return new CursoProfesorVista(controller);
    }

    @Override
    public void cerrarRecursos() {
        if (cursoProfesorRepositorio != null) {
            cursoProfesorRepositorio.cerrar();
        }
    }

    public CursoProfesorRepositorio getCursoProfesorRepositorio() {
        return cursoProfesorRepositorio;
    }

    public CursoProfesorServicio getCursoProfesorServicio() {
        return cursoProfesorServicio;
    }

    

}
