package edu.unimag.dto.materia;

import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.modelo.constante.PoliticasAcademicas;

public record MateriaActualizarDto(
        Integer id,
        String nuevoNombre,
        Integer nuevosCreditos,
        Integer nuevoCupo
        ) {

    public MateriaActualizarDto {
        if (id == null) {
            throw new IllegalArgumentException("El ID de la materia es obligatorio para actualizar");
        }

        if (nuevoNombre != null) {
            nuevoNombre = ReglasValidacion.limpiarRequerido(nuevoNombre, "El nuevo nombre no puede estar vacio");
        }

        if (nuevosCreditos != null) {
            nuevosCreditos = ReglasValidacion.limpiarEnteroEnRango(
                    nuevosCreditos,
                    PoliticasAcademicas.MIN_CREDITOS_MATERIA,
                    PoliticasAcademicas.MAX_CREDITOS_MATERIA,
                    "Los créditos deben estar entre "
                    + PoliticasAcademicas.MIN_CREDITOS_MATERIA
                    + " y "
                    + PoliticasAcademicas.MAX_CREDITOS_MATERIA
            );
        }

        if (nuevoCupo != null) {
            nuevoCupo = ReglasValidacion.limpiarEnteroEnRango(
                    nuevoCupo,
                    PoliticasAcademicas.MIN_CUPO_MATERIA,
                    PoliticasAcademicas.MAX_CUPO_MATERIA,
                    "El cupo maximo debe estar entre "
                    + PoliticasAcademicas.MIN_CUPO_MATERIA
                    + " y "
                    + PoliticasAcademicas.MAX_CUPO_MATERIA
            );
        }
    }
}
