package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaId;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CursoPeriodo {

    @TpaId
    private UUID idCursoPeriodo;
    private String nombreCursoPeriodo;
    private OffsetDateTime creacionCursoPeriodo;
    private Integer cupoCursoPeriodo;
    private Materia materia;
    private PeriodoAcademico periodoAcademico;

    // Reflexión
    protected CursoPeriodo() {
    }

    // Creación
    public CursoPeriodo(
            String nombreCursoPeriodo,
            Materia materia,
            PeriodoAcademico periodoAcademico,
            Integer cupoCursoPeriodo
    ) {
        this.nombreCursoPeriodo = nombreCursoPeriodo;
        this.materia = materia;
        this.periodoAcademico = periodoAcademico;
        this.cupoCursoPeriodo = cupoCursoPeriodo;
        this.creacionCursoPeriodo = OffsetDateTime.now();
    }

    // Hidratación (Defensa contra datos corruptos del ORM)
    public CursoPeriodo(
            UUID idCursoPeriodo,
            String nombreCursoPeriodo,
            OffsetDateTime creacionCursoPeriodo,
            Integer cupoCursoPeriodo,
            Materia materia,
            PeriodoAcademico periodoAcademico
    ) {
        this.nombreCursoPeriodo = nombreCursoPeriodo;
        this.cupoCursoPeriodo = cupoCursoPeriodo;
        this.materia = materia;
        this.periodoAcademico = periodoAcademico;
        this.creacionCursoPeriodo = (creacionCursoPeriodo != null) ? creacionCursoPeriodo : OffsetDateTime.now();

        if (idCursoPeriodo == null) {
            throw new IllegalArgumentException("El ID del curso periodo es obligatorio en hidratacion");
        }
        this.idCursoPeriodo = idCursoPeriodo;
    }

    // Métodos de comportamiento
    public void actualizarNombre(String nuevoNombre) {
        if (nuevoNombre.equalsIgnoreCase(this.nombreCursoPeriodo)) {
            throw new IllegalArgumentException("El nuevo nombre es igual al actual, no hay cambios");
        }
        this.nombreCursoPeriodo = nuevoNombre;
    }

    public void actualizarCupo(Integer nuevoCupo) {
        if (nuevoCupo.equals(this.cupoCursoPeriodo)) {
            throw new IllegalArgumentException("El nuevo cupo es igual al actual");
        }
        this.cupoCursoPeriodo = nuevoCupo;
    }

    public boolean tieneCupoDisponible(int cantidadInscritos) {
        return cantidadInscritos < this.cupoCursoPeriodo;
    }

    // Getters
    public UUID getIdCursoPeriodo() {
        return idCursoPeriodo;
    }

    public String getNombreCursoPeriodo() {
        return nombreCursoPeriodo;
    }

    public OffsetDateTime getCreacionCursoPeriodo() {
        return creacionCursoPeriodo;
    }

    public Integer getCupoCursoPeriodo() {
        return cupoCursoPeriodo;
    }

    public Materia getMateria() {
        return materia;
    }

    public PeriodoAcademico getPeriodoAcademico() {
        return periodoAcademico;
    }

    // Getters de conveniencia (Ley de Demeter)
    public Integer getIdMateria() {
        return this.materia.getIdMateria();
    }

    public Integer getIdPeriodoAcademico() {
        return this.periodoAcademico.getIdPeriodoAcademico();
    }
}
