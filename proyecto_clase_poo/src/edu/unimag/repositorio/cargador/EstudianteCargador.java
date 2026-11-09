package edu.unimag.repositorio.cargador;

import edu.unimag.repositorio.api.CargadorDatos;
import edu.unimag.modelo.Estudiante;
import edu.unimag.repositorio.EstudianteRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class EstudianteCargador implements CargadorDatos<Estudiante, UUID> {

    private final EstudianteRepositorio repositorio;
    private final Map<UUID, Estudiante> cache;

    public EstudianteCargador(EstudianteRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de estudiantes no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.cache = new HashMap<>();
    }

    @Override
    public void cargarTodos() {
        this.cache.clear();
        for (Estudiante estudiante : repositorio.listarTodos()) {
            cache.put(estudiante.getIdEstudiante(), estudiante);
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
        for (Estudiante estudiante : repositorio.listarTodos()) {
            if (idsFaltantesSet.contains(estudiante.getIdEstudiante())) {
                cache.put(estudiante.getIdEstudiante(), estudiante);
            }
        }
    }

    @Override
    public void registrarEnCache(Estudiante entidad) {
        if (entidad != null && entidad.getIdEstudiante() != null) {
            cache.put(entidad.getIdEstudiante(), entidad);
        }
    }

    @Override
    public Optional<Estudiante> obtener(UUID id) {
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
