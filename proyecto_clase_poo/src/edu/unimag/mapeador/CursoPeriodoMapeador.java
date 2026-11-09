package edu.unimag.mapeador;

import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.materia.MateriaDto;
import edu.unimag.dto.periodoacademico.PeriodoAcademicoDto;
import edu.unimag.modelo.CursoPeriodo;
import edu.unimag.modelo.Materia;
import edu.unimag.modelo.PeriodoAcademico;
import edu.unimag.repositorio.cargador.MateriaCargador;
import edu.unimag.repositorio.cargador.PeriodoAcademicoCargador;

import java.util.ArrayList;
import java.util.List;

public class CursoPeriodoMapeador {

    private final MateriaMapeador materiaMapeador;
    private final PeriodoAcademicoMapeador periodoAcademicoMapeador;

    public CursoPeriodoMapeador(MateriaMapeador materiaMapeador, PeriodoAcademicoMapeador periodoAcademicoMapeador) {
        if (materiaMapeador == null) {
            throw new IllegalArgumentException("El mapeador de materias no puede ser nulo");
        }
        if (periodoAcademicoMapeador == null) {
            throw new IllegalArgumentException("El mapeador de periodos académicos no puede ser nulo");
        }
        this.materiaMapeador = materiaMapeador;
        this.periodoAcademicoMapeador = periodoAcademicoMapeador;
    }

    public List<CursoPeriodoDto> toDtoList(
            List<CursoPeriodo> entities,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador) {

        if (entities == null || entities.isEmpty()) {
            return List.of();
        }
        if (materiaCargador == null) {
            throw new IllegalArgumentException("El cargador de materias no puede ser nulo");
        }
        if (periodoCargador == null) {
            throw new IllegalArgumentException("El cargador de periodos academicos no puede ser nulo");
        }

        List<CursoPeriodoDto> resultado = new ArrayList<>(entities.size());
        for (CursoPeriodo entity : entities) {
            if (!validarIntegridad(entity, materiaCargador, periodoCargador)) {
                continue;
            }
            resultado.add(transformarADto(entity, materiaCargador, periodoCargador));
        }
        return resultado;
    }

    public CursoPeriodoDto toDto(
            CursoPeriodo entity,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador) {

        if (entity == null) {
            throw new IllegalArgumentException("La entidad de curso periodo no puede ser nula");
        }
        if (materiaCargador == null) {
            throw new IllegalArgumentException("El cargador de materias no puede ser nulo");
        }
        if (periodoCargador == null) {
            throw new IllegalArgumentException("El cargador de periodos academicos no puede ser nulo");
        }

        if (!validarIntegridad(entity, materiaCargador, periodoCargador)) {
            throw new IllegalStateException(
                    "Error de integridad en CursoPeriodo ID: " + entity.getIdCursoPeriodo()
                    + ". Materia o Periodo no encontrados en caché."
            );
        }

        return transformarADto(entity, materiaCargador, periodoCargador);
    }

    private boolean validarIntegridad(
            CursoPeriodo entity,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador) {

        Integer idMateria = entity.getIdMateria();
        Integer idPeriodo = entity.getIdPeriodoAcademico();

        return materiaCargador.existe(idMateria) && periodoCargador.existe(idPeriodo);
    }

    private CursoPeriodoDto transformarADto(
            CursoPeriodo entity,
            MateriaCargador materiaCargador,
            PeriodoAcademicoCargador periodoCargador) {

        Materia materia = materiaCargador.obtener(entity.getIdMateria())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Materia no encontrada para ID: " + entity.getIdMateria()
        ));

        PeriodoAcademico periodo = periodoCargador.obtener(entity.getIdPeriodoAcademico())
                .orElseThrow(() -> new IllegalStateException(
                "Error de integridad: Periodo academico no encontrado para ID: " + entity.getIdPeriodoAcademico()
        ));

        MateriaDto materiaDto = materiaMapeador.toDto(materia);
        PeriodoAcademicoDto periodoDto = periodoAcademicoMapeador.toDto(periodo);

        return new CursoPeriodoDto(
                entity.getIdCursoPeriodo(),
                entity.getNombreCursoPeriodo(),
                materiaDto,
                periodoDto,
                entity.getCupoCursoPeriodo(),
                entity.getCreacionCursoPeriodo()
        );
    }
}
