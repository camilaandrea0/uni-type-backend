package edu.unimag.repositorio;

import edu.unimag.modelo.PlanEstudioMateria;
import edu.unimag.modelo.pk.PlanEstudioMateriaPk;
import edu.unimag.repositorio.api.RepositorioBase;
import java.util.List;

public interface PlanEstudioMateriaRepositorio extends RepositorioBase<PlanEstudioMateria, PlanEstudioMateriaPk> {

    /**
     * Asigna una materia a un plan solo si la llave compuesta (plan + materia)
     * no existe. La verificacion y la insercion son atomicas.
     */
    boolean insertarSiNoExiste(PlanEstudioMateria planEstudioMateria);

    List<PlanEstudioMateria> listarPorPlanEstudio(Long idPlanEstudio);

    List<PlanEstudioMateria> listarPorMateria(Integer idMateria);
}
