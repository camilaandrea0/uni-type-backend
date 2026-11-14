package edu.unimag.persistencia;

import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.Programa;
import edu.unimag.repositorio.ProgramaRepositorio;

import java.util.UUID;

public class ProgramaRepositorioImpl
        extends RepositorioBaseAbstracto<Programa, UUID>
        implements ProgramaRepositorio {

    public ProgramaRepositorioImpl(TpaRepository<Programa, UUID> tpaRepository) {
        super(tpaRepository);
    }

    @Override
    public boolean insertarSiNombreNoExiste(String nombre, Programa programa) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacio");
        }
        if (programa == null) {
            throw new IllegalArgumentException("El programa no puede ser nulo");
        }

        return tpaRepository.executeAtomic(repo -> {
            // 1. Verificar que el nombre no existe (case-insensitive)
            for (Programa p : repo.findAll()) {
                if (p.getNombrePrograma().equalsIgnoreCase(nombre)) {
                    return false;
                }
            }

            // 2. No existe, insertar
            repo.save(programa);
            return true;
        });
    }

    @Override
    public boolean actualizarSiNoExisteEnOtra(Programa programaModificado, boolean verificarNombre) {
        if (programaModificado == null) {
            throw new IllegalArgumentException("El programa no puede ser nulo");
        }
        if (programaModificado.getIdPrograma() == null) {
            throw new IllegalArgumentException("El ID del programa no puede ser nulo");
        }

        return tpaRepository.executeAtomic(repo -> {
            UUID id = programaModificado.getIdPrograma();
            Programa existente = repo.findById(id).orElse(null);
            if (existente == null) {
                throw new IllegalArgumentException("No existe programa con ID: " + id);
            }

            if (verificarNombre) {
                String nombre = programaModificado.getNombrePrograma();

                for (Programa p : repo.findAll()) {
                    // Excluir el programa actual (puede "actualizar" al mismo nombre)
                    if (!p.getIdPrograma().equals(id)
                            && p.getNombrePrograma().equalsIgnoreCase(nombre)) {
                        return false;
                    }
                }
            }
            repo.update(programaModificado);
            return true;
        });
    }
}
