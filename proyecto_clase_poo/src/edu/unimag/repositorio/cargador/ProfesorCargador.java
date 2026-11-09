package edu.unimag.repositorio.cargador;

import edu.unimag.repositorio.api.CargadorDatos;
import edu.unimag.modelo.Profesor;
import edu.unimag.repositorio.ProfesorRepositorio;
import java.util.ArrayList;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class ProfesorCargador implements CargadorDatos<Profesor, Long> {

    private final ProfesorRepositorio repositorio;
    private final Map<Long, Profesor> cache;

    public ProfesorCargador(ProfesorRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de profesores no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.cache = new HashMap<>();
    }

    @Override
    public void cargarTodos() {
        this.cache.clear();
        for (Profesor profesor : repositorio.listarTodos()) {
            cache.put(profesor.getIdProfesor(), profesor);
        }
    }

    @Override
    public void cargarPorIds(List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<Long> idsFaltantes = new ArrayList<>();
        for (Long id : ids) {
            if (!cache.containsKey(id)) {
                idsFaltantes.add(id);
            }
        }

        if (idsFaltantes.isEmpty()) {
            return;
        }

        Set<Long> idsFaltantesSet = new HashSet<>(idsFaltantes);

        for (Profesor profesor : repositorio.listarTodos()) {
            if (idsFaltantesSet.contains(profesor.getIdProfesor())) {
                cache.put(profesor.getIdProfesor(), profesor);
            }
        }
    }

    @Override
    public void registrarEnCache(Profesor entidad) {
        if (entidad != null && entidad.getIdProfesor() != null) {
            cache.put(entidad.getIdProfesor(), entidad);
        }
    }

    @Override
    public Optional<Profesor> obtener(Long id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public boolean existe(Long id) {
        if (id == null) {
            return false;
        }
        return cache.containsKey(id);
    }
}
