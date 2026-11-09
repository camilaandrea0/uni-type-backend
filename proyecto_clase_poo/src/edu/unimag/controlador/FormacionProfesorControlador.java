package edu.unimag.controlador;

import edu.unimag.dto.formacion.FormacionProfesorActualizarDto;
import edu.unimag.dto.formacion.FormacionProfesorCrearDto;
import edu.unimag.dto.formacion.FormacionProfesorDto;
import edu.unimag.servicio.FormacionProfesorServicio;

import java.util.List;

public class FormacionProfesorControlador {

    private final FormacionProfesorServicio servicio;

    public FormacionProfesorControlador(FormacionProfesorServicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio de formaciones no puede ser nulo");
        }
        this.servicio = servicio;
    }

    public FormacionProfesorDto registrar(FormacionProfesorCrearDto request) {
        return servicio.registrarFormacion(request);
    }

    public List<FormacionProfesorDto> listarTodos() {
        return servicio.listarFormaciones();
    }

    public FormacionProfesorDto actualizar(FormacionProfesorActualizarDto request) {
        return servicio.actualizarFormacion(request);
    }

    public boolean eliminar(Long id) {
        return servicio.eliminarFormacion(id);
    }

    public List<FormacionProfesorDto> listarPorProfesor(Long idProfesor) {
        return servicio.listarFormacionesPorProfesor(idProfesor);
    }
}
