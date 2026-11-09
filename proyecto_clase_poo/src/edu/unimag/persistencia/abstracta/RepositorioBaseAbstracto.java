package edu.unimag.persistencia.abstracta;

import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.repositorio.api.RepositorioBase;
import java.util.List;
import java.util.Optional;

public abstract class RepositorioBaseAbstracto<T, ID> implements RepositorioBase<T, ID> {

    protected final TpaRepository<T, ID> tpaRepository;

    public RepositorioBaseAbstracto(TpaRepository<T, ID> tpaRepository) {
        this.tpaRepository = tpaRepository;
    }

    @Override
    public T guardar(T entidad) {
        return tpaRepository.save(entidad);
    }

    @Override
    public T actualizar(T entidad) {
        return tpaRepository.update(entidad);
    }

    @Override
    public Optional<T> buscarPorId(ID id) {
        return tpaRepository.findById(id);
    }

    @Override
    public List<T> listarTodos() {
        return tpaRepository.findAll();
    }

    @Override
    public long contar() {
        return tpaRepository.count();
    }

    @Override
    public final boolean eliminar(ID id) {
        if (!permiteBorradoFisico()) {
            throw new UnsupportedOperationException("No se permite borrado fisico");
        }
        return tpaRepository.deleteById(id);
    }

    @Override
    public void cerrar() {
        tpaRepository.close();
    }

    protected boolean permiteBorradoFisico() {
        return false;
    }

}
