package edu.unimag.persistencia;

import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.repositorio.CursoPeriodoRepositorio;

import java.util.UUID;

public class CursoPeriodoRepositorioImpl
        extends RepositorioBaseAbstracto<CursoPeriodo, UUID>
        implements CursoPeriodoRepositorio {

    public CursoPeriodoRepositorioImpl(TpaRepository<CursoPeriodo, UUID> tpaRepository) {
        super(tpaRepository);
    }

    @Override
    protected boolean permiteBorradoFisico() {
        return true;
    }

    @Override
    public boolean insertarSiCombinacionNoExiste(
            Integer idMateria,
            Integer idPeriodoAcademico,
            String nombre,
            CursoPeriodo cursoPeriodo
    ) {
        if (idMateria == null) {
            throw new IllegalArgumentException("El ID de la materia no puede ser nulo");
        }
        if (idPeriodoAcademico == null) {
            throw new IllegalArgumentException("El ID del periodo academico no puede ser nulo");
        }
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacio");
        }
        if (cursoPeriodo == null) {
            throw new IllegalArgumentException("El curso periodo no puede ser nulo");
        }

        return tpaRepository.executeAtomic(repo -> {
            for (CursoPeriodo cp : repo.findAll()) {
                if (cp.getIdMateria().equals(idMateria)
                        && cp.getIdPeriodoAcademico().equals(idPeriodoAcademico)
                        && cp.getNombreCursoPeriodo().equalsIgnoreCase(nombre)) {
                    return false;
                }
            }
            repo.save(cursoPeriodo);
            return true;
        });
    }

    @Override
    public boolean actualizarSiNoExisteEnOtra(CursoPeriodo cursoPeriodoModificado, boolean verificarNombre) {
        if (cursoPeriodoModificado == null) {
            throw new IllegalArgumentException("El curso periodo no puede ser nulo");
        }
        if (cursoPeriodoModificado.getIdCursoPeriodo() == null) {
            throw new IllegalArgumentException("El ID del curso periodo no puede ser nulo");
        }

        return tpaRepository.executeAtomic(repo -> {
            UUID id = cursoPeriodoModificado.getIdCursoPeriodo();

            CursoPeriodo existente = repo.findById(id).orElse(null);
            if (existente == null) {
                throw new IllegalArgumentException("No existe curso periodo con ID: " + id);
            }

            if (verificarNombre) {
                Integer idMateria = cursoPeriodoModificado.getIdMateria();
                Integer idPeriodo = cursoPeriodoModificado.getIdPeriodoAcademico();
                String nombre = cursoPeriodoModificado.getNombreCursoPeriodo();

                for (CursoPeriodo cp : repo.findAll()) {
                    if (!cp.getIdCursoPeriodo().equals(id)
                            && cp.getIdMateria().equals(idMateria)
                            && cp.getIdPeriodoAcademico().equals(idPeriodo)
                            && cp.getNombreCursoPeriodo().equalsIgnoreCase(nombre)) {
                        return false;
                    }
                }
            }
            repo.update(cursoPeriodoModificado);
            return true;
        });
    }
}
