package edu.unimag.mapeador;

import edu.unimag.dto.planestudio.PlanEstudioDto;
import edu.unimag.dto.programa.ProgramaDto;
import edu.unimag.modelo.PlanEstudio;
import edu.unimag.modelo.Programa;
import edu.unimag.repositorio.cargador.FacultadCargador;
import edu.unimag.repositorio.cargador.ProgramaCargador;

import java.util.ArrayList;
import java.util.List;

public class PlanEstudioMapeador {

    private final ProgramaMapeador programaMapeador;

    public PlanEstudioMapeador(ProgramaMapeador programaMapeador) {
        if (programaMapeador == null) {
            throw new IllegalArgumentException("El mapeador de programas no puede ser nulo");
        }
        this.programaMapeador = programaMapeador;
    }

    public List<PlanEstudioDto> toDtoList(
            List<PlanEstudio> entidades,
            ProgramaCargador programaCargador,
            FacultadCargador facultadCargador
    ) {
        if (entidades == null || entidades.isEmpty()) {
            return List.of();
        }
        if (programaCargador == null) {
            throw new IllegalArgumentException("El cargador de programas no puede ser nulo");
        }
        if (facultadCargador == null) {
            throw new IllegalArgumentException("El cargador de facultades no puede ser nulo");
        }

        List<PlanEstudioDto> resultado = new ArrayList<>(entidades.size());

        for (PlanEstudio entity : entidades) {
            if (!validarIntegridad(entity, programaCargador, facultadCargador)) {
                continue;
            }
            resultado.add(transformarADto(entity, programaCargador, facultadCargador));
        }
        return resultado;
    }

    public PlanEstudioDto toDto(
            PlanEstudio entity,
            ProgramaCargador programaCargador,
            FacultadCargador facultadCargador
    ) {
        if (entity == null) {
            throw new IllegalArgumentException("La entidad PlanEstudio no puede ser nula");
        }
        if (programaCargador == null) {
            throw new IllegalArgumentException("El cargador de programas no puede ser nulo");
        }
        if (facultadCargador == null) {
            throw new IllegalArgumentException("El cargador de facultades no puede ser nulo");
        }

        if (!validarIntegridad(entity, programaCargador, facultadCargador)) {
            // Determinar qué falló
            if (!programaCargador.existe(entity.getIdPrograma())) {
                throw new IllegalStateException(
                        "Error de integridad en PlanEstudio ID: " + entity.getIdPlanEstudio()
                        + ". Programa no encontrado en caché con ID: " + entity.getIdPrograma()
                );
            }
            // Si el programa existe pero la facultad no
            Programa programa = programaCargador.obtener(entity.getIdPrograma()).orElse(null);
            if (programa != null && !facultadCargador.existe(programa.getIdFacultad())) {
                throw new IllegalStateException(
                        "Error de integridad en PlanEstudio ID: " + entity.getIdPlanEstudio()
                        + ". Facultad no encontrada en caché con ID: " + programa.getIdFacultad()
                );
            }
            throw new IllegalStateException(
                    "Error de integridad en PlanEstudio ID: " + entity.getIdPlanEstudio()
            );
        }

        return transformarADto(entity, programaCargador, facultadCargador);
    }

    private boolean validarIntegridad(
            PlanEstudio entity,
            ProgramaCargador programaCargador,
            FacultadCargador facultadCargador
    ) {
        // Verificar que el programa esté en el cache
        var idPrograma = entity.getIdPrograma();
        if (!programaCargador.existe(idPrograma)) {
            return false;
        }

        // Obtener el programa del cache para saber su facultad
        Programa programa = programaCargador.obtener(idPrograma).orElse(null);
        if (programa == null) {
            return false;
        }

        // Verificar que la facultad del programa esté en el cache
        var idFacultad = programa.getIdFacultad();
        return facultadCargador.existe(idFacultad);
    }

    private PlanEstudioDto transformarADto(
            PlanEstudio entity,
            ProgramaCargador programaCargador,
            FacultadCargador facultadCargador
    ) {
        // Obtener el Programa del cache
        Programa programa = programaCargador.obtener(entity.getIdPrograma())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Programa no encontrado para ID: " + entity.getIdPrograma()
        ));

        // Convertir Programa a ProgramaDto (requiere FacultadCargador)
        ProgramaDto programaDto = programaMapeador.toDto(programa, facultadCargador);

        // Construir el PlanEstudioDto con el ProgramaDto hidratado
        return new PlanEstudioDto(
                entity.getIdPlanEstudio(),
                entity.getVersionPlanEstudio(),
                entity.getVigentePlanEstudio(),
                entity.getFechaCreacionPlanEstudio(),
                programaDto
        );
    }
}
