package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaConvert;
import com.cleandev.tpa.api.annotations.TpaId;
import edu.unimag.modelo.convertidor.EstadoInscripcionConverter;
import edu.unimag.modelo.enumeracion.EstadoInscripcion;
import edu.unimag.modelo.pk.CursoEstudiantePk;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CursoEstudiante {

    @TpaId
    private CursoEstudiantePk idCursoEstudiante;

    @TpaConvert(converter = EstadoInscripcionConverter.class)
    private EstadoInscripcion estadoInscripcion;

    private Integer calificacionCursoEstudiante;
    private String observacionCursoEstudiante;
    private OffsetDateTime fechaInscripcion;
    private OffsetDateTime fechaEvaluacion;

    // Reflexion
    protected CursoEstudiante() {
    }

    // Creacion
    public CursoEstudiante(CursoEstudiantePk pk) {
        if (pk == null) {
            throw new IllegalArgumentException("La clave de la inscripcion es obligatoria");
        }
        this.idCursoEstudiante = pk;
        this.estadoInscripcion = EstadoInscripcion.INSCRITO;
        this.calificacionCursoEstudiante = null;
        this.observacionCursoEstudiante = null;
        this.fechaInscripcion = OffsetDateTime.now();
        this.fechaEvaluacion = null;
    }

    // Hidratacion
    public CursoEstudiante(
            CursoEstudiantePk pk,
            EstadoInscripcion estado,
            Integer calificacion,
            String observacion,
            OffsetDateTime fechaInscripcion,
            OffsetDateTime fechaEvaluacion
    ) {
        if (pk == null) {
            throw new IllegalArgumentException("La clave de la inscripcion es obligatoria en hidratacion");
        }
        this.idCursoEstudiante = pk;
        this.estadoInscripcion = (estado != null) ? estado : EstadoInscripcion.INSCRITO;
        this.calificacionCursoEstudiante = calificacion;
        this.observacionCursoEstudiante = observacion;
        this.fechaInscripcion = (fechaInscripcion != null) ? fechaInscripcion : OffsetDateTime.now();
        this.fechaEvaluacion = fechaEvaluacion;
    }

    // Metodos de comportamiento (reglas de estado en el modelo)
    public void admitir() {
        this.estadoInscripcion = estadoInscripcion.cambiarEstadoA(EstadoInscripcion.ADMITIDO);
    }

    public void rechazar() {
        this.estadoInscripcion = estadoInscripcion.cambiarEstadoA(EstadoInscripcion.RECHAZADO);
    }

    public void retirar() {
        this.estadoInscripcion = estadoInscripcion.cambiarEstadoA(EstadoInscripcion.RETIRADO);
    }

    public void evaluar(Integer calificacion, String observacion) {
        EstadoInscripcion nuevoEstado = EstadoInscripcion.desdeCalificacion(calificacion);
        this.estadoInscripcion = estadoInscripcion.cambiarEstadoA(nuevoEstado);
        this.calificacionCursoEstudiante = calificacion;
        this.observacionCursoEstudiante = (observacion == null || observacion.isBlank())
                ? null
                : observacion.trim();
        this.fechaEvaluacion = OffsetDateTime.now();
    }

    // Metodos de consulta
    public boolean estaInscrito() {
        return estadoInscripcion == EstadoInscripcion.INSCRITO;
    }

    public boolean estaAdmitido() {
        return estadoInscripcion == EstadoInscripcion.ADMITIDO;
    }

    public boolean estaActiva() {
        return estadoInscripcion.estaActiva();
    }

    public boolean estaEvaluada() {
        return estadoInscripcion.estaEvaluada();
    }

    public boolean estaRetirada() {
        return estadoInscripcion == EstadoInscripcion.RETIRADO;
    }

    public boolean estaRechazada() {
        return estadoInscripcion == EstadoInscripcion.RECHAZADO;
    }

    // Getters
    public CursoEstudiantePk getIdCursoEstudiante() {
        return idCursoEstudiante;
    }

    public EstadoInscripcion getEstadoInscripcion() {
        return estadoInscripcion;
    }

    public Integer getCalificacionCursoEstudiante() {
        return calificacionCursoEstudiante;
    }

    public String getObservacionCursoEstudiante() {
        return observacionCursoEstudiante;
    }

    public OffsetDateTime getFechaInscripcion() {
        return fechaInscripcion;
    }

    public OffsetDateTime getFechaEvaluacion() {
        return fechaEvaluacion;
    }

    // Getters de conveniencia (Ley de Demeter)
    public UUID getIdCursoPeriodo() {
        return idCursoEstudiante.getIdCursoPeriodo();
    }

    public UUID getIdEstudiante() {
        return idCursoEstudiante.getIdEstudiante();
    }

    public CursoPeriodo getCursoPeriodo() {
        return idCursoEstudiante.getCursoPeriodo();
    }

    public Estudiante getEstudiante() {
        return idCursoEstudiante.getEstudiante();
    }
}
