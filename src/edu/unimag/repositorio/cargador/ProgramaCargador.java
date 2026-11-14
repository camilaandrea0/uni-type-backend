package edu.unimag.repositorio.cargador;

import edu.unimag.repositorio.api.CargadorDatos;
import edu.unimag.modelo.Programa;
import edu.unimag.repositorio.ProgramaRepositorio;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

public class ProgramaCargador implements CargadorDatos<Programa, UUID> {

    private final ProgramaRepositorio repositorio;
    private final Map<UUID, Programa> cache;

    public ProgramaCargador(ProgramaRepositorio repositorio) {
        if (repositorio == null) {
            throw new IllegalArgumentException("El repositorio de programas no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.cache = new HashMap<>();
    }

    @Override
    public void cargarTodos() {
        this.cache.clear();
        for (Programa programa : repositorio.listarTodos()) {
            cache.put(programa.getIdPrograma(), programa);
        }
    }

    @Override
    public void cargarPorIds(List<UUID> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        // Identificar qué IDs faltan en el cache
        List<UUID> idsFaltantes = new ArrayList<>();
        for (UUID id : ids) {
            if (!cache.containsKey(id)) {
                idsFaltantes.add(id);
            }
        }

        if (idsFaltantes.isEmpty()) {
            return;
        }

        // Convertir a Set para búsqueda optimizada
        Set<UUID> idsFaltantesSet = new HashSet<>(idsFaltantes);

        // Consultar el repositorio solo por los faltantes
        for (Programa programa : repositorio.listarTodos()) {
            if (idsFaltantesSet.contains(programa.getIdPrograma())) {
                cache.put(programa.getIdPrograma(), programa);
            }
        }
    }

    @Override
    public void registrarEnCache(Programa entidad) {
        if (entidad != null && entidad.getIdPrograma() != null) {
            cache.put(entidad.getIdPrograma(), entidad);
        }
    }

    @Override
    public Optional<Programa> obtener(UUID id) {
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
