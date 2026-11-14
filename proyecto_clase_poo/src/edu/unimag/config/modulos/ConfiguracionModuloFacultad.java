package edu.unimag.config.modulos;

import edu.unimag.config.modulos.api.ModuloConfigurable;
import com.cleandev.cli.core.SystemModule;
import com.cleandev.tpa.api.TpaRepository;
import com.cleandev.tpa.api.TpaRepositoryFactory;
import edu.unimag.config.RutaPersistencia;
import edu.unimag.controlador.FacultadControlador;
import edu.unimag.mapeador.FacultadMapeador;
import edu.unimag.modelo.Facultad;
import edu.unimag.persistencia.FacultadRepositorioImpl;
import edu.unimag.repositorio.FacultadRepositorio;
import edu.unimag.servicio.FacultadServicio;
import edu.unimag.vista.FacultadVista;

public class ConfiguracionModuloFacultad implements ModuloConfigurable {

    private final FacultadRepositorio facultadRepositorio;
    private final FacultadServicio facultadServicio;
    
    public ConfiguracionModuloFacultad() {
        boolean necesitaUUID = false;
        TpaRepository<Facultad, Long> tpaEngine = TpaRepositoryFactory.create(
                Facultad.class,
                RutaPersistencia.FACULTADES.obtenerRuta(),
                necesitaUUID
        );
        this.facultadRepositorio = new FacultadRepositorioImpl(tpaEngine);
        FacultadMapeador mapper = new FacultadMapeador();
       facultadServicio= new FacultadServicio(facultadRepositorio, mapper);
    }

    public FacultadRepositorio getFacultadRepositorio() {
        return facultadRepositorio;
    }

    public FacultadServicio getFacultadServicio() {
        return facultadServicio;
    }

    
    @Override
    public SystemModule construirVista() {
        
        FacultadControlador controller = new FacultadControlador(facultadServicio);
        return new FacultadVista(controller);
    }

    @Override
    public void cerrarRecursos() {
        if (facultadRepositorio != null) {
            facultadRepositorio.cerrar();
        }
    }
}
