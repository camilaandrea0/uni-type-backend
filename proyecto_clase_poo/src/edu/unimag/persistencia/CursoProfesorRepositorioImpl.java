package edu.unimag.persistencia;

import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.CursoProfesor;
import edu.unimag.modelo.pk.CursoProfesorPk;
import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import edu.unimag.repositorio.CursoProfesorRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CursoProfesorRepositorioImpl extends RepositorioBaseAbstracto<CursoProfesor, CursoProfesorPk> implements CursoProfesorRepositorio {

    public CursoProfesorRepositorioImpl(TpaRepository<CursoProfesor, CursoProfesorPk> tpaRepository) {
        super(tpaRepository);
    }

    @Override
    public boolean insertarSinombrenoExiste(CursoProfesor cursoProfesor) {

        if (cursoProfesor == null) {
            throw new IllegalArgumentException("El curso profesor no puesde ser nulo");
        }
        if (cursoProfesor.getIdCursoProfesorPeriodo() == null) {
            throw new IllegalArgumentException("La id del cursno no puede ser nula");
        }

        return tpaRepository.executeAtomic(repo -> {

            CursoProfesorPk pk = cursoProfesor.getIdCursoProfesorPeriodo();
            if (repo.findById(pk).isPresent()) {
                return false;
            }
            repo.save(cursoProfesor);
            return true;
        });

    }

    @Override
    public List<CursoProfesor> listPorCursoPeriodo(UUID idcursoPerido) {
        if (idcursoPerido == null) {
            throw new IllegalArgumentException("El id del curso periodo es nulo");
        }
        List<CursoProfesor> resultado = new ArrayList<>();
        for (CursoProfesor asignacion : tpaRepository.findAll()) {
            if (asignacion.getIdCurso().equals(idcursoPerido)) {
                resultado.add(asignacion);
            }
        }
        return resultado;
    }


}
