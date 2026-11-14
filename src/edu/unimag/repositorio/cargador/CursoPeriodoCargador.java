package edu.unimag.repositorio.cargador;

import edu.unimag.repositorio.api.CargadorDatos;
import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.repositorio.CursoPeriodoRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class CursoPeriodoCargador implements CargadorDatos<CursoPeriodo, UUID> {

    private final CursoPeriodoRepositorio repositorio;
    private final Map<UUID, CursoPeriodo> cache;

    public CursoPeriodoCargador(CursoPeriodoRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de cursos por periodo no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.cache = new HashMap<>();
    }

    @Override
    public void cargarTodos() {
        this.cache.clear();
        for (CursoPeriodo curso : repositorio.listarTodos()) {
            cache.put(curso.getIdCursoPeriodo(), curso);
        }
    }

    @Override
    public void cargarPorIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<UUID> idsFaltantes = new ArrayList<>();
        for (UUID id : ids) {
            if (!cache.containsKey(id)) {
                idsFaltantes.add(id);
            }
        }

        if (idsFaltantes.isEmpty()) {
            return;
        }

        Set<UUID> idsFaltantesSet = new HashSet<>(idsFaltantes);

        for (CursoPeriodo curso : repositorio.listarTodos()) {
            if (idsFaltantesSet.contains(curso.getIdCursoPeriodo())) {
                cache.put(curso.getIdCursoPeriodo(), curso);
            }
        }
    }

    @Override
    public void registrarEnCache(CursoPeriodo entidad) {
        if (entidad != null && entidad.getIdCursoPeriodo() != null) {
            cache.put(entidad.getIdCursoPeriodo(), entidad);
        }
    }

    @Override
    public Optional<CursoPeriodo> obtener(UUID id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public boolean existe(UUID id) {
        if (id == null) {
            return false;
        }
        return cache.containsKey(id);
    }
}
