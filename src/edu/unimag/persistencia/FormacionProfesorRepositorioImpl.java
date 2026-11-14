package edu.unimag.persistencia;

import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.FormacionProfesor;
import edu.unimag.repositorio.FormacionProfesorRepositorio;

public class FormacionProfesorRepositorioImpl
        extends RepositorioBaseAbstracto<FormacionProfesor, Long>
        implements FormacionProfesorRepositorio {

    public FormacionProfesorRepositorioImpl(TpaRepository<FormacionProfesor, Long> tpaRepository) {
        super(tpaRepository);
    }

    @Override
    protected boolean permiteBorradoFisico() {
        return true;
    }

}
