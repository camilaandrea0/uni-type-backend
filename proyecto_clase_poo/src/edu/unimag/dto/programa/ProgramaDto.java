package edu.unimag.dto.programa;

import edu.unimag.dto.facultad.FacultadDto;
import edu.unimag.modelo.enumeracion.EstadoEntidad;
import edu.unimag.modelo.enumeracion.NivelPrograma;

import java.util.UUID;

public record ProgramaDto(
        UUID id,
        String nombre,
        NivelPrograma nivelPrograma,
        FacultadDto facultad,
        EstadoEntidad estado
        ) {

}
