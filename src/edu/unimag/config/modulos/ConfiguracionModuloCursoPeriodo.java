package edu.unimag.config.modulos;

import edu.unimag.config.modulos.api.ModuloConfigurable;
import com.cleandev.cli.core.SystemModule;
import com.cleandev.tpa.api.TpaRepository;
import com.cleandev.tpa.api.TpaRepositoryFactory;
import edu.unimag.config.RutaPersistencia;
import edu.unimag.controlador.CursoPeriodoControlador;
import edu.unimag.mapeador.CursoPeriodoMapeador;
import edu.unimag.mapeador.MateriaMapeador;
import edu.unimag.mapeador.PeriodoAcademicoMapeador;
import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.persistencia.CursoPeriodoRepositorioImpl;
import edu.unimag.repositorio.CursoPeriodoRepositorio;
import edu.unimag.repositorio.MateriaRepositorio;
import edu.unimag.repositorio.PeriodoAcademicoRepositorio;
import edu.unimag.servicio.CursoPeriodoServicio;
import edu.unimag.vista.CursoPeriodoVista;

import java.util.UUID;

public class ConfiguracionModuloCursoPeriodo implements ModuloConfigurable {

    private final CursoPeriodoRepositorio cursoPeriodoRepositorio;
    private final MateriaRepositorio materiaRepositorio;
    private final PeriodoAcademicoRepositorio periodoAcademicoRepositorio;
    private final CursoPeriodoServicio cursoPeriodoServicio;

    public ConfiguracionModuloCursoPeriodo(
            MateriaRepositorio materiaRepositorio,
            PeriodoAcademicoRepositorio periodoAcademicoRepositorio
    ) {
        if (materiaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de materias no puede ser nulo");
        }
        if (periodoAcademicoRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de periodos academicos no puede ser nulo");
        }

        this.materiaRepositorio = materiaRepositorio;
        this.periodoAcademicoRepositorio = periodoAcademicoRepositorio;

        boolean necesitaUUID = true;
        TpaRepository<CursoPeriodo, UUID> tpaEngine = TpaRepositoryFactory.create(
                CursoPeriodo.class,
                RutaPersistencia.CURSOS_PERIODOS.obtenerRuta(),
                necesitaUUID
        );
        this.cursoPeriodoRepositorio = new CursoPeriodoRepositorioImpl(tpaEngine);

        // Crear el servicio en el constructor para poder exponerlo
        MateriaMapeador materiaMapper = new MateriaMapeador();
        PeriodoAcademicoMapeador periodoMapper = new PeriodoAcademicoMapeador();
        CursoPeriodoMapeador cursoPeriodoMapper = new CursoPeriodoMapeador(materiaMapper, periodoMapper);
        this.cursoPeriodoServicio = new CursoPeriodoServicio(
                this.cursoPeriodoRepositorio,
                this.materiaRepositorio,
                this.periodoAcademicoRepositorio,
                cursoPeriodoMapper
        );
    }

    public CursoPeriodoRepositorio getCursoPeriodoRepositorio() {
        return cursoPeriodoRepositorio;
    }

    public CursoPeriodoServicio getCursoPeriodoServicio() {
        return cursoPeriodoServicio;
    }

    @Override
    public SystemModule construirVista() {
        CursoPeriodoControlador controller = new CursoPeriodoControlador(this.cursoPeriodoServicio);
        return new CursoPeriodoVista(controller);
    }

    @Override
    public void cerrarRecursos() {
        if (cursoPeriodoRepositorio != null) {
            cursoPeriodoRepositorio.cerrar();
        }
    }
}
