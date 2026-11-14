package edu.unimag.config.modulos;

import edu.unimag.config.modulos.api.ModuloConfigurable;
import com.cleandev.cli.core.SystemModule;
import com.cleandev.tpa.api.TpaRepository;
import com.cleandev.tpa.api.TpaRepositoryFactory;
import edu.unimag.config.RutaPersistencia;
import edu.unimag.controlador.ProgramaControlador;
import edu.unimag.mapeador.FacultadMapeador;
import edu.unimag.mapeador.ProgramaMapeador;
import edu.unimag.modelo.Programa;
import edu.unimag.persistencia.ProgramaRepositorioImpl;
import edu.unimag.repositorio.FacultadRepositorio;
import edu.unimag.repositorio.ProgramaRepositorio;
import edu.unimag.servicio.ProgramaServicio;
import edu.unimag.vista.ProgramaVista;

import java.util.UUID;

public class ConfiguracionModuloPrograma implements ModuloConfigurable {

    private final ProgramaRepositorio programaRepositorio;
    private final FacultadRepositorio facultadRepositorio;
    private final ProgramaServicio programaServicio;

    public ConfiguracionModuloPrograma(FacultadRepositorio facultadRepositorio) {
        if (facultadRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de facultades no puede ser nulo");
        }

        boolean necesitaUUID = true;
        this.facultadRepositorio = facultadRepositorio;

        TpaRepository<Programa, UUID> tpaEngine = TpaRepositoryFactory.create(
                Programa.class,
                RutaPersistencia.PROGRAMAS.obtenerRuta(),
                necesitaUUID
        );
        this.programaRepositorio = new ProgramaRepositorioImpl(tpaEngine);

        FacultadMapeador facultadMapper = new FacultadMapeador();
        ProgramaMapeador programaMapper = new ProgramaMapeador(facultadMapper);
        this.programaServicio = new ProgramaServicio(
                this.programaRepositorio,
                this.facultadRepositorio,
                programaMapper
        );
    }

    public ProgramaRepositorio getProgramaRepositorio() {
        return programaRepositorio;
    }

    public ProgramaServicio getProgramaServicio() {
        return programaServicio;
    }

    @Override
    public SystemModule construirVista() {
        ProgramaControlador controller = new ProgramaControlador(this.programaServicio);
        return new ProgramaVista(controller);
    }

    @Override
    public void cerrarRecursos() {
        if (programaRepositorio != null) {
            programaRepositorio.cerrar();
        }
    }
}
