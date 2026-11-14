package edu.unimag.repositorio;

import edu.unimag.modelo.Monitoria;
import edu.unimag.modelo.pk.MonitoriaPk;
import edu.unimag.repositorio.api.RepositorioBase;
import java.util.List;
import java.util.UUID;

public interface MonitoriaRepositorio extends RepositorioBase<Monitoria, MonitoriaPk> {

    /**
     * Crea una monitoria solo si la llave compuesta (curso periodo +
     * estudiante + profesor) no existe. La verificacion y la insercion son
     * atomicas.
     */
    boolean insertarSiNoExiste(Monitoria monitoria);

    List<Monitoria> listarPorCursoPeriodo(UUID idCursoPeriodo);

    List<Monitoria> listarPorEstudiante(UUID idEstudiante);

    List<Monitoria> listarPorProfesor(Long idProfesor);
}
