package edu.unimag.mapeador;

import edu.unimag.dto.formacion.FormacionProfesorDto;
import edu.unimag.dto.profesor.ProfesorDto;
import edu.unimag.modelo.FormacionProfesor;
import edu.unimag.modelo.Profesor;
import edu.unimag.repositorio.cargador.ProfesorCargador;

import java.util.ArrayList;
import java.util.List;

public class FormacionProfesorMapeador {

    private final ProfesorMapeador profesorMapeador;

    public FormacionProfesorMapeador(ProfesorMapeador profesorMapeador) {
        if (profesorMapeador == null) {
            throw new IllegalArgumentException("El mapeador de profesores no puede ser nulo");
        }
        this.profesorMapeador = profesorMapeador;
    }

    public FormacionProfesorDto toDto(FormacionProfesor entity, ProfesorCargador profesorCargador) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad FormacionProfesor no puede ser nula");
        }
        if (profesorCargador == null) {
            throw new IllegalArgumentException("El cargador de profesores no puede ser nulo");
        }

        Profesor profesorCompleto = profesorCargador.obtener(entity.getIdProfesor())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Profesor no encontrado en caché para ID: " + entity.getIdProfesor()
        ));

        ProfesorDto profesorDto = profesorMapeador.toDto(profesorCompleto);

        return new FormacionProfesorDto(
                entity.getIdFormacionProfesor(),
                profesorDto,
                entity.getTituloFormacionProfesor(),
                entity.getFechaFormacionProfesor()
        );
    }

    public List<FormacionProfesorDto> toDtoList(List<FormacionProfesor> entidades, ProfesorCargador profesorCargador) {
        if (entidades == null || entidades.isEmpty()) {
            return List.of();
        }

        List<FormacionProfesorDto> resultado = new ArrayList<>(entidades.size());
        for (FormacionProfesor entity : entidades) {
            resultado.add(toDto(entity, profesorCargador));
        }
        return resultado;
    }
}
