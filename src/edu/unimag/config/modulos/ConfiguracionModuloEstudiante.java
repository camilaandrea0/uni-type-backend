package edu.unimag.config.modulos;

import edu.unimag.config.modulos.api.ModuloConfigurable;
import com.cleandev.cli.core.SystemModule;
import com.cleandev.tpa.api.TpaRepository;
import com.cleandev.tpa.api.TpaRepositoryFactory;
import edu.unimag.config.RutaPersistencia;
import edu.unimag.controlador.EstudianteControlador;
import edu.unimag.mapeador.EstudianteMapeador;
import edu.unimag.modelo.Estudiante;
import edu.unimag.persistencia.EstudianteRepositorioImpl;
import edu.unimag.repositorio.EstudianteRepositorio;
import edu.unimag.servicio.EstudianteServicio;
import edu.unimag.vista.EstudianteVista;

import java.util.UUID;

public class ConfiguracionModuloEstudiante implements ModuloConfigurable {

    private final EstudianteRepositorio estudianteRepositorio;
    private final EstudianteServicio estudianteServicio;

    public ConfiguracionModuloEstudiante() {
        boolean necesitaUUID = true;
        TpaRepository<Estudiante, UUID> tpaEngine = TpaRepositoryFactory.create(
                Estudiante.class,
                RutaPersistencia.ESTUDIANTES.obtenerRuta(),
                necesitaUUID
        );
        this.estudianteRepositorio = new EstudianteRepositorioImpl(tpaEngine);

        // Crear el servicio en el constructor para poder exponerlo
        EstudianteMapeador estudianteMapper = new EstudianteMapeador();
        this.estudianteServicio = new EstudianteServicio(
                this.estudianteRepositorio,
                estudianteMapper
        );
    }

    public EstudianteRepositorio getEstudianteRepositorio() {
        return estudianteRepositorio;
    }

    public EstudianteServicio getEstudianteServicio() {
        return estudianteServicio;
    }

    @Override
    public SystemModule construirVista() {
        EstudianteControlador controller = new EstudianteControlador(this.estudianteServicio);
        return new EstudianteVista(controller);
    }

    @Override
    public void cerrarRecursos() {
        if (estudianteRepositorio != null) {
            estudianteRepositorio.cerrar();
        }
    }
}
