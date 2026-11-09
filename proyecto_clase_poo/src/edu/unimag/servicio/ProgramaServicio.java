package edu.unimag.servicio;

import edu.unimag.dto.programa.ProgramaActualizarDto;
import edu.unimag.dto.programa.ProgramaCrearDto;
import edu.unimag.dto.programa.ProgramaDto;
import edu.unimag.mapeador.ProgramaMapeador;
import edu.unimag.modelo.Facultad;
import edu.unimag.modelo.Programa;
import edu.unimag.repositorio.FacultadRepositorio;
import edu.unimag.repositorio.ProgramaRepositorio;
import edu.unimag.repositorio.cargador.FacultadCargador;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

public class ProgramaServicio {

    private final ProgramaRepositorio programaRepositorio;
    private final FacultadRepositorio facultadRepositorio;
    private final ProgramaMapeador mapeador;

    public ProgramaServicio(
            ProgramaRepositorio programaRepositorio,
            FacultadRepositorio facultadRepositorio,
            ProgramaMapeador mapeador
    ) {
        if (programaRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de programas no puede ser nulo");
        }
        if (facultadRepositorio == null) {
            throw new IllegalArgumentException("El repositorio de facultades no puede ser nulo");
        }
        if (mapeador == null) {
            throw new IllegalArgumentException("El mapeador no puede ser nulo");
        }

        this.programaRepositorio = programaRepositorio;
        this.facultadRepositorio = facultadRepositorio;
        this.mapeador = mapeador;
    }

    public ProgramaDto registrar(ProgramaCrearDto dto) {
        Facultad facultad = facultadRepositorio.buscarPorId(dto.idFacultad())
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro facultad con ID: " + dto.idFacultad()
        ));

        Programa nuevoPrograma = new Programa(dto.nombre(), dto.nivelPrograma(), facultad);

        boolean insertado = programaRepositorio.insertarSiNombreNoExiste(
                dto.nombre(),
                nuevoPrograma
        );

        if (!insertado) {
            throw new IllegalStateException(
                    "Ya existe un programa con el nombre: " + dto.nombre()
            );
        }

        FacultadCargador facultadCargador = new FacultadCargador(facultadRepositorio);
        facultadCargador.registrarEnCache(facultad);

        return mapeador.toDto(nuevoPrograma, facultadCargador);
    }

    public ProgramaDto actualizar(ProgramaActualizarDto dto) {
        Programa programa = programaRepositorio.buscarPorId(dto.id())
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe programa con ID: " + dto.id()
        ));

        boolean cambioNombre = dto.nuevoNombre() != null;
        boolean cambioNivel = dto.nuevoNivel() != null;
        boolean cambioFacultad = dto.nuevoIdFacultad() != null;

        if (!cambioNombre && !cambioNivel && !cambioFacultad) {
            return hidratarYMapearUnico(programa);
        }

        Facultad nuevaFacultad = null;
        if (cambioFacultad) {
            nuevaFacultad = facultadRepositorio.buscarPorId(dto.nuevoIdFacultad())
                    .orElseThrow(() -> new IllegalArgumentException(
                    "No se encontro facultad con ID: " + dto.nuevoIdFacultad()
            ));
        }

        if (cambioNombre) {
            programa.actualizarNombre(dto.nuevoNombre());
        }
        if (cambioNivel) {
            programa.actualizarNivel(dto.nuevoNivel());
        }
        if (cambioFacultad) {
            programa.cambiarFacultad(nuevaFacultad);
        }

        boolean actualizado = programaRepositorio.actualizarSiNoExisteEnOtra(
                programa,
                cambioNombre
        );

        if (!actualizado) {
            throw new IllegalStateException(
                    "Ya existe otro programa con el nombre: " + dto.nuevoNombre()
            );
        }
        if (cambioFacultad) {
            return hidratarYMapearUnico(programa, nuevaFacultad);
        }
        return hidratarYMapearUnico(programa);
    }

    public ProgramaDto suspender(UUID idPrograma) {
        Programa programa = programaRepositorio.buscarPorId(idPrograma)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe programa con ID: " + idPrograma
        ));

        programa.suspender();
        Programa actualizado = programaRepositorio.actualizar(programa);
        return hidratarYMapearUnico(actualizado);
    }

    public ProgramaDto reactivar(UUID idPrograma) {
        Programa programa = programaRepositorio.buscarPorId(idPrograma)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe programa con ID: " + idPrograma
        ));

        programa.reactivar();
        Programa actualizado = programaRepositorio.actualizar(programa);
        return hidratarYMapearUnico(actualizado);
    }

    public List<ProgramaDto> listarTodos() {
        List<Programa> programas = programaRepositorio.listarTodos();

        if (programas.isEmpty()) {
            return List.of();
        }

        Set<Long> idsFacultades = new HashSet<>();
        for (Programa programa : programas) {
            idsFacultades.add(programa.getIdFacultad());
        }

        FacultadCargador facultadCargador = new FacultadCargador(facultadRepositorio);
        facultadCargador.cargarPorIds(new ArrayList<>(idsFacultades));
        return mapeador.toDtoList(programas, facultadCargador);
    }

    public ProgramaDto buscarPorId(UUID idPrograma) {
        Programa programa = programaRepositorio.buscarPorId(idPrograma)
                .orElseThrow(() -> new IllegalArgumentException(
                "No existe programa con ID: " + idPrograma
        ));
        return hidratarYMapearUnico(programa);
    }

    public List<ProgramaDto> listarPorFacultad(Long idFacultad) {
        Facultad facultad = facultadRepositorio.buscarPorId(idFacultad)
                .orElseThrow(() -> new IllegalArgumentException(
                "No se encontro facultad con ID: " + idFacultad
        ));

        List<Programa> todos = programaRepositorio.listarTodos();
        List<Programa> deFacultad = new ArrayList<>();

        for (Programa programa : todos) {
            if (programa.getIdFacultad().equals(idFacultad)) {
                deFacultad.add(programa);
            }
        }

        if (deFacultad.isEmpty()) {
            return List.of();
        }

        FacultadCargador facultadCargador = new FacultadCargador(facultadRepositorio);
        facultadCargador.registrarEnCache(facultad);

        return mapeador.toDtoList(deFacultad, facultadCargador);
    }

    private ProgramaDto hidratarYMapearUnico(Programa programa) {
        Facultad facultad = facultadRepositorio.buscarPorId(programa.getIdFacultad())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Facultad no encontrada con ID: " + programa.getIdFacultad()
        ));
        return hidratarYMapearUnico(programa, facultad);
    }

    private ProgramaDto hidratarYMapearUnico(Programa programa, Facultad facultad) {
        FacultadCargador facultadCargador = new FacultadCargador(facultadRepositorio);
        facultadCargador.registrarEnCache(facultad);
        return mapeador.toDto(programa, facultadCargador);
    }

}
