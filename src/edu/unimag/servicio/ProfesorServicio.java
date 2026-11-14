package edu.unimag.servicio;

import edu.unimag.dto.profesor.ProfesorActualizarDto;
import edu.unimag.dto.profesor.ProfesorCrearDto;
import edu.unimag.dto.profesor.ProfesorDto;
import edu.unimag.mapeador.ProfesorMapeador;
import edu.unimag.modelo.Profesor;
import edu.unimag.modelo.enumeracion.EstadoEntidad;
import edu.unimag.repositorio.ProfesorRepositorio;

import java.util.ArrayList;
import java.util.List;

public class ProfesorServicio {

    private final ProfesorRepositorio repositorio;
    private final ProfesorMapeador mapper;

    public ProfesorServicio(ProfesorRepositorio repositorio, ProfesorMapeador mapper) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio no puede ser nulo");
        }
        if (mapper == null) {
            throw new IllegalArgumentException("El mapeador no puede ser nulo");
        }

        this.repositorio = repositorio;
        this.mapper = mapper;
    }

    public ProfesorDto registrarProfesor(ProfesorCrearDto dto) {
        Profesor nuevoProfesor = new Profesor(dto.nombre(), dto.celular());
        Profesor guardado = repositorio.guardar(nuevoProfesor);
        return mapper.toDto(guardado);
    }

    public ProfesorDto actualizar(ProfesorActualizarDto dto) {
        Profesor profesor = repositorio.buscarPorId(dto.id())
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe profesor con ID: " + dto.id()
        ));

        if (dto.nuevoNombre() != null) {
            profesor.actualizarNombre(dto.nuevoNombre());
        }

        if (dto.nuevoCelular() != null) {
            profesor.actualizarCelular(dto.nuevoCelular());
        }

        EstadoEntidad nuevoEstado = dto.obtenerEstadoComoEnum();
        if (nuevoEstado != null) {
            profesor.cambiarEstado(nuevoEstado);
        }

        Profesor actualizado = repositorio.actualizar(profesor);
        return mapper.toDto(actualizado);
    }

    public ProfesorDto buscarPorId(Long id) {
        Profesor profesor = repositorio.buscarPorId(id)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe profesor con ID: " + id
        ));
        return mapper.toDto(profesor);
    }

    public List<ProfesorDto> listarTodos() {
        return mapper.toDtoList(repositorio.listarTodos());
    }

    public List<ProfesorDto> listarActivos() {
        List<Profesor> todos = repositorio.listarTodos();
        List<Profesor> activos = new ArrayList<>();

        for (Profesor profesor : todos) {
            if (profesor.estaActivo()) {
                activos.add(profesor);
            }
        }
        return mapper.toDtoList(activos);
    }

    public List<ProfesorDto> listarInactivos() {
        List<Profesor> todos = repositorio.listarTodos();
        List<Profesor> inactivos = new ArrayList<>();

        for (Profesor profesor : todos) {
            if (!profesor.estaActivo()) {
                inactivos.add(profesor);
            }
        }

        return mapper.toDtoList(inactivos);
    }

    public boolean estaActivo(Long id) {
        Profesor profesor = repositorio.buscarPorId(id).orElse(null);
        if (profesor == null) {
            return false;
        }
        return profesor.estaActivo();
    }

    public int contarProfesores() {
        return (int) repositorio.contar();
    }
}
