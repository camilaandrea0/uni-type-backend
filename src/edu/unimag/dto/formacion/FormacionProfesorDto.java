package edu.unimag.dto.formacion;

import edu.unimag.dto.profesor.ProfesorDto;
import java.time.OffsetDateTime;

public record FormacionProfesorDto(
        Long id,
        ProfesorDto profesor,
        String titulo,
        OffsetDateTime fecha
        ) {

}
