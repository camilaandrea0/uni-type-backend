package edu.unimag.mapeador;

import edu.unimag.mapeador.api.Mapeador;
import edu.unimag.dto.estudiante.EstudianteDto;
import edu.unimag.modelo.Estudiante;

import java.util.ArrayList;
import java.util.List;

public class EstudianteMapeador implements Mapeador<Estudiante, EstudianteDto> {

    @Override
    public EstudianteDto toDto(Estudiante entidad) {
        if (entidad == null) {
            throw new IllegalArgumentException("Entidad nula");
        }
        return new EstudianteDto(
                entidad.getIdEstudiante(),
                entidad.getCodigoEstudiante(),
                entidad.getNombreEstudiante(),
                entidad.getCorreoEstudiante(),
                entidad.getCelularEstudiante(),
                entidad.getDireccionEstudiante(),
                entidad.getEstadoEstudiante(),
                entidad.estaActivo()
        );
    }

    @Override
    public List<EstudianteDto> toDtoList(List<Estudiante> entidades) {
        if (entidades == null || entidades.isEmpty()) {
            return List.of();
        }

        List<EstudianteDto> resultado = new ArrayList<>(entidades.size());
        for (Estudiante entidad : entidades) {
            resultado.add(toDto(entidad));
        }
        return resultado;
    }
}
