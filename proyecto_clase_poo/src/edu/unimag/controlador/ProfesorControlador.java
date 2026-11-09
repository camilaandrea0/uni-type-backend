package edu.unimag.controlador;

import edu.unimag.dto.profesor.ProfesorActualizarDto;
import edu.unimag.dto.profesor.ProfesorCrearDto;
import edu.unimag.dto.profesor.ProfesorDto;
import edu.unimag.dto.validacion.ReglasValidacion;
import edu.unimag.servicio.ProfesorServicio;

import java.util.List;

public class ProfesorControlador {

    private final ProfesorServicio servicio;

    public ProfesorControlador(ProfesorServicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio de profesores no puede ser nulo");
        }
        this.servicio = servicio;
    }

    // Crear
    public ProfesorDto registrar(ProfesorCrearDto request) {
        return servicio.registrarProfesor(request);
    }

    // Actualizar
    public ProfesorDto actualizar(ProfesorActualizarDto request) {
        return servicio.actualizar(request);
    }

    // El Backend-For-Frontend
    public boolean validarCelular(String celular) {
        try {
            ReglasValidacion.limpiarCelular(celular);
            return true;
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    public boolean validarNombre(String nombre) {
        return nombre != null && !nombre.isBlank();
    }

    // Consultas
    public ProfesorDto buscarPorId(Long id) {
        return servicio.buscarPorId(id);
    }

    public List<ProfesorDto> listarTodos() {
        return servicio.listarTodos();
    }

    public List<ProfesorDto> listarActivos() {
        return servicio.listarActivos();
    }

    public List<ProfesorDto> listarInactivos() {
        return servicio.listarInactivos();
    }

    public int contarProfesores() {
        return servicio.contarProfesores();
    }
}
