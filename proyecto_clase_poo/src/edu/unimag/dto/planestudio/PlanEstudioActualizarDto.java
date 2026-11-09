package edu.unimag.dto.planestudio;

import edu.unimag.dto.validacion.ReglasValidacion;

public record PlanEstudioActualizarDto(
        Long id,
        String nuevaVersion
        ) {

    public PlanEstudioActualizarDto {
        id = ReglasValidacion.limpiarLongRequerido(
                id,
                "El ID del plan de estudio es obligatorio para actualizar"
        );
        nuevaVersion = ReglasValidacion.limpiarRequerido(
                nuevaVersion,
                "La nueva version es obligatoria"
        );
    }
}
