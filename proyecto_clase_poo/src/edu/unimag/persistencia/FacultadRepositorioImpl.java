package edu.unimag.persistencia;

import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.Facultad;
import edu.unimag.repositorio.FacultadRepositorio;

public class FacultadRepositorioImpl
        extends RepositorioBaseAbstracto<Facultad, Long>
        implements FacultadRepositorio {

    public FacultadRepositorioImpl(TpaRepository<Facultad, Long> tpaRepository) {
        super(tpaRepository);
    }

    @Override
    public boolean insertarSiNombreNoExiste(String nombre, Facultad facultad) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacio");
        }
        if (facultad == null) {
            throw new IllegalArgumentException("La facultad no puede ser nula");
        }

        return tpaRepository.executeAtomic(repo -> {
            for (Facultad f : repo.findAll()) {
                if (f.getNombreFacultad().equalsIgnoreCase(nombre)) {
                    return false; // Ya existe, no insertar
                }
            }

            repo.save(facultad);
            return true;
        });
    }

    @Override
    public boolean actualizarNombreSiNoExisteEnOtra(Long idFacultad, String nuevoNombre) {
        if (idFacultad == null) {
            throw new IllegalArgumentException("El ID de la facultad no puede ser nulo");
        }
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new IllegalArgumentException("El nuevo nombre no puede ser nulo ni vacio");
        }

        return tpaRepository.executeAtomic(repo -> {
            Facultad facultad = repo.findById(idFacultad).orElse(null);
            if (facultad == null) {
                throw new IllegalArgumentException("No existe facultad con ID: " + idFacultad);
            }

            for (Facultad f : repo.findAll()) {
                // Excluyo la facultad actual
                if (!f.getIdFacultad().equals(idFacultad)
                        && f.getNombreFacultad().equalsIgnoreCase(nuevoNombre)) {
                    return false;
                }
            }

            facultad.actualizarNombre(nuevoNombre);
            repo.update(facultad);
            return true;
        });
    }
}
