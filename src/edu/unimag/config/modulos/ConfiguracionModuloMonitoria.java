package edu.unimag.config.modulos;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.tpa.api.TpaRepository;
import com.cleandev.tpa.api.TpaRepositoryFactory;
import edu.unimag.config.RutaPersistencia;
import edu.unimag.config.modulos.api.ModuloConfigurable;
import edu.unimag.controlador.ControladorMonitoria;
import edu.unimag.mapeador.CursoPeriodoMapeador;
import edu.unimag.mapeador.EstudianteMapeador;
import edu.unimag.mapeador.MapeadorMonitoria;
import edu.unimag.mapeador.MateriaMapeador;
import edu.unimag.mapeador.PeriodoAcademicoMapeador;
import edu.unimag.mapeador.ProfesorMapeador;
import edu.unimag.modelo.Monitoria;
import edu.unimag.modelo.pk.MonitoriaPk;
import edu.unimag.persistencia.MonitoriaRepositorioImpl;
import edu.unimag.repositorio.CursoPeriodoRepositorio;
import edu.unimag.repositorio.EstudianteRepositorio;
import edu.unimag.repositorio.MateriaRepositorio;
import edu.unimag.repositorio.MonitoriaRepositorio;
import edu.unimag.repositorio.PeriodoAcademicoRepositorio;
import edu.unimag.repositorio.ProfesorRepositorio;
import edu.unimag.servicio.CursoPeriodoServicio;
import edu.unimag.servicio.EstudianteServicio;
import edu.unimag.servicio.MonitoriaServicio;
import edu.unimag.servicio.ProfesorServicio;
import edu.unimag.vista.MonitoriaVista;

public class ConfiguracionModuloMonitoria implements ModuloConfigurable {

    private final MonitoriaRepositorio monitoriaRepositorio;
    private final CursoPeriodoRepositorio cursoPeriodoRepositorio;
    private final CursoPeriodoServicio cursoPeriodoServicio;
    private final EstudianteRepositorio estudianteRepositorio;
    private final EstudianteServicio estudianteServicio;
    private final ProfesorRepositorio profesorRepositorio;
    private final ProfesorServicio profesorServicio;
    private final MonitoriaServicio monitoriaServicio;

    public ConfiguracionModuloMonitoria(
            CursoPeriodoRepositorio cursoPeriodoRepositorio,
            CursoPeriodoServicio cursoPeriodoServicio,
            EstudianteRepositorio estudianteRepositorio,
            EstudianteServicio estudianteServicio,
            ProfesorRepositorio profesorRepositorio,
            ProfesorServicio profesorServicio,
            MateriaRepositorio materiaRepositorio,
            PeriodoAcademicoRepositorio periodoAcademicoRepositorio
    ) {
        if (cursoPeriodoRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de cursos periodo no puede ser nulo");
        }
        if (cursoPeriodoServicio == null) {
            throw new IllegalArgumentException("El servicio de cursos periodo no puede ser nulo");
        }
        if (estudianteRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de estudiantes no puede ser nulo");
        }
        if (estudianteServicio == null) {
            throw new IllegalArgumentException("El servicio de estudiantes no puede ser nulo");
        }
        if (profesorRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de profesores no puede ser nulo");
        }
        if (profesorServicio == null) {
            throw new IllegalArgumentException("El servicio de profesores no puede ser nulo");
        }
        if (materiaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de materias no puede ser nulo");
        }
        if (periodoAcademicoRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de periodos academicos no puede ser nulo");
        }

        this.cursoPeriodoRepositorio = cursoPeriodoRepositorio;
        this.cursoPeriodoServicio = cursoPeriodoServicio;
        this.estudianteRepositorio = estudianteRepositorio;
        this.estudianteServicio = estudianteServicio;
        this.profesorRepositorio = profesorRepositorio;
        this.profesorServicio = profesorServicio;

        boolean necesitaUUID = false;
        TpaRepository<Monitoria, MonitoriaPk> tpaEngine = TpaRepositoryFactory.create(
                Monitoria.class,
                RutaPersistencia.MONITORIAS.obtenerRuta(),
                necesitaUUID
        );
        this.monitoriaRepositorio = new MonitoriaRepositorioImpl(tpaEngine);

        MateriaMapeador materiaMapeador = new MateriaMapeador();
        PeriodoAcademicoMapeador periodoAcademicoMapeador = new PeriodoAcademicoMapeador();
        CursoPeriodoMapeador cursoPeriodoMapeador = new CursoPeriodoMapeador(
                materiaMapeador,
                periodoAcademicoMapeador
        );
        EstudianteMapeador estudianteMapeador = new EstudianteMapeador();
        ProfesorMapeador profesorMapeador = new ProfesorMapeador();
        MapeadorMonitoria mapeador = new MapeadorMonitoria(
                cursoPeriodoMapeador,
                estudianteMapeador,
                profesorMapeador
        );

        this.monitoriaServicio = new MonitoriaServicio(
                this.monitoriaRepositorio,
                this.cursoPeriodoRepositorio,
                this.estudianteRepositorio,
                this.profesorRepositorio,
                materiaRepositorio,
                periodoAcademicoRepositorio,
                mapeador
        );
    }

    public MonitoriaRepositorio getMonitoriaRepositorio() {
        return monitoriaRepositorio;
    }

    public MonitoriaServicio getMonitoriaServicio() {
        return monitoriaServicio;
    }

    @Override
    public SystemModule construirVista() {
        ControladorMonitoria controller = new ControladorMonitoria(
                this.monitoriaServicio,
                this.cursoPeriodoServicio,
                this.estudianteServicio,
                this.profesorServicio
        );
        return new MonitoriaVista(controller);
    }

    @Override
    public void cerrarRecursos() {
        if (monitoriaRepositorio != null) {
            monitoriaRepositorio.cerrar();
        }
    }
}
