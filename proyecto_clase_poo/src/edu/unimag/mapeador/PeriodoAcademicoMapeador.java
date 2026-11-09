package edu.unimag.mapeador;

import edu.unimag.mapeador.api.Mapeador;
import edu.unimag.dto.periodoacademico.PeriodoAcademicoDto;
import edu.unimag.modelo.PeriodoAcademico;

import java.util.ArrayList;
import java.util.List;

public class PeriodoAcademicoMapeador implements Mapeador<PeriodoAcademico, PeriodoAcademicoDto> {

    public PeriodoAcademicoMapeador() {
    }

    @Override
    public PeriodoAcademicoDto toDto(PeriodoAcademico entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad PeriodoAcademico no puede ser nula");
        }

        return new PeriodoAcademicoDto(
                entidad.getIdPeriodoAcademico(),
                entidad.getNombrePeriodoAcademico(),
                entidad.getEstadoPeriodoAcademico()
        );
    }

    @Override
    public List<PeriodoAcademicoDto> toDtoList(List<PeriodoAcademico> entidades) {
        if (entidades == null || entidades.isEmpty()) {
            return List.of();
        }

        List<PeriodoAcademicoDto> resultado = new ArrayList<>(entidades.size());
        for (PeriodoAcademico entidad : entidades) {
            resultado.add(toDto(entidad));
        }
        return resultado;
    }
}
