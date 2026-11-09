package edu.unimag.repositorio;

import edu.unimag.repositorio.api.RepositorioBase;
import edu.unimag.modelo.Programa;
import java.util.UUID;

public interface ProgramaRepositorio extends RepositorioBase<Programa, UUID> {

    boolean insertarSiNombreNoExiste(String nombre, Programa programa);

    boolean actualizarSiNoExisteEnOtra(Programa programaModificado, boolean verificarNombre);
}
