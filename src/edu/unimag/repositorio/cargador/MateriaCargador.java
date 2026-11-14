package edu.unimag.repositorio.cargador;

import edu.unimag.repositorio.api.CargadorDatos;
import edu.unimag.modelo.Materia;
import edu.unimag.repositorio.MateriaRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class MateriaCargador implements CargadorDatos<Materia, Integer> {

    private final MateriaRepositorio repositorio;
    private final Map<Integer, Materia> cache;

    public MateriaCargador(MateriaRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de materias no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.cache = new HashMap<>();
    }

    @Override
    public void cargarTodos() {
        this.cache.clear();
        for (Materia materia : repositorio.listarTodos()) {
            cache.put(materia.getIdMateria(), materia);
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
        for (Materia materia : repositorio.listarTodos()) {
            if (idsFaltantesSet.contains(materia.getIdMateria())) {
                cache.put(materia.getIdMateria(), materia);
            }
        }
    }

    @Override
    public void registrarEnCache(Materia entidad) {
        if (entidad != null && entidad.getIdMateria() != null) {
            cache.put(entidad.getIdMateria(), entidad);
        }
    }

    @Override
    public Optional<Materia> obtener(Integer id) {
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
