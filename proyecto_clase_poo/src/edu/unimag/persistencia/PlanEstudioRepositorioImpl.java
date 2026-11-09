package edu.unimag.persistencia;

import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.PlanEstudio;
import edu.unimag.repositorio.PlanEstudioRepositorio;

public class PlanEstudioRepositorioImpl
        extends RepositorioBaseAbstracto<PlanEstudio, Long>
        implements PlanEstudioRepositorio {

    public PlanEstudioRepositorioImpl(TpaRepository<PlanEstudio, Long> tpaRepository) {
        super(tpaRepository);
    }

}
