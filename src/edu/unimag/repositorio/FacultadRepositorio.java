package edu.unimag.repositorio;

import edu.unimag.repositorio.api.RepositorioBase;
import edu.unimag.modelo.Facultad;

public interface FacultadRepositorio extends RepositorioBase<Facultad, Long> {

    /**
     * Inserta una facultad solo si el nombre no existe previamente
     *
     * Operación ATÓMICA: la verificación de unicidad y la inserción ocurren
     * bajo el mismo WriteLock
     *
     * @param nombre el nombre a verificar
     * @param facultad la facultad a insertar
     * @return true si se insertó correctamente, false si el nombre ya existe
     * @throws IllegalArgumentException si nombre o facultad son nulos/vacios
     */
    boolean insertarSiNombreNoExiste(String nombre, Facultad facultad);

    /**
     * Actualiza el nombre de una facultad solo si no existe en otra facultad
     *
     * Operación ATÓMICA: la verificación de unicidad y la actualización ocurren
     * bajo el mismo WriteLock
     *
     * @param idFacultad el ID de la facultad a actualizar
     * @param nuevoNombre el nuevo nombre
     * @return true si se actualizó correctamente, false si el nombre ya existe
     * en otra facultad
     * @throws IllegalArgumentException si idFacultad es nulo o nuevoNombre es
     * nulo/vacío
     */
    boolean actualizarNombreSiNoExisteEnOtra(Long idFacultad, String nuevoNombre);
}
