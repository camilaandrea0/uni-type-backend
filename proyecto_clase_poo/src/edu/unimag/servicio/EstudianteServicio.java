package edu.unimag.servicio;

import edu.unimag.dto.estudiante.EstudianteActualizarDto;
import edu.unimag.dto.estudiante.EstudianteCrearDto;
import edu.unimag.dto.estudiante.EstudianteDto;
import edu.unimag.mapeador.EstudianteMapeador;
import edu.unimag.modelo.Estudiante;
import edu.unimag.modelo.enumeracion.EstadoEntidad;
import edu.unimag.repositorio.EstudianteRepositorio;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class EstudianteServicio {

    private final EstudianteRepositorio repositorio;
    private final EstudianteMapeador mapper;

    public EstudianteServicio(EstudianteRepositorio repositorio, EstudianteMapeador mapper) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("El mapeador no puede ser nulo");
        }

        this.repositorio = repositorio;
        this.mapper = mapper;
    }

    public EstudianteDto registrarNuevoEstudiante(EstudianteCrearDto dto) {
        Estudiante nuevo = new Estudiante(
                dto.codigo(),
                dto.nombre(),
                dto.correo(),
                dto.celular(),
                dto.direccion()
        );

        boolean insertado = repositorio.insertarSiCodigoNoExiste(dto.codigo(), nuevo);
        if (!insertado) {
            throw new IllegalStateException("Ya existe un estudiante registrado con el código: " + dto.codigo());
        }
        return mapper.toDto(nuevo);
    }

    public EstudianteDto actualizarDatos(EstudianteActualizarDto dto) {
        Estudiante estudiante = repositorio.buscarPorId(dto.id())
                .orElseThrow(() -> new IllegalArgumentException(
                "Estudiante no encontrado con ID: " + dto.id()
        ));

        if (dto.correo() != null) {
            estudiante.actualizarCorreo(dto.correo());
        }

        if (dto.celular() != null) {
            estudiante.actualizarCelular(dto.celular());
        }

        if (dto.direccion() != null) {
            estudiante.actualizarDireccion(dto.direccion());
        }

        EstadoEntidad nuevoEstado = dto.obtenerEstadoComoEnum();
        if (nuevoEstado != null) {
            estudiante.cambiarEstado(nuevoEstado);
        }

        Estudiante actualizado = repositorio.actualizar(estudiante);
        return mapper.toDto(actualizado);
    }

    public EstudianteDto activar(UUID id) {
        Estudiante estudiante = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                "Estudiante no encontrado con ID: " + id
        ));

        estudiante.cambiarEstado(EstadoEntidad.ACTIVO);
        Estudiante actualizado = repositorio.actualizar(estudiante);
        return mapper.toDto(actualizado);
    }

    public EstudianteDto inactivar(UUID id) {
        Estudiante estudiante = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                "Estudiante no encontrado con ID: " + id
        ));

        estudiante.cambiarEstado(EstadoEntidad.INACTIVO);
        Estudiante actualizado = repositorio.actualizar(estudiante);
        return mapper.toDto(actualizado);
    }

    public EstudianteDto buscarPorId(UUID id) {
        Estudiante estudiante = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                "Estudiante no encontrado con ID: " + id
        ));
        return mapper.toDto(estudiante);
    }

    public List<EstudianteDto> listarTodos() {
        return mapper.toDtoList(repositorio.listarTodos());
    }

    public List<EstudianteDto> listarActivos() {
        List<Estudiante> todos = repositorio.listarTodos();
        List<Estudiante> activos = new ArrayList<>();

        for (Estudiante estudiante : todos) {
            if (estudiante.estaActivo()) {
                activos.add(estudiante);
            }
        }

        return mapper.toDtoList(activos);
    }

    public List<EstudianteDto> listarInactivos() {
        List<Estudiante> todos = repositorio.listarTodos();
        List<Estudiante> inactivos = new ArrayList<>();

        for (Estudiante estudiante : todos) {
            if (!estudiante.estaActivo()) {
                inactivos.add(estudiante);
            }
        }

        return mapper.toDtoList(inactivos);
    }

    public boolean estaActivo(UUID id) {
        Estudiante estudiante = repositorio.buscarPorId(id).orElse(null);
        if (estudiante == null) {
            return false;
        }
        return estudiante.estaActivo();
    }

    public int contarEstudiantes() {
        return (int) repositorio.contar();
    }
}
