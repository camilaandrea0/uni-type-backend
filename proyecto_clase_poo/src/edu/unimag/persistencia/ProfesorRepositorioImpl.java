package edu.unimag.persistencia;

import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.Profesor;
import edu.unimag.repositorio.ProfesorRepositorio;

public class ProfesorRepositorioImpl
        extends RepositorioBaseAbstracto<Profesor, Long>
        implements ProfesorRepositorio {

    public ProfesorRepositorioImpl(TpaRepository<Profesor, Long> tpaRepository) {
        super(tpaRepository);
    }

}
