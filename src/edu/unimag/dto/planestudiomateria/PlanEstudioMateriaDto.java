package edu.unimag.dto.planestudiomateria;

import edu.unimag.dto.materia.MateriaDto;
import edu.unimag.dto.planestudio.PlanEstudioDto;

public record PlanEstudioMateriaDto(
        PlanEstudioDto planEstudio,
        MateriaDto materia,
        Integer semestre,
        Integer orden
        ) {

}
