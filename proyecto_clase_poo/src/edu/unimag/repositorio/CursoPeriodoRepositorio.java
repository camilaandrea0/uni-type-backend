package edu.unimag.repositorio;

import edu.unimag.repositorio.api.RepositorioBase;
import edu.unimag.modelo.CursoPeriodo;
import java.util.UUID;

public interface CursoPeriodoRepositorio extends RepositorioBase<CursoPeriodo, UUID> {

    /**
     * Inserta un curso periodo solo si la combinación (Materia +
     * PeriodoAcademico + Nombre) no existe previamente.
     *
     * Esta operación es ATÓMICA: la verificación de unicidad y la inserción
     * ocurren bajo el mismo WriteLock, eliminando condiciones de carrera
     * (TOCTOU).
     *
     * CASO DE USO: Cuando se necesita garantizar que no existan dos grupos con
     * el mismo nombre para la misma materia en el mismo periodo académico.
     * Ejemplo: No puede haber dos "Grupo 01" de "Matemáticas I" en "2024-1".
     *
     * NOTA: La PK es el UUID (generado por TPA Blaster), pero esta combinación
     * de tres campos actúa como business key única.
     *
     * @param idMateria el ID de la materia (no puede ser nulo)
     * @param idPeriodoAcademico el ID del periodo académico (no puede ser nulo)
     * @param nombre el nombre del grupo a verificar (no puede ser nulo ni
     * vacío)
     * @param cursoPeriodo el curso periodo a insertar (no puede ser nulo)
     * @return true si se insertó correctamente, false si la combinación ya
     * existía
     * @throws IllegalArgumentException si alguno de los parámetros es
     * nulo/vacío
     */
    boolean insertarSiCombinacionNoExiste(
            Integer idMateria,
            Integer idPeriodoAcademico,
            String nombre,
            CursoPeriodo cursoPeriodo
    );

    /**
     * Actualiza un curso periodo verificando que el nombre no exista en otro
     * curso con la misma combinación (Materia + PeriodoAcademico + Nombre)
     *
     * Operación ATÓMICA: la verificación de unicidad y la actualización ocurren
     * bajo el mismo WriteLock
     *
     * @param cursoPeriodoModificado el curso periodo con los cambios ya
     * aplicados en memoria
     * @param verificarNombre si es true, verifica la unicidad del nombre
     * @return true si se actualizó correctamente, false si el nombre ya existía
     * en otro curso
     * @throws IllegalArgumentException si cursoPeriodoModificado es nulo
     */
    boolean actualizarSiNoExisteEnOtra(CursoPeriodo cursoPeriodoModificado, boolean verificarNombre);
}
