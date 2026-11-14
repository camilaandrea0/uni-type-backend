package edu.unimag.controlador;

import edu.unimag.dto.programa.ProgramaActualizarDto;
import edu.unimag.dto.programa.ProgramaCrearDto;
import edu.unimag.dto.programa.ProgramaDto;
import edu.unimag.servicio.ProgramaServicio;

import java.util.List;
import java.util.UUID;

public class ProgramaControlador {

    private final ProgramaServicio servicio;

    public ProgramaControlador(ProgramaServicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio de programas no puede ser nulo");
        }
        this.servicio = servicio;
    }

    public ProgramaDto registrar(ProgramaCrearDto request) {
        return servicio.registrar(request);
    }

    public ProgramaDto actualizar(ProgramaActualizarDto request) {
        return servicio.actualizar(request);
    }

    public ProgramaDto suspender(UUID idPrograma) {
        return servicio.suspender(idPrograma);
    }

    public ProgramaDto reactivar(UUID idPrograma) {
        return servicio.reactivar(idPrograma);
    }

    public List<ProgramaDto> listarTodos() {
        return servicio.listarTodos();
    }

    public ProgramaDto buscarPorId(UUID idPrograma) {
        return servicio.buscarPorId(idPrograma);
    }

    public List<ProgramaDto> listarPorFacultad(Long idFacultad) {
        return servicio.listarPorFacultad(idFacultad);
    }

}
