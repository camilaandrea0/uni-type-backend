package edu.unimag.mapeador;

import edu.unimag.mapeador.api.Mapeador;
import edu.unimag.dto.profesor.ProfesorDto;
import edu.unimag.modelo.Profesor;

import java.util.ArrayList;
import java.util.List;

public class ProfesorMapeador implements Mapeador<Profesor, ProfesorDto> {

    public ProfesorMapeador() {
    }

    @Override
    public ProfesorDto toDto(Profesor entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("La entidad Profesor no puede ser nula");
        }

        return new ProfesorDto(
                entidad.getIdProfesor(),
                entidad.getNombreProfesor(),
                entidad.getCelularProfesor(),
                entidad.getEstadoProfesor(),
                entidad.estaActivo()
        );
    }

    @Override
    public List<ProfesorDto> toDtoList(List<Profesor> entidades) {
        if (entidades == null || entidades.isEmpty()) {
            return List.of();
        }

        List<ProfesorDto> resultado = new ArrayList<>(entidades.size());
        for (Profesor entidad : entidades) {
            resultado.add(toDto(entidad));
        }
        return resultado;
    }
}
