package edu.unimag.repositorio.cargador;

import edu.unimag.repositorio.api.CargadorDatos;
import edu.unimag.modelo.Facultad;
import edu.unimag.repositorio.FacultadRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class FacultadCargador implements CargadorDatos<Facultad, Long> {

    private final FacultadRepositorio repositorio;
    private final Map<Long, Facultad> cache;

    public FacultadCargador(FacultadRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de facultades no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.cache = new HashMap<>();
    }

    @Override
    public void cargarTodos() {
        this.cache.clear();
        for (Facultad facultad : repositorio.listarTodos()) {
            cache.put(facultad.getIdFacultad(), facultad);
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
        for (Facultad facultad : repositorio.listarTodos()) {
            if (idsFaltantesSet.contains(facultad.getIdFacultad())) {
                cache.put(facultad.getIdFacultad(), facultad);
            }
        }
    }

    @Override
    public void registrarEnCache(Facultad entidad) {
        if (entidad != null && entidad.getIdFacultad() != null) {
            cache.put(entidad.getIdFacultad(), entidad);
        }
    }

    @Override
    public Optional<Facultad> obtener(Long id) {
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
