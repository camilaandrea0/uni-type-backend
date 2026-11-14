package edu.unimag.persistencia;

import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.Monitoria;
import edu.unimag.modelo.pk.MonitoriaPk;
import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import edu.unimag.repositorio.MonitoriaRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class MonitoriaRepositorioImpl
        extends RepositorioBaseAbstracto<Monitoria, MonitoriaPk>
        implements MonitoriaRepositorio {

    public MonitoriaRepositorioImpl(TpaRepository<Monitoria, MonitoriaPk> tpaRepository) {
        super(tpaRepository);
    }

    @Override
    protected boolean permiteBorradoFisico() {
        return true;
    }

    @Override
    public boolean insertarSiNoExiste(Monitoria monitoria) {
        if (monitoria == null) {
            throw new IllegalArgumentException("La monitoria no puede ser nula");
        }
        if (monitoria.getIdMonitoria() == null) {
            throw new IllegalArgumentException("La clave de la monitoria no puede ser nula");
        }

        return tpaRepository.executeAtomic(repo -> {
            MonitoriaPk pk = monitoria.getIdMonitoria();
            if (repo.findById(pk).isPresent()) {
                return false;
            }
            repo.save(monitoria);
            return true;
        });
    }

    @Override
    public List<Monitoria> listarPorCursoPeriodo(UUID idCursoPeriodo) {
        if (idCursoPeriodo == null) {
            throw new IllegalArgumentException("El ID del curso periodo no puede ser nulo");
        }
        List<Monitoria> resultado = new ArrayList<>();
        for (Monitoria monitoria : tpaRepository.findAll()) {
            if (monitoria.getIdCursoPeriodo().equals(idCursoPeriodo)) {
                resultado.add(monitoria);
            }
        }
        return resultado;
    }

    @Override
    public List<Monitoria> listarPorEstudiante(UUID idEstudiante) {
        if (idEstudiante == null) {
            throw new IllegalArgumentException("El ID del estudiante no puede ser nulo");
        }
        List<Monitoria> resultado = new ArrayList<>();
        for (Monitoria monitoria : tpaRepository.findAll()) {
            if (monitoria.getIdEstudiante().equals(idEstudiante)) {
                resultado.add(monitoria);
            }
        }
        return resultado;
    }

    @Override
    public List<Monitoria> listarPorProfesor(Long idProfesor) {
        if (idProfesor == null) {
            throw new IllegalArgumentException("El ID del profesor no puede ser nulo");
        }
        List<Monitoria> resultado = new ArrayList<>();
        for (Monitoria monitoria : tpaRepository.findAll()) {
            if (monitoria.getIdProfesor().equals(idProfesor)) {
                resultado.add(monitoria);
            }
        }
        return resultado;
    }
}
