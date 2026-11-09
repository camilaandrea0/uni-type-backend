package edu.unimag.persistencia;

import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.PeriodoAcademico;
import edu.unimag.repositorio.PeriodoAcademicoRepositorio;

public class PeriodoAcademicoRepositorioImpl
        extends RepositorioBaseAbstracto<PeriodoAcademico, Integer>
        implements PeriodoAcademicoRepositorio {

    public PeriodoAcademicoRepositorioImpl(TpaRepository<PeriodoAcademico, Integer> tpaRepository) {
        super(tpaRepository);
    }

    @Override
    public boolean insertarSiNombreNoExiste(String nombre, PeriodoAcademico periodo) {
        if (nombre == null || nombre.isBlank()) {
            throw new IllegalArgumentException("El nombre no puede ser nulo ni vacio");
        }
        if (periodo == null) {
            throw new IllegalArgumentException("El periodo academico no puede ser nulo");
        }

        return tpaRepository.executeAtomic(repo -> {
            for (PeriodoAcademico p : repo.findAll()) {
                if (p.getNombrePeriodoAcademico().equalsIgnoreCase(nombre)) {
                    return false; // Ya existe, no insertar
                }
            }

            repo.save(periodo);
            return true;
        });
    }

    @Override
    public boolean actualizarNombreSiNoExisteEnOtra(Integer idPeriodoAcademico, String nuevoNombre) {
        if (idPeriodoAcademico == null) {
            throw new IllegalArgumentException("El ID del periodo academico no puede ser nulo");
        }
        if (nuevoNombre == null || nuevoNombre.isBlank()) {
            throw new IllegalArgumentException("El nuevo nombre no puede ser nulo ni vacio");
        }

        return tpaRepository.executeAtomic(repo -> {
            PeriodoAcademico periodo = repo.findById(idPeriodoAcademico).orElse(null);
            if (periodo == null) {
                throw new IllegalArgumentException("No existe periodo academico con ID: " + idPeriodoAcademico);
            }

            for (PeriodoAcademico p : repo.findAll()) {
                if (!p.getIdPeriodoAcademico().equals(idPeriodoAcademico)
                        && p.getNombrePeriodoAcademico().equalsIgnoreCase(nuevoNombre)) {
                    return false;
                }
            }

            periodo.corregirNombre(nuevoNombre);
            repo.update(periodo);
            return true;
        });
    }
}
