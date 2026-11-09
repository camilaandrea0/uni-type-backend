package edu.unimag.mapeador;

import edu.unimag.dto.facultad.FacultadDto;
import edu.unimag.dto.programa.ProgramaDto;
import edu.unimag.modelo.Facultad;
import edu.unimag.modelo.Programa;
import edu.unimag.repositorio.cargador.FacultadCargador;

import java.util.ArrayList;
import java.util.List;

public class ProgramaMapeador {

    private final FacultadMapeador facultadMapeador;

    public ProgramaMapeador(FacultadMapeador facultadMapeador) {
        if (facultadMapeador == null) {
            throw new IllegalArgumentException("El mapeador de facultades no puede ser nulo");
        }
        this.facultadMapeador = facultadMapeador;
    }

    public List<ProgramaDto> toDtoList(
            List<Programa> entidades,
            FacultadCargador facultadCargador
    ) {
        if (entidades == null || entidades.isEmpty()) {
            return List.of();
        }
        if (facultadCargador == null) {
            throw new IllegalArgumentException("El cargador de facultades no puede ser nulo");
        }

        List<ProgramaDto> resultado = new ArrayList<>(entidades.size());

        for (Programa entity : entidades) {
            // ACA ESTA LA TRAMPA, omitir registros corruptos
            if (!validarIntegridad(entity, facultadCargador)) {
                continue;
            }
            resultado.add(transformarADto(entity, facultadCargador));
        }

        return resultado;
    }

    public ProgramaDto toDto(
            Programa entity,
            FacultadCargador facultadCargador
    ) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad Programa no puede ser nula");
        }
        if (facultadCargador == null) {
            throw new IllegalArgumentException("El cargador de facultades no puede ser nulo");
        }

        if (!validarIntegridad(entity, facultadCargador)) {
            throw new IllegalStateException(
                    "Error de integridad en Programa ID: " + entity.getIdPrograma()
                    + ". Facultad no encontrada en cache con ID: " + entity.getIdFacultad()
            );
        }
        return transformarADto(entity, facultadCargador);
    }

    private boolean validarIntegridad(
            Programa entity,
            FacultadCargador facultadCargador
    ) {
        Long idFacultad = entity.getIdFacultad();
        return facultadCargador.existe(idFacultad);
    }

    private ProgramaDto transformarADto(
            Programa entity,
            FacultadCargador facultadCargador
    ) {
        Facultad facultad = facultadCargador.obtener(entity.getIdFacultad())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Facultad no encontrada para ID: " + entity.getIdFacultad()
        ));

        FacultadDto facultadDto = facultadMapeador.toDto(facultad);

        return new ProgramaDto(
                entity.getIdPrograma(),
                entity.getNombrePrograma(),
                entity.getNivelPrograma(),
                facultadDto,
                entity.getEstadoPrograma()
        );
    }
}
