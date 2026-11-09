package edu.unimag.dto.estudiante;

import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.modelo.enumeracion.EstadoEntidad;

import java.util.UUID;

public record EstudianteActualizarDto(
        UUID id,
        String correo,
        String celular,
        String direccion,
        Integer nuevoEstado
        ) {

    public EstudianteActualizarDto {
        // Si estás en cafeteria esto se llama fail-fast nivel 1
        // Quiere decir que en el DTO hacemos validación
        id = ReglasValidacion.limpiarUuidRequerido(id,
                "El ID del estudiante es obligatorio");

        if (correo != null) {
            correo = correo.isBlank() ? null : ReglasValidacion.limpiarCorreo(correo);
        }

        if (celular != null) {
            celular = celular.isBlank() ? null : ReglasValidacion.limpiarCelular(celular);
        }

        if (direccion != null) {
            if (direccion.isBlank()) {
                direccion = null;
            } else {
                direccion = ReglasValidacion.limpiarRequerido(direccion,
                        "La dirección no puede estar vacía");
            }
        }

        if (nuevoEstado != null) {
            if (nuevoEstado != EstadoEntidad.ACTIVO.getCode()
                    && nuevoEstado != EstadoEntidad.INACTIVO.getCode()) {
                throw new IllegalArgumentException(
                        "El estado debe ser 1 (Activo) o 2 (Inactivo)"
                );
            }
        }

        if (correo == null && celular == null && direccion == null && nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "Debe modificar al menos un campo (correo, celular, dirección o estado)"
            );
        }
    }

    public EstadoEntidad obtenerEstadoComoEnum() {
        if (nuevoEstado == null) {
            return null;
        }
        if (nuevoEstado == EstadoEntidad.ACTIVO.getCode()) {
            return EstadoEntidad.ACTIVO;
        }
        return EstadoEntidad.INACTIVO;
    }
}
