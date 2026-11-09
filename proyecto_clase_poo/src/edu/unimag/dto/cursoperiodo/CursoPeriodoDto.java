package edu.unimag.dto.cursoperiodo;

import edu.unimag.dto.materia.MateriaDto;
import edu.unimag.dto.periodoacademico.PeriodoAcademicoDto;

import java.time.OffsetDateTime;
import java.util.UUID;

public record CursoPeriodoDto(
        UUID id,
        String nombre,
        MateriaDto materia,
        PeriodoAcademicoDto periodo,
        Integer cupo,
        OffsetDateTime fechaCreacion
        ) {

}
