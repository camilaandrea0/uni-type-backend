package edu.unimag.controlador;

import edu.unimag.dto.facultad.FacultadActualizarDto;
import edu.unimag.dto.facultad.FacultadCrearDto;
import edu.unimag.dto.facultad.FacultadDto;
import edu.unimag.servicio.FacultadServicio;

import java.util.List;

public class FacultadControlador {

    private final FacultadServicio servicio;

    public FacultadControlador(FacultadServicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio de facultades no puede ser nulo");
        }
        this.servicio = servicio;
    }

    public FacultadDto registrar(FacultadCrearDto request) {
        return servicio.registrarFacultad(request);
    }

    public List<FacultadDto> listarTodas() {
        return servicio.listarTodas();
    }

    public FacultadDto actualizar(FacultadActualizarDto request) {
        return servicio.actualizar(request);
    }

    public FacultadDto suspender(Long id) {
        return servicio.suspender(id);
    }

    public FacultadDto reactivar(Long id) {
        return servicio.reactivar(id);
    }

}
