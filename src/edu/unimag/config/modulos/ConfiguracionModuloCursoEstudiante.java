package edu.unimag.config.modulos;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.tpa.api.TpaRepository;
import com.cleandev.tpa.api.TpaRepositoryFactory;
import edu.unimag.config.RutaPersistencia;
import edu.unimag.config.modulos.api.ModuloConfigurable;
import edu.unimag.controlador.ControladorCursoEstudiante;
import edu.unimag.mapeador.CursoPeriodoMapeador;
import edu.unimag.mapeador.EstudianteMapeador;
import edu.unimag.mapeador.MapeadorCursoEstudiante;
import edu.unimag.mapeador.MateriaMapeador;
import edu.unimag.mapeador.PeriodoAcademicoMapeador;
import edu.unimag.modelo.CursoEstudiante;
import edu.unimag.modelo.pk.CursoEstudiantePk;
import edu.unimag.persistencia.CursoEstudianteRepositorioImpl;
import edu.unimag.repositorio.CursoEstudianteRepositorio;
import edu.unimag.repositorio.CursoPeriodoRepositorio;
import edu.unimag.repositorio.EstudianteRepositorio;
import edu.unimag.repositorio.MateriaRepositorio;
import edu.unimag.repositorio.PeriodoAcademicoRepositorio;
import edu.unimag.servicio.CursoEstudianteServicio;
import edu.unimag.servicio.CursoPeriodoServicio;
import edu.unimag.servicio.EstudianteServicio;
import edu.unimag.vista.CursoEstudianteVista;

public class ConfiguracionModuloCursoEstudiante implements ModuloConfigurable {

    private final CursoEstudianteRepositorio cursoEstudianteRepositorio;
    private final EstudianteRepositorio estudianteRepositorio;
    private final EstudianteServicio estudianteServicio;
    private final CursoPeriodoRepositorio cursoPeriodoRepositorio;
    private final CursoPeriodoServicio cursoPeriodoServicio;
    private final CursoEstudianteServicio cursoEstudianteServicio;

    public ConfiguracionModuloCursoEstudiante(
            EstudianteRepositorio estudianteRepositorio,
            EstudianteServicio estudianteServicio,
            CursoPeriodoRepositorio cursoPeriodoRepositorio,
            CursoPeriodoServicio cursoPeriodoServicio,
            MateriaRepositorio materiaRepositorio,
            PeriodoAcademicoRepositorio periodoAcademicoRepositorio
    ) {
        if (estudianteRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de estudiantes no puede ser nulo");
        }
        if (estudianteServicio == null) {
            throw new IllegalArgumentException("El servicio de estudiantes no puede ser nulo");
        }
        if (cursoPeriodoRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de cursos periodo no puede ser nulo");
        }
        if (cursoPeriodoServicio == null) {
            throw new IllegalArgumentException("El servicio de cursos periodo no puede ser nulo");
        }
        if (materiaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de materias no puede ser nulo");
        }
        if (periodoAcademicoRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de periodos academicos no puede ser nulo");
        }

        this.estudianteRepositorio = estudianteRepositorio;
        this.estudianteServicio = estudianteServicio;
        this.cursoPeriodoRepositorio = cursoPeriodoRepositorio;
        this.cursoPeriodoServicio = cursoPeriodoServicio;

        boolean necesitaUUID = false;
        TpaRepository<CursoEstudiante, CursoEstudiantePk> tpaEngine = TpaRepositoryFactory.create(
                CursoEstudiante.class,
                RutaPersistencia.CURSOS_ESTUDIANTES.obtenerRuta(),
                necesitaUUID
        );
        this.cursoEstudianteRepositorio = new CursoEstudianteRepositorioImpl(tpaEngine);

        EstudianteMapeador estudianteMapeador = new EstudianteMapeador();
        MateriaMapeador materiaMapeador = new MateriaMapeador();
        PeriodoAcademicoMapeador periodoAcademicoMapeador = new PeriodoAcademicoMapeador();
        CursoPeriodoMapeador cursoPeriodoMapeador = new CursoPeriodoMapeador(
                materiaMapeador,
                periodoAcademicoMapeador
        );
        MapeadorCursoEstudiante mapeador = new MapeadorCursoEstudiante(
                estudianteMapeador,
                cursoPeriodoMapeador
        );

        this.cursoEstudianteServicio = new CursoEstudianteServicio(
                this.cursoEstudianteRepositorio,
                this.estudianteRepositorio,
                this.cursoPeriodoRepositorio,
                materiaRepositorio,
                periodoAcademicoRepositorio,
                mapeador
        );
    }

    public CursoEstudianteRepositorio getCursoEstudianteRepositorio() {
        return cursoEstudianteRepositorio;
    }

    public CursoEstudianteServicio getCursoEstudianteServicio() {
        return cursoEstudianteServicio;
    }

    @Override
    public SystemModule construirVista() {
        ControladorCursoEstudiante controller = new ControladorCursoEstudiante(
                this.cursoEstudianteServicio,
                this.estudianteServicio,
                this.cursoPeriodoServicio
        );
        return new CursoEstudianteVista(controller);
    }

    @Override
    public void cerrarRecursos() {
        if (cursoEstudianteRepositorio != null) {
            cursoEstudianteRepositorio.cerrar();
        }
    }
}
