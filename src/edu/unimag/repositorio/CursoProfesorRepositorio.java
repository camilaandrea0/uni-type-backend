package edu.unimag.repositorio;

import edu.unimag.modelo.CursoProfesor;
import edu.unimag.modelo.pk.CursoProfesorPk;
import edu.unimag.repositorio.api.RepositorioBase;
import java.util.List;
import java.util.UUID;

public interface CursoProfesorRepositorio extends RepositorioBase<CursoProfesor, CursoProfesorPk>{
    
    boolean insertarSinombrenoExiste(CursoProfesor cursoProfesor);
    
    List<CursoProfesor> listPorCursoPeriodo(UUID idcursoPerido);
    
    
}
