package edu.unimag.servicio;

import edu.unimag.dto.periodoacademico.PeriodoAcademicoActualizarDto;
import edu.unimag.dto.periodoacademico.PeriodoAcademicoCrearDto;
import edu.unimag.dto.periodoacademico.PeriodoAcademicoDto;
import edu.unimag.mapeador.PeriodoAcademicoMapeador;
import edu.unimag.modelo.PeriodoAcademico;
import edu.unimag.repositorio.PeriodoAcademicoRepositorio;

import java.util.List;

public class PeriodoAcademicoServicio {

    private final PeriodoAcademicoRepositorio repositorio;
    private final PeriodoAcademicoMapeador mapper;

    public PeriodoAcademicoServicio(PeriodoAcademicoRepositorio repositorio, PeriodoAcademicoMapeador mapper) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de periodos academicos no puede ser nulo");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("El mapeador de periodos academicos no puede ser nulo");
        }

        this.repositorio = repositorio;
        this.mapper = mapper;
    }

    public PeriodoAcademicoDto registrar(PeriodoAcademicoCrearDto dto) {
        PeriodoAcademico nuevoPeriodo = new PeriodoAcademico(dto.nombre());

        boolean insertado = repositorio.insertarSiNombreNoExiste(dto.nombre(), nuevoPeriodo);

        if (!insertado) {
            throw new IllegalStateException("Ya existe un periodo academico con el nombre: " + dto.nombre());
        }

        return mapper.toDto(nuevoPeriodo);
    }

    public List<PeriodoAcademicoDto> listarTodos() {
        return mapper.toDtoList(repositorio.listarTodos());
    }

    public PeriodoAcademicoDto actualizar(PeriodoAcademicoActualizarDto dto) {
        PeriodoAcademico periodo = repositorio.buscarPorId(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("No existe periodo academico con ID: " + dto.id()));

        if (dto.nuevoNombre() != null) {
            boolean actualizado = repositorio.actualizarNombreSiNoExisteEnOtra(
                    periodo.getIdPeriodoAcademico(),
                    dto.nuevoNombre()
            );

            if (!actualizado) {
                throw new IllegalStateException("Ya existe otro periodo academico con el nombre: " + dto.nuevoNombre());
            }
        }

        return mapper.toDto(periodo);
    }

    public PeriodoAcademicoDto suspender(Integer id) {
        PeriodoAcademico periodo = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe periodo academico con ID: " + id));

        periodo.suspender();
        PeriodoAcademico actualizado = repositorio.actualizar(periodo);
        return mapper.toDto(actualizado);
    }

    public PeriodoAcademicoDto reactivar(Integer id) {
        PeriodoAcademico periodo = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe periodo academico con ID: " + id));

        periodo.reactivar();
        PeriodoAcademico actualizado = repositorio.actualizar(periodo);
        return mapper.toDto(actualizado);
    }

}
