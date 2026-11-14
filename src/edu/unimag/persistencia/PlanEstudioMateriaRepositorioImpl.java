package edu.unimag.persistencia;

import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.PlanEstudioMateria;
import edu.unimag.modelo.pk.PlanEstudioMateriaPk;
import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import edu.unimag.repositorio.PlanEstudioMateriaRepositorio;
import java.util.ArrayList;
import java.util.List;

public class PlanEstudioMateriaRepositorioImpl
        extends RepositorioBaseAbstracto<PlanEstudioMateria, PlanEstudioMateriaPk>
        implements PlanEstudioMateriaRepositorio {

    public PlanEstudioMateriaRepositorioImpl(TpaRepository<PlanEstudioMateria, PlanEstudioMateriaPk> tpaRepository) {
        super(tpaRepository);
    }

    @Override
    protected boolean permiteBorradoFisico() {
        return true;
    }

    @Override
    public boolean insertarSiNoExiste(PlanEstudioMateria planEstudioMateria) {
        if (planEstudioMateria == null) {
            throw new IllegalArgumentException("La asignacion plan-materia no puede ser nula");
        }
        if (planEstudioMateria.getIdPlanEstudioMateria() == null) {
            throw new IllegalArgumentException("La clave plan-materia no puede ser nula");
        }

        return tpaRepository.executeAtomic(repo -> {
            PlanEstudioMateriaPk pk = planEstudioMateria.getIdPlanEstudioMateria();
            if (repo.findById(pk).isPresent()) {
                return false;
            }
            repo.save(planEstudioMateria);
            return true;
        });
    }

    @Override
    public List<PlanEstudioMateria> listarPorPlanEstudio(Long idPlanEstudio) {
        if (idPlanEstudio == null) {
            throw new IllegalArgumentException("El ID del plan de estudio no puede ser nulo");
        }
        List<PlanEstudioMateria> resultado = new ArrayList<>();
        for (PlanEstudioMateria asignacion : tpaRepository.findAll()) {
            if (asignacion.getIdPlanEstudio().equals(idPlanEstudio)) {
                resultado.add(asignacion);
            }
        }
        return resultado;
    }

    @Override
    public List<PlanEstudioMateria> listarPorMateria(Integer idMateria) {
        if (idMateria == null) {
            throw new IllegalArgumentException("El ID de la materia no puede ser nulo");
        }
        List<PlanEstudioMateria> resultado = new ArrayList<>();
        for (PlanEstudioMateria asignacion : tpaRepository.findAll()) {
            if (asignacion.getIdMateria().equals(idMateria)) {
                resultado.add(asignacion);
            }
        }
        return resultado;
    }
}
