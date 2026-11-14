package edu.unimag.persistencia;

import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import com.cleandev.tpa.api.TpaRepository;

import edu.unimag.modelo.Materia;
import edu.unimag.repositorio.MateriaRepositorio;

public class MateriaRepositorioImpl
        extends RepositorioBaseAbstracto<Materia, Integer>
        implements MateriaRepositorio {

    public MateriaRepositorioImpl(TpaRepository<Materia, Integer> tpaRepository) {
        super(tpaRepository);
    }

}
