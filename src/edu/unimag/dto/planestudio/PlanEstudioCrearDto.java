package edu.unimag.dto.planestudio;

import edu.unimag.dto.validacion.ReglasValidacion;
import java.util.UUID;

public record PlanEstudioCrearDto(
        String version,
        UUID idPrograma
        ) {

    public PlanEstudioCrearDto {
        version = ReglasValidacion.limpiarRequerido(version, "La version es obligatoria");

        if (idPrograma == null) {
            throw new IllegalArgumentException("El ID del programa es obligatorio");
        }
    }
}
