package edu.unimag.controlador;

import edu.unimag.dto.estudiante.EstudianteActualizarDto;
import edu.unimag.dto.estudiante.EstudianteCrearDto;
import edu.unimag.dto.estudiante.EstudianteDto;
import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.servicio.EstudianteServicio;

import java.util.List;
import java.util.UUID;

public class EstudianteControlador {

    private final EstudianteServicio servicio;

    public EstudianteControlador(EstudianteServicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio de estudiantes no puede ser nulo");
        }
        this.servicio = servicio;
    }

    // ===== OPERACIONES DE CREACIÓN =====
    public EstudianteDto registrar(EstudianteCrearDto request) {
        return servicio.registrarNuevoEstudiante(request);
    }

    // ===== OPERACIONES DE ACTUALIZACIÓN =====
    public EstudianteDto actualizar(EstudianteActualizarDto request) {
        return servicio.actualizarDatos(request);
    }

    public EstudianteDto activar(UUID idEstudiante) {
        return servicio.activar(idEstudiante);
    }

    public EstudianteDto inactivar(UUID idEstudiante) {
        return servicio.inactivar(idEstudiante);
    }

    // ===== VALIDACIONES BFF (Backend-For-Frontend) =====
    /**
     * Valida que el código no sea null ni vacío.
     *
     * @param codigo el código a validar (puede ser null)
     * @return true si el código es válido (no null y no vacío), false en caso
     * contrario
     */
    public boolean validarCodigo(String codigo) {
        return codigo != null && !codigo.isBlank();
    }

    /**
     * Valida que el nombre no sea null ni vacío.
     *
     * @param nombre el nombre a validar (puede ser null)
     * @return true si el nombre es válido (no null y no vacío), false en caso
     * contrario
     */
    public boolean validarNombre(String nombre) {
        return nombre != null && !nombre.isBlank();
    }

    /**
     * Valida el formato del correo sin crear el DTO.
     *
     * <p>
     * <b>Responsabilidad BFF:</b> La vista usa este método para dar feedback
     * inmediato al usuario mientras digita, sin necesidad de construir un DTO
     * completo.</p>
     *
     * <p>
     * <b>Delega a:</b> {@link ReglasValidacion#limpiarCorreo(String)}</p>
     *
     * @param correo el correo a validar (puede ser null o vacío)
     * @return true si el correo es válido o vacío, false si tiene formato
     * inválido
     */
    public boolean validarCorreo(String correo) {
        if (correo == null || correo.isBlank()) {
            return true; // vacío es válido (campo opcional)
        }
        try {
            ReglasValidacion.limpiarCorreo(correo);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * Valida el formato del celular sin crear el DTO.
     *
     * <p>
     * <b>Responsabilidad BFF:</b> La vista usa este método para dar feedback
     * inmediato al usuario mientras digita, sin necesidad de construir un DTO
     * completo.</p>
     *
     * <p>
     * <b>Delega a:</b> {@link ReglasValidacion#limpiarCelular(String)}</p>
     *
     * @param celular el celular a validar (puede ser null o vacío)
     * @return true si el celular es válido o vacío, false si tiene formato
     * inválido
     */
    public boolean validarCelular(String celular) {
        if (celular == null || celular.isBlank()) {
            return true; // vacío es válido (campo opcional)
        }
        try {
            ReglasValidacion.limpiarCelular(celular);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    // ===== CONSULTAS =====
    public EstudianteDto buscarPorId(UUID id) {
        return servicio.buscarPorId(id);
    }

    public List<EstudianteDto> listarTodos() {
        return servicio.listarTodos();
    }

    public List<EstudianteDto> listarActivos() {
        return servicio.listarActivos();
    }

    public List<EstudianteDto> listarInactivos() {
        return servicio.listarInactivos();
    }

    public int contarEstudiantes() {
        return servicio.contarEstudiantes();
    }
}
