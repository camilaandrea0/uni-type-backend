package edu.unimag.config.modulos;

import edu.unimag.config.modulos.api.ModuloConfigurable;
import com.cleandev.cli.core.SystemModule;
import com.cleandev.tpa.api.TpaRepository;
import com.cleandev.tpa.api.TpaRepositoryFactory;
import edu.unimag.config.RutaPersistencia;
import edu.unimag.controlador.PlanEstudioControlador;
import edu.unimag.mapeador.FacultadMapeador;
import edu.unimag.mapeador.PlanEstudioMapeador;
import edu.unimag.mapeador.ProgramaMapeador;
import edu.unimag.modelo.PlanEstudio;
import edu.unimag.persistencia.PlanEstudioRepositorioImpl;
import edu.unimag.repositorio.FacultadRepositorio;
import edu.unimag.repositorio.PlanEstudioRepositorio;
import edu.unimag.repositorio.ProgramaRepositorio;
import edu.unimag.servicio.PlanEstudioServicio;
import edu.unimag.servicio.ProgramaServicio;
import edu.unimag.vista.PlanEstudioVista;

public class ConfiguracionModuloPlanEstudio implements ModuloConfigurable {

    private final PlanEstudioRepositorio planEstudioRepositorio;
    private final ProgramaRepositorio programaRepositorio;
    private final ProgramaServicio programaServicio;
    private final FacultadRepositorio facultadRepositorio;
    private final PlanEstudioServicio planEstudioServicio;

    public ConfiguracionModuloPlanEstudio(
            ProgramaRepositorio programaRepositorio,
            ProgramaServicio programaServicio,
            FacultadRepositorio facultadRepositorio
    ) {
        if (programaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de programas no puede ser nulo");
        }
        if (programaServicio == null) {
            throw new IllegalArgumentException("El servicio de programas no puede ser nulo");
        }
        if (facultadRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de facultades no puede ser nulo");
        }

        this.programaRepositorio = programaRepositorio;
        this.programaServicio = programaServicio;
        this.facultadRepositorio = facultadRepositorio;

        // PlanEstudio usa Long como ID (necesitaUUID = false)
        boolean necesitaUUID = false;
        TpaRepository<PlanEstudio, Long> tpaEngine = TpaRepositoryFactory.create(
                PlanEstudio.class,
                RutaPersistencia.PLANES_ESTUDIO.obtenerRuta(),
                necesitaUUID
        );
        this.planEstudioRepositorio = new PlanEstudioRepositorioImpl(tpaEngine);

        // Crear mapeadores de la cadena de hidratación
        FacultadMapeador facultadMapper = new FacultadMapeador();
        ProgramaMapeador programaMapper = new ProgramaMapeador(facultadMapper);
        PlanEstudioMapeador planEstudioMapper = new PlanEstudioMapeador(programaMapper);

        // Crear el servicio en el constructor para poder exponerlo
        this.planEstudioServicio = new PlanEstudioServicio(
                this.planEstudioRepositorio,
                this.programaRepositorio,
                this.facultadRepositorio,
                planEstudioMapper
        );
    }

    public PlanEstudioRepositorio getPlanEstudioRepositorio() {
        return planEstudioRepositorio;
    }

    public PlanEstudioServicio getPlanEstudioServicio() {
        return planEstudioServicio;
    }

    @Override
    public SystemModule construirVista() {
        PlanEstudioControlador controller = new PlanEstudioControlador(
                this.planEstudioServicio,
                this.programaServicio
        );

        return new PlanEstudioVista(controller);
    }

    @Override
    public void cerrarRecursos() {
        if (planEstudioRepositorio != null) {
            planEstudioRepositorio.cerrar();
        }
    }
}
