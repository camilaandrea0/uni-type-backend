package edu.unimag.persistencia;

import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.Estudiante;
import edu.unimag.repositorio.EstudianteRepositorio;

import java.util.UUID;

public class EstudianteRepositorioImpl
        extends RepositorioBaseAbstracto<Estudiante, UUID>
        implements EstudianteRepositorio {

    public EstudianteRepositorioImpl(TpaRepository<Estudiante, UUID> tpaRepository) {
        super(tpaRepository);
    }

    @Override
    protected boolean permiteBorradoFisico() {
        return true;
    }

    @Override
    public boolean insertarSiCodigoNoExiste(String codigo, Estudiante estudiante) {
        if (codigo == null || codigo.isBlank()) {
            throw new IllegalArgumentException("El codigo no puede ser nulo ni vacio");
        }
        if (estudiante == null) {
            throw new IllegalArgumentException("El estudiante no puede ser nulo");
        }
        return tpaRepository.executeAtomic(repo -> {
            for (Estudiante e : repo.findAll()) {
                if (e.getCodigoEstudiante().equalsIgnoreCase(codigo)) {
                    return false;
                }
            }

            repo.save(estudiante);
            return true;
        });
    }

// No lo voy a borrar para usarlo más adelante en otro caso
//    @Override
//    public Optional<Estudiante> buscarPorCodigo(String codigo) {
//        List<Estudiante> todosLosEstudiantes = listarTodos();
//        for (Estudiante estudiante : todosLosEstudiantes) {
//            if (estudiante.getCodigoEstudiante().equalsIgnoreCase(codigo)) {
//                return Optional.of(estudiante);
//            }
//        }
//        return Optional.empty();
//    }
}
