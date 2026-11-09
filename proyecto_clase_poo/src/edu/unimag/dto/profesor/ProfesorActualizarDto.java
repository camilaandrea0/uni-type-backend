package edu.unimag.dto.profesor;

import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.modelo.enumeracion.EstadoEntidad;

public record ProfesorActualizarDto(
        Long id,
        String nuevoNombre,
        String nuevoCelular,
        Integer nuevoEstado
        ) {

    public ProfesorActualizarDto {
        // ID obligatorio el famoso fail-fast nivel 1
        // Tu haces failfast nivel 1 o 2 o 3 o 4? 
        // Dto, controlador, servicio y modelo 
        // No los usamos todos por que eso es de paranoícos mano
        id = ReglasValidacion.limpiarLongRequerido(id,
                "El ID del profesor es obligatorio");

        // Nombre opcional: null = mantener actual
        if (nuevoNombre != null) {
            nuevoNombre = nuevoNombre.isBlank() ? null : nuevoNombre.trim();
        }

        if (nuevoCelular != null) {
            if (nuevoCelular.isBlank()) {
                nuevoCelular = null;
            } else {
                nuevoCelular = ReglasValidacion.limpiarCelular(nuevoCelular);
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

        if (nuevoNombre == null && nuevoCelular == null && nuevoEstado == null) {
            throw new IllegalArgumentException(
                    "Debe modificar al menos un campo (nombre, celular o estado)"
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
