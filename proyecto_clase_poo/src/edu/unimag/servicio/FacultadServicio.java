package edu.unimag.servicio;

import edu.unimag.dto.facultad.FacultadActualizarDto;
import edu.unimag.dto.facultad.FacultadCrearDto;
import edu.unimag.dto.facultad.FacultadDto;
import edu.unimag.mapeador.FacultadMapeador;
import edu.unimag.modelo.Facultad;
import edu.unimag.repositorio.FacultadRepositorio;

import java.util.List;

public class FacultadServicio {

    private final FacultadRepositorio repositorio;
    private final FacultadMapeador mapper;

    public FacultadServicio(FacultadRepositorio repositorio, FacultadMapeador mapper) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("El mapeador no puede ser nulo");
        }

        this.repositorio = repositorio;
        this.mapper = mapper;
    }

    public FacultadDto registrarFacultad(FacultadCrearDto dto) {
        Facultad nuevaFacultad = new Facultad(dto.nombre());

        boolean insertado = repositorio.insertarSiNombreNoExiste(dto.nombre(), nuevaFacultad);
        if (!insertado) {
            throw new IllegalStateException("Ya existe una facultad con el nombre: " + dto.nombre());
        }
        return mapper.toDto(nuevaFacultad);
    }

    public List<FacultadDto> listarTodas() {
        return mapper.toDtoList(repositorio.listarTodos());
    }

    public FacultadDto actualizar(FacultadActualizarDto dto) {
        if (dto.nuevoNombre() != null) {
            // Verificación de unicidad atómica del nombre
            boolean actualizado = repositorio.actualizarNombreSiNoExisteEnOtra(
                    dto.id(),
                    dto.nuevoNombre()
            );

            if (!actualizado) {
                throw new IllegalStateException(
                        "Ya existe otra facultad con el nombre: " + dto.nuevoNombre()
                );
            }
        }

        Facultad actualizada = repositorio.buscarPorId(dto.id())
                .orElseThrow(() -> new IllegalStateException("Error inesperado: Facultad no encontrada después de actualizar"));
        return mapper.toDto(actualizada);
    }

    public FacultadDto suspender(Long id) {
        Facultad facultad = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe facultad con ID: " + id));

        facultad.suspender();
        Facultad actualizada = repositorio.actualizar(facultad);
        return mapper.toDto(actualizada);
    }

    public FacultadDto reactivar(Long id) {
        Facultad facultad = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe facultad con ID: " + id));

        facultad.reactivar();
        Facultad actualizada = repositorio.actualizar(facultad);
        return mapper.toDto(actualizada);
    }

    // Pilas NO hay método eliminar porque Facultad no permite borrado físico.
}
