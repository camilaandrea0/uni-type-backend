package edu.unimag.servicio;

import edu.unimag.dto.formacion.FormacionProfesorActualizarDto;
import edu.unimag.dto.formacion.FormacionProfesorCrearDto;
import edu.unimag.dto.formacion.FormacionProfesorDto;
import edu.unimag.mapeador.FormacionProfesorMapeador;
import edu.unimag.modelo.FormacionProfesor;
import edu.unimag.modelo.Profesor;
import edu.unimag.repositorio.FormacionProfesorRepositorio;
import edu.unimag.repositorio.ProfesorRepositorio;
import edu.unimag.repositorio.cargador.ProfesorCargador;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class FormacionProfesorServicio {

    private final FormacionProfesorRepositorio formacionRepositorio;
    private final ProfesorRepositorio profesorRepositorio;
    private final FormacionProfesorMapeador mapeador;

    public FormacionProfesorServicio(
            FormacionProfesorRepositorio formacionRepositorio,
            ProfesorRepositorio profesorRepositorio,
            FormacionProfesorMapeador mapeador) {

        if (formacionRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de formaciones no puede ser nulo");
        }
        if (profesorRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de profesores no puede ser nulo");
        }
        if (mapeador == null) {
            throw new IllegalArgumentException("El mapeador no puede ser nulo");
        }

        this.formacionRepositorio = formacionRepositorio;
        this.profesorRepositorio = profesorRepositorio;
        this.mapeador = mapeador;
    }

    public FormacionProfesorDto registrarFormacion(FormacionProfesorCrearDto dto) {
        // Esto se le dice Fail-Fast: validar que el Profesor existe
        Profesor profesor = profesorRepositorio.buscarPorId(dto.idProfesor())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro profesor con ID: " + dto.idProfesor()
        ));

        // Construir modelo con Profesor completo
        FormacionProfesor nuevaFormacion = new FormacionProfesor(profesor, dto.titulo(), dto.fecha());
        FormacionProfesor guardada = formacionRepositorio.guardar(nuevaFormacion);

        // Crear cargador efímero y registrar el profesor en caché
        ProfesorCargador cargador = new ProfesorCargador(profesorRepositorio);
        cargador.registrarEnCache(profesor);

        return mapeador.toDto(guardada, cargador);
    }

    public List<FormacionProfesorDto> listarFormaciones() {
        List<FormacionProfesor> formaciones = formacionRepositorio.listarTodos();

        if (formaciones.isEmpty()) {
            return new ArrayList<>();
        }

        // Extraer IDs únicos de profesores
        Set<Long> idsSet = new HashSet<>();
        for (FormacionProfesor f : formaciones) {
            idsSet.add(f.getIdProfesor());
        }

        // Cargar todos los profesores en memoria
        ProfesorCargador cargador = new ProfesorCargador(profesorRepositorio);
        cargador.cargarPorIds(new ArrayList<>(idsSet));

        return mapeador.toDtoList(formaciones, cargador);
    }

    public FormacionProfesorDto actualizarFormacion(FormacionProfesorActualizarDto dto) {
        FormacionProfesor formacion = formacionRepositorio.buscarPorId(dto.id())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro ninguna formacion con ID: " + dto.id()
        ));

        if (dto.nuevoTitulo() != null) {
            formacion.actualizarTitulo(dto.nuevoTitulo());
        }
        if (dto.nuevaFecha() != null) {
            formacion.actualizarFecha(dto.nuevaFecha());
        }

        FormacionProfesor actualizada = formacionRepositorio.actualizar(formacion);
        return hidratarYMapearUnico(actualizada);
    }

    public boolean eliminarFormacion(Long id) {
        if (!formacionRepositorio.buscarPorId(id).isPresent()) {
            throw new IllegalArgumentException(
                    "No se puede eliminar: Formacion no encontrada con ID: " + id
            );
        }
        return formacionRepositorio.eliminar(id);
    }

    public List<FormacionProfesorDto> listarFormacionesPorProfesor(Long idProfesor) {
        Profesor profesor = profesorRepositorio.buscarPorId(idProfesor)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro profesor con ID: " + idProfesor
        ));

        List<FormacionProfesor> todas = formacionRepositorio.listarTodos();
        List<FormacionProfesor> delProfesor = new ArrayList<>();

        for (FormacionProfesor f : todas) {
            if (f.getIdProfesor().equals(idProfesor)) {
                delProfesor.add(f);
            }
        }

        if (delProfesor.isEmpty()) {
            return new ArrayList<>();
        }

        // Registrar el profesor en caché
        ProfesorCargador cargador = new ProfesorCargador(profesorRepositorio);
        cargador.registrarEnCache(profesor);

        return mapeador.toDtoList(delProfesor, cargador);
    }

    private FormacionProfesorDto hidratarYMapearUnico(FormacionProfesor formacion) {
        Profesor profesorReal = profesorRepositorio.buscarPorId(formacion.getIdProfesor())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Profesor no encontrado con ID: "
                + formacion.getIdProfesor()
        ));

        // El truco, crear cargador efímero y registrar el profesor en caché
        ProfesorCargador cargador = new ProfesorCargador(profesorRepositorio);
        cargador.registrarEnCache(profesorReal);

        return mapeador.toDto(formacion, cargador);
    }
}
