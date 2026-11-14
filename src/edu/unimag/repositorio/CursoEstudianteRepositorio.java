package edu.unimag.repositorio;

import edu.unimag.modelo.CursoEstudiante;
import edu.unimag.modelo.pk.CursoEstudiantePk;
import edu.unimag.repositorio.api.RepositorioBase;
import java.util.List;
import java.util.UUID;

public interface CursoEstudianteRepositorio extends RepositorioBase<CursoEstudiante, CursoEstudiantePk> {

    /**
     * Inserta una inscripcion solo si la llave compuesta (curso periodo +
     * estudiante) no existe. La verificacion y la insercion son atomicas.
     */
    boolean insertarSiNoExiste(CursoEstudiante cursoEstudiante);

    List<CursoEstudiante> listarPorEstudiante(UUID idEstudiante);

    List<CursoEstudiante> listarPorCursoPeriodo(UUID idCursoPeriodo);

    int contarOcupantesPorCursoPeriodo(UUID idCursoPeriodo);
}
