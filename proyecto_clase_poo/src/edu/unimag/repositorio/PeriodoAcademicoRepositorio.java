package edu.unimag.repositorio;

import edu.unimag.repositorio.api.RepositorioBase;
import edu.unimag.modelo.PeriodoAcademico;

public interface PeriodoAcademicoRepositorio extends RepositorioBase<PeriodoAcademico, Integer> {

    /**
     * Inserta un periodo académico solo si el nombre no existe previamente
     *
     * Esta operación es ATÓMICA: la verificación de unicidad y la inserción
     * ocurren bajo el mismo WriteLock
     *
     * @param nombre el nombre a verificar (no puede ser nulo ni vacío)
     * @param periodo el periodo a insertar (no puede ser nulo)
     * @return true si se insertó correctamente, false si el nombre ya existía
     * @throws IllegalArgumentException si nombre o periodo son nulos/vacíos
     */
    boolean insertarSiNombreNoExiste(String nombre, PeriodoAcademico periodo);

    /**
     * Actualiza el nombre de un periodo académico solo si no existe en otro
     * periodo
     *
     * Esta operación es ATÓMICA: la verificación de unicidad y la actualización
     * ocurren bajo el mismo WriteLock
     *
     * @param idPeriodoAcademico el ID del periodo a actualizar
     * @param nuevoNombre el nuevo nombre (no puede ser nulo ni vacío)
     * @return true si se actualizó correctamente, false si el nombre ya existía
     * en otro periodo
     * @throws IllegalArgumentException si idPeriodoAcademico es nulo o
     * nuevoNombre es nulo/vacío
     */
    boolean actualizarNombreSiNoExisteEnOtra(Integer idPeriodoAcademico, String nuevoNombre);
}
