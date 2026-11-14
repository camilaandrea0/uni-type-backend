package edu.unimag.dto.planestudio;

import edu.unimag.dto.programa.ProgramaDto;
import edu.unimag.modelo.enumeracion.EstadoEntidad;
import java.time.OffsetDateTime;

public record PlanEstudioDto(
        Long id,
        String version,
        EstadoEntidad estado,
        OffsetDateTime fechaCreacion,
        ProgramaDto programa
        ) {

}
