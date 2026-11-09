package edu.unimag.config.modulos;

import edu.unimag.config.modulos.api.ModuloConfigurable;
import com.cleandev.cli.core.SystemModule;
import com.cleandev.tpa.api.TpaRepository;
import com.cleandev.tpa.api.TpaRepositoryFactory;
import edu.unimag.config.RutaPersistencia;
import edu.unimag.controlador.FormacionProfesorControlador;
import edu.unimag.mapeador.FormacionProfesorMapeador;
import edu.unimag.mapeador.ProfesorMapeador;
import edu.unimag.modelo.FormacionProfesor;
import edu.unimag.persistencia.FormacionProfesorRepositorioImpl;
import edu.unimag.repositorio.FormacionProfesorRepositorio;
import edu.unimag.repositorio.ProfesorRepositorio;
import edu.unimag.servicio.FormacionProfesorServicio;
import edu.unimag.vista.FormacionProfesorVista;

public class ConfiguracionModuloFormacionProfesor implements ModuloConfigurable {

    private final FormacionProfesorRepositorio formacionProfesorRepositorio;
    private final ProfesorRepositorio profesorRepositorio;

    public ConfiguracionModuloFormacionProfesor(ProfesorRepositorio profesorRepoCompartido) {
        if (profesorRepoCompartido == null) {
            throw new IllegalArgumentException("El repositorio de profesores no puede ser nulo");
        }
        this.profesorRepositorio = profesorRepoCompartido;

        boolean necesitaUUID = false;
        TpaRepository<FormacionProfesor, Long> tpaFormacion = TpaRepositoryFactory.create(
                FormacionProfesor.class,
                RutaPersistencia.FORMACIONES_PROFESORES.obtenerRuta(),
                necesitaUUID
        );
        this.formacionProfesorRepositorio = new FormacionProfesorRepositorioImpl(tpaFormacion);
    }

    public FormacionProfesorRepositorio getFormacionRepositorio() {
        return formacionProfesorRepositorio;
    }

    @Override
    public SystemModule construirVista() {
        ProfesorMapeador profesorMapper = new ProfesorMapeador();
        FormacionProfesorMapeador mapeador = new FormacionProfesorMapeador(profesorMapper);

        FormacionProfesorServicio service = new FormacionProfesorServicio(
                this.formacionProfesorRepositorio,
                this.profesorRepositorio,
                mapeador
        );

        FormacionProfesorControlador controller = new FormacionProfesorControlador(service);
        return new FormacionProfesorVista(controller);
    }

    @Override
    public void cerrarRecursos() {
        if (formacionProfesorRepositorio != null) {
            formacionProfesorRepositorio.cerrar();
        }
    }
}
