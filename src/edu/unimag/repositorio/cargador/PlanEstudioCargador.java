package edu.unimag.repositorio.cargador;

import edu.unimag.repositorio.api.CargadorDatos;
import edu.unimag.modelo.PlanEstudio;
import edu.unimag.repositorio.PlanEstudioRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PlanEstudioCargador implements CargadorDatos<PlanEstudio, Long> {

    private final PlanEstudioRepositorio repositorio;
    private final Map<Long, PlanEstudio> cache;

    public PlanEstudioCargador(PlanEstudioRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de planes de estudio no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.cache = new HashMap<>();
    }

    @Override
    public void cargarTodos() {
        this.cache.clear();
        for (PlanEstudio plan : repositorio.listarTodos()) {
            cache.put(plan.getIdPlanEstudio(), plan);
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

        for (PlanEstudio plan : repositorio.listarTodos()) {
            if (idsFaltantesSet.contains(plan.getIdPlanEstudio())) {
                cache.put(plan.getIdPlanEstudio(), plan);
            }
        }
    }

    @Override
    public void registrarEnCache(PlanEstudio entidad) {
        if (entidad != null && entidad.getIdPlanEstudio() != null) {
            cache.put(entidad.getIdPlanEstudio(), entidad);
        }
    }

    @Override
    public Optional<PlanEstudio> obtener(Long id) {
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
