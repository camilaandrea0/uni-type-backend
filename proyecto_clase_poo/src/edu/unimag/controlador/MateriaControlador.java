package edu.unimag.controlador;

import edu.unimag.dto.materia.MateriaActualizarDto;
import edu.unimag.dto.materia.MateriaCrearDto;
import edu.unimag.dto.materia.MateriaDto;
import edu.unimag.servicio.MateriaServicio;
import java.util.List;

public class MateriaControlador {

    private final MateriaServicio servicio;

    public MateriaControlador(MateriaServicio servicio) {
        if (servicio == null) {
            throw new IllegalArgumentException("El servicio no puede ser nulo");
        }
        this.servicio = servicio;
    }

    public MateriaDto registrar(MateriaCrearDto request) {
        return servicio.registrarMateria(request);
    }

    public List<MateriaDto> listarTodas() {
        return servicio.listarTodas();
    }

    public MateriaDto actualizar(MateriaActualizarDto request) {
        return servicio.actualizar(request);
    }

    public MateriaDto suspender(Integer id) {
        return servicio.suspender(id);
    }

    public MateriaDto reactivar(Integer id) {
        return servicio.reactivar(id);
    }

}
