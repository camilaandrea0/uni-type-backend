package edu.unimag.servicio;

import edu.unimag.dto.materia.MateriaActualizarDto;
import edu.unimag.dto.materia.MateriaCrearDto;
import edu.unimag.dto.materia.MateriaDto;
import edu.unimag.mapeador.MateriaMapeador;
import edu.unimag.modelo.Materia;
import edu.unimag.repositorio.MateriaRepositorio;

import java.util.List;

public class MateriaServicio {

    private final MateriaRepositorio repositorio;
    private final MateriaMapeador mapper;

    public MateriaServicio(MateriaRepositorio repositorio, MateriaMapeador mapper) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("El mapeador no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.mapper = mapper;
    }

    public MateriaDto registrarMateria(MateriaCrearDto dto) {
        Materia nueva = new Materia(dto.nombre(), dto.creditos(), dto.cupoMaximo());
        Materia guardada = repositorio.guardar(nueva);
        return mapper.toDto(guardada);
    }

    public List<MateriaDto> listarTodas() {
        return mapper.toDtoList(repositorio.listarTodos());
    }

    public MateriaDto actualizar(MateriaActualizarDto dto) {
        Materia materia = repositorio.buscarPorId(dto.id())
                .orElseThrow(() -> new IllegalArgumentException("No existe materia con ID: " + dto.id()));

        if (dto.nuevoNombre() != null) {
            materia.actualizarNombre(dto.nuevoNombre());
        }
        if (dto.nuevosCreditos() != null) {
            materia.actualizarCreditos(dto.nuevosCreditos());
        }
        if (dto.nuevoCupo() != null) {
            materia.actualizarCupo(dto.nuevoCupo());
        }

        Materia actualizada = repositorio.actualizar(materia);
        return mapper.toDto(actualizada);
    }

    public MateriaDto suspender(Integer id) {
        Materia materia = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe materia con ID: " + id));

        materia.suspender();
        Materia actualizada = repositorio.actualizar(materia);
        return mapper.toDto(actualizada);
    }

    public MateriaDto reactivar(Integer id) {
        Materia materia = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("No existe materia con ID: " + id));

        materia.reactivar();
        Materia actualizada = repositorio.actualizar(materia);
        return mapper.toDto(actualizada);
    }

    public MateriaDto buscarPorId(Integer id) {
        Materia materia = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException("Materia no encontrada con ID: " + id));
        return mapper.toDto(materia);
    }
}
