package edu.unimag.repositorio.cargador;

import edu.unimag.modelo.CursoProfesor;
import edu.unimag.modelo.pk.CursoProfesorPk;
import edu.unimag.repositorio.CursoProfesorRepositorio;
import edu.unimag.repositorio.api.CargadorDatos;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

public class CursoProfesorCargador implements CargadorDatos<CursoProfesor, CursoProfesorPk>{

    private final CursoProfesorRepositorio repositorio;
    private final Map<CursoProfesorPk, CursoProfesor> cache;

    public CursoProfesorCargador(CursoProfesorRepositorio repositorio) {
        if (repositorio==null) {
            throw new IllegalArgumentException("EL repositorio no puede ser nulo");
        }
        this.repositorio = repositorio;
        this.cache = new HashMap<>();
    }
    
    
    
    @Override
    public void cargarTodos() {
        this.cache.clear();
        for (CursoProfesor curso : repositorio.listarTodos()) {
            cache.put(curso.getIdCursoProfesorPeriodo(), curso);
        }
    }

    @Override
    public void cargarPorIds(List<CursoProfesorPk> ids) {
        if (ids == null || ids.isEmpty()) {
            return;
        }

        List<CursoProfesorPk> idsFaltantes = new ArrayList<>();
        for (CursoProfesorPk id : ids) {
            if (!cache.containsKey(id)) {
                idsFaltantes.add(id);
            }
        }

        if (idsFaltantes.isEmpty()) {
            return;
        }

        Set<CursoProfesorPk> idsFaltantesSet = new HashSet<>(idsFaltantes);

        for (CursoProfesor curso : repositorio.listarTodos()) {
            if (idsFaltantesSet.contains(curso.getIdCursoProfesorPeriodo())) {
                cache.put(curso.getIdCursoProfesorPeriodo(), curso);
            }
        }
    }

    @Override
    public void registrarEnCache(CursoProfesor entidad) {
        if (entidad != null && entidad.getIdCursoProfesorPeriodo() != null) {
            cache.put(entidad.getIdCursoProfesorPeriodo(), entidad);
        }
    }

    @Override
    public Optional<CursoProfesor> obtener(CursoProfesorPk id) {
       if(id == null) {
            return Optional.empty();
        }
        return Optional.ofNullable(cache.get(id)); 
    }

    @Override
    public boolean existe(CursoProfesorPk id) {
        if (id == null) {
            return false;
        }
        return cache.containsKey(id);
    }
    
}
