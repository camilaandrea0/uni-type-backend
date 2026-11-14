package edu.unimag.persistencia;

import com.cleandev.tpa.api.TpaRepository;
import edu.unimag.modelo.CursoEstudiante;
import edu.unimag.modelo.pk.CursoEstudiantePk;
import edu.unimag.persistencia.abstracta.RepositorioBaseAbstracto;
import edu.unimag.repositorio.CursoEstudianteRepositorio;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class CursoEstudianteRepositorioImpl
        extends RepositorioBaseAbstracto<CursoEstudiante, CursoEstudiantePk>
        implements CursoEstudianteRepositorio {

    public CursoEstudianteRepositorioImpl(TpaRepository<CursoEstudiante, CursoEstudiantePk> tpaRepository) {
        super(tpaRepository);
    }

    @Override
    public boolean insertarSiNoExiste(CursoEstudiante cursoEstudiante) {
        if (cursoEstudiante == null) {
            throw new IllegalArgumentException("La inscripcion no puede ser nula");
        }
        if (cursoEstudiante.getIdCursoEstudiante() == null) {
            throw new IllegalArgumentException("La clave de la inscripcion no puede ser nula");
        }

        return tpaRepository.executeAtomic(repo -> {
            CursoEstudiantePk pk = cursoEstudiante.getIdCursoEstudiante();
            if (repo.findById(pk).isPresent()) {
                return false;
            }
            repo.save(cursoEstudiante);
            return true;
        });
    }

    @Override
    public List<CursoEstudiante> listarPorEstudiante(UUID idEstudiante) {
        if (idEstudiante == null) {
            throw new IllegalArgumentException("El ID del estudiante no puede ser nulo");
        }
        List<CursoEstudiante> resultado = new ArrayList<>();
        for (CursoEstudiante inscripcion : tpaRepository.findAll()) {
            if (inscripcion.getIdEstudiante().equals(idEstudiante)) {
                resultado.add(inscripcion);
            }
        }
        return resultado;
    }

    @Override
    public List<CursoEstudiante> listarPorCursoPeriodo(UUID idCursoPeriodo) {
        if (idCursoPeriodo == null) {
            throw new IllegalArgumentException("El ID del curso periodo no puede ser nulo");
        }
        List<CursoEstudiante> resultado = new ArrayList<>();
        for (CursoEstudiante inscripcion : tpaRepository.findAll()) {
            if (inscripcion.getIdCursoPeriodo().equals(idCursoPeriodo)) {
                resultado.add(inscripcion);
            }
        }
        return resultado;
    }

    @Override
    public int contarOcupantesPorCursoPeriodo(UUID idCursoPeriodo) {
        int ocupantes = 0;
        for (CursoEstudiante inscripcion : listarPorCursoPeriodo(idCursoPeriodo)) {
            if (!inscripcion.estaRetirada() && !inscripcion.estaRechazada()) {
                ocupantes++;
            }
        }
        return ocupantes;
    }
}
