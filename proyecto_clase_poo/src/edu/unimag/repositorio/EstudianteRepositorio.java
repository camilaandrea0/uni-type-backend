package edu.unimag.repositorio;

import edu.unimag.repositorio.api.RepositorioBase;
import edu.unimag.modelo.Estudiante;
import java.util.UUID;

public interface EstudianteRepositorio extends RepositorioBase<Estudiante, UUID> {

    /**
     * Inserta un estudiante solo si el código no existe previamente
     *
     * Truco con operación ATÓMICA: la verificación de unicidad y la inserción
     * ocurren bajo el mismo WriteLock, eliminando condiciones de carrera
     *
     * CASO DE USO: Cuando un desarrollador necesita garantizar que no existan
     * dos estudiantes con el mismo código, pero el código NO es la llave
     * primaria (la PK es el UUID).
     *
     * @param codigo el código a verificar
     * @param estudiante el estudiante a insertar
     * @return true si se insertó correctamente, false si el código ya existía
     * @throws IllegalArgumentException si codigo o estudiante son nulos/vacíos
     */
    boolean insertarSiCodigoNoExiste(String codigo, Estudiante estudiante);
}
