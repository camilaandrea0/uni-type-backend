package edu.unimag.mapeador;

import edu.unimag.mapeador.api.Mapeador;
import edu.unimag.dto.materia.MateriaDto;
import edu.unimag.modelo.Materia;

import java.util.ArrayList;
import java.util.List;

public class MateriaMapeador implements Mapeador<Materia, MateriaDto> {

    public MateriaMapeador() {
    }

    @Override
    public MateriaDto toDto(Materia entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad Materia no puede ser nula");
        }

        return new MateriaDto(
                entidad.getIdMateria(),
                entidad.getNombreMateria(),
                entidad.getCreditosMateria(),
                entidad.getCupoMaximoMateria(),
                entidad.getEstadoMateria()
        );
    }

    @Override
    public List<MateriaDto> toDtoList(List<Materia> entidades) {
        if (entidades == null || entidades.isEmpty()) {
            return List.of();
        }

        List<MateriaDto> resultado = new ArrayList<>(entidades.size());
        for (Materia entidad : entidades) {
            resultado.add(toDto(entidad));
        }
        return resultado;
    }
}
