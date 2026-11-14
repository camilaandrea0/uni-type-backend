package edu.unimag.config.modulos;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.tpa.api.TpaRepository;
import com.cleandev.tpa.api.TpaRepositoryFactory;
import edu.unimag.config.RutaPersistencia;
import edu.unimag.config.modulos.api.ModuloConfigurable;
import edu.unimag.controlador.ControladorPlanEstudioMateria;
import edu.unimag.mapeador.FacultadMapeador;
import edu.unimag.mapeador.MapeadorPlanEstudioMateria;
import edu.unimag.mapeador.MateriaMapeador;
import edu.unimag.mapeador.PlanEstudioMapeador;
import edu.unimag.mapeador.ProgramaMapeador;
import edu.unimag.modelo.PlanEstudioMateria;
import edu.unimag.modelo.pk.PlanEstudioMateriaPk;
import edu.unimag.persistencia.PlanEstudioMateriaRepositorioImpl;
import edu.unimag.repositorio.FacultadRepositorio;
import edu.unimag.repositorio.MateriaRepositorio;
import edu.unimag.repositorio.PlanEstudioMateriaRepositorio;
import edu.unimag.repositorio.PlanEstudioRepositorio;
import edu.unimag.repositorio.ProgramaRepositorio;
import edu.unimag.servicio.MateriaServicio;
import edu.unimag.servicio.PlanEstudioMateriaServicio;
import edu.unimag.servicio.PlanEstudioServicio;
import edu.unimag.vista.PlanEstudioMateriaVista;

public class ConfiguracionModuloPlanEstudioMateria implements ModuloConfigurable {

    private final PlanEstudioMateriaRepositorio planEstudioMateriaRepositorio;
    private final PlanEstudioRepositorio planEstudioRepositorio;
    private final PlanEstudioServicio planEstudioServicio;
    private final MateriaRepositorio materiaRepositorio;
    private final MateriaServicio materiaServicio;
    private final PlanEstudioMateriaServicio planEstudioMateriaServicio;

    public ConfiguracionModuloPlanEstudioMateria(
            PlanEstudioRepositorio planEstudioRepositorio,
            PlanEstudioServicio planEstudioServicio,
            MateriaRepositorio materiaRepositorio,
            MateriaServicio materiaServicio,
            ProgramaRepositorio programaRepositorio,
            FacultadRepositorio facultadRepositorio
    ) {
        if (planEstudioRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de planes de estudio no puede ser nulo");
        }
        if (planEstudioServicio == null) {
            throw new IllegalArgumentException("El servicio de planes de estudio no puede ser nulo");
        }
        if (materiaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de materias no puede ser nulo");
        }
        if (materiaServicio == null) {
            throw new IllegalArgumentException("El servicio de materias no puede ser nulo");
        }
        if (programaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de programas no puede ser nulo");
        }
        if (facultadRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de facultades no puede ser nulo");
        }

        this.planEstudioRepositorio = planEstudioRepositorio;
        this.planEstudioServicio = planEstudioServicio;
        this.materiaRepositorio = materiaRepositorio;
        this.materiaServicio = materiaServicio;

        boolean necesitaUUID = false;
        TpaRepository<PlanEstudioMateria, PlanEstudioMateriaPk> tpaEngine = TpaRepositoryFactory.create(
                PlanEstudioMateria.class,
                RutaPersistencia.PLANES_MATERIAS.obtenerRuta(),
                necesitaUUID
        );
        this.planEstudioMateriaRepositorio = new PlanEstudioMateriaRepositorioImpl(tpaEngine);

        FacultadMapeador facultadMapeador = new FacultadMapeador();
        ProgramaMapeador programaMapeador = new ProgramaMapeador(facultadMapeador);
        PlanEstudioMapeador planEstudioMapeador = new PlanEstudioMapeador(programaMapeador);
        MateriaMapeador materiaMapeador = new MateriaMapeador();
        MapeadorPlanEstudioMateria mapeador = new MapeadorPlanEstudioMateria(
                planEstudioMapeador,
                materiaMapeador
        );

        this.planEstudioMateriaServicio = new PlanEstudioMateriaServicio(
                this.planEstudioMateriaRepositorio,
                this.planEstudioRepositorio,
                this.materiaRepositorio,
                programaRepositorio,
                facultadRepositorio,
                mapeador
        );
    }

    public PlanEstudioMateriaRepositorio getPlanEstudioMateriaRepositorio() {
        return planEstudioMateriaRepositorio;
    }

    public PlanEstudioMateriaServicio getPlanEstudioMateriaServicio() {
        return planEstudioMateriaServicio;
    }

    @Override
    public SystemModule construirVista() {
        ControladorPlanEstudioMateria controller = new ControladorPlanEstudioMateria(
                this.planEstudioMateriaServicio,
                this.planEstudioServicio,
                this.materiaServicio
        );
        return new PlanEstudioMateriaVista(controller);
    }

    @Override
    public void cerrarRecursos() {
        if (planEstudioMateriaRepositorio != null) {
            planEstudioMateriaRepositorio.cerrar();
        }
    }
}
