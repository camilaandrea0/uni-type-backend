package edu.unimag.repositorio.cargador;

import edu.unimag.repositorio.api.CargadorDatos;
import edu.unimag.modelo.PeriodoAcademico;
import edu.unimag.repositorio.PeriodoAcademicoRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class PeriodoAcademicoCargador implements CargadorDatos<PeriodoAcademico, Integer> {

    private final PeriodoAcademicoRepositorio repositorio;
    private final Map<Integer, PeriodoAcademico> cache;

    public PeriodoAcademicoCargador(PeriodoAcademicoRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de periodos académicos no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.cache = new HashMap<>();
    }

    @Override
    public void cargarTodos() {
        this.cache.clear();
        for (PeriodoAcademico periodo : repositorio.listarTodos()) {
            cache.put(periodo.getIdPeriodoAcademico(), periodo);
        }
    }

    @Override
    public void cargarPorIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<Integer> idsFaltantes = new ArrayList<>();
        for (Integer id : ids) {
            if (!cache.containsKey(id)) {
                idsFaltantes.add(id);
            }
        }

        if (idsFaltantes.isEmpty()) {
            return;
        }

        Set<Integer> idsFaltantesSet = new HashSet<>(idsFaltantes);
        for (PeriodoAcademico periodo : repositorio.listarTodos()) {
            if (idsFaltantesSet.contains(periodo.getIdPeriodoAcademico())) {
                cache.put(periodo.getIdPeriodoAcademico(), periodo);
            }
        }
    }

    @Override
    public void registrarEnCache(PeriodoAcademico entidad) {
        if (entidad != null && entidad.getIdPeriodoAcademico() != null) {
            cache.put(entidad.getIdPeriodoAcademico(), entidad);
        }
    }

    @Override
    public Optional<PeriodoAcademico> obtener(Integer id) {
        if (id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.get(id));
    }

    @Override
    public boolean existe(Integer id) {
        if (id == null) {
            return false;
        }
        return cache.containsKey(id);
    }
}
