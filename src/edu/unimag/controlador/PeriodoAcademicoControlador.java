package edu.unimag.controlador;

import edu.unimag.dto.periodoacademico.PeriodoAcademicoActualizarDto;
import edu.unimag.dto.periodoacademico.PeriodoAcademicoCrearDto;
import edu.unimag.dto.periodoacademico.PeriodoAcademicoDto;
import edu.unimag.servicio.PeriodoAcademicoServicio;

import java.util.List;

public class PeriodoAcademicoControlador {

    private final PeriodoAcademicoServicio servicio;

    public PeriodoAcademicoControlador(PeriodoAcademicoServicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio de periodos academicos no puede ser nulo");
        }
        this.servicio = servicio;
    }

    public PeriodoAcademicoDto registrar(PeriodoAcademicoCrearDto request) {
        return servicio.registrar(request);
    }

    public List<PeriodoAcademicoDto> listarTodos() {
        return servicio.listarTodos();
    }

    public PeriodoAcademicoDto actualizar(PeriodoAcademicoActualizarDto request) {
        return servicio.actualizar(request);
    }

    public PeriodoAcademicoDto suspender(Integer id) {
        return servicio.suspender(id);
    }

    public PeriodoAcademicoDto reactivar(Integer id) {
        return servicio.reactivar(id);
    }
}
