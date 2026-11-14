package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaConvert;
import com.cleandev.tpa.api.annotations.TpaId;
import edu.unimag.modelo.constante.PoliticasAcademicas;
import edu.unimag.modelo.convertidor.EstadoMonitoriaConverter;
import edu.unimag.modelo.enumeracion.EstadoMonitoria;
import edu.unimag.modelo.pk.MonitoriaPk;
import java.time.OffsetDateTime;
import java.util.UUID;

public class Monitoria {

    @TpaId
    private MonitoriaPk idMonitoria;

    private Integer calificacionMonitoria;
    private String comentarioMonitoria;

    @TpaConvert(converter = EstadoMonitoriaConverter.class)
    private EstadoMonitoria estadoMonitoria;

    private OffsetDateTime fechaCreacionMonitoria;
    private OffsetDateTime fechaEvaluacionMonitoria;

    // Reflexion
    protected Monitoria() {
    }

    // Creacion
    public Monitoria(MonitoriaPk pk) {
        if (pk == null) {
            throw new IllegalArgumentException("La clave de la monitoria es obligatoria");
        }
        this.idMonitoria = pk;
        this.estadoMonitoria = EstadoMonitoria.PENDIENTE;
        this.calificacionMonitoria = null;
        this.comentarioMonitoria = null;
        this.fechaCreacionMonitoria = OffsetDateTime.now();
        this.fechaEvaluacionMonitoria = null;
    }

    // Hidratacion
    public Monitoria(
            MonitoriaPk pk,
            Integer calificacion,
            String comentario,
            EstadoMonitoria estado,
            OffsetDateTime fechaCreacion,
            OffsetDateTime fechaEvaluacion
    ) {
        if (pk == null) {
            throw new IllegalArgumentException("La clave de la monitoria es obligatoria en hidratacion");
        }
        this.idMonitoria = pk;
        this.calificacionMonitoria = calificacion;
        this.comentarioMonitoria = comentario;
        this.estadoMonitoria = (estado != null) ? estado : EstadoMonitoria.PENDIENTE;
        this.fechaCreacionMonitoria = (fechaCreacion != null) ? fechaCreacion : OffsetDateTime.now();
        this.fechaEvaluacionMonitoria = fechaEvaluacion;
    }

    // Metodos de comportamiento
    public void evaluar(Integer calificacion, String comentario) {
        EstadoMonitoria nuevoEstado = EstadoMonitoria.desdeCalificacion(calificacion);
        this.estadoMonitoria = this.estadoMonitoria.cambiarEstadoA(nuevoEstado);

        String comentarioLimpio = (comentario == null || comentario.isBlank())
                ? null
                : comentario.trim();

        if (this.calificacionMonitoria != null) {
            String fechaFormateada = OffsetDateTime.now().format(PoliticasAcademicas.FORMATO_AUDITORIA);
            String detalle = (comentarioLimpio != null) ? comentarioLimpio : "Sin comentario";
            String trazaAuditoria = String.format(
                    " | REEVALUACION [%s]: %s (Nota anterior: %d)",
                    fechaFormateada, detalle, this.calificacionMonitoria
            );
            String base = (this.comentarioMonitoria == null) ? "" : this.comentarioMonitoria;
            this.comentarioMonitoria = base + trazaAuditoria;
        } else {
            this.comentarioMonitoria = comentarioLimpio;
        }

        this.calificacionMonitoria = calificacion;
        this.fechaEvaluacionMonitoria = OffsetDateTime.now();
    }

    // Metodos de consulta
    public boolean estaPendiente() {
        return this.estadoMonitoria == EstadoMonitoria.PENDIENTE;
    }

    public boolean estaEvaluada() {
        return this.estadoMonitoria != EstadoMonitoria.PENDIENTE;
    }

    public boolean estaAprobada() {
        return this.estadoMonitoria == EstadoMonitoria.APROBADO;
    }

    public boolean estaReprobada() {
        return this.estadoMonitoria == EstadoMonitoria.REPROBADO;
    }

    // Getters
    public MonitoriaPk getIdMonitoria() {
        return idMonitoria;
    }

    public Integer getCalificacionMonitoria() {
        return calificacionMonitoria;
    }

    public String getComentarioMonitoria() {
        return comentarioMonitoria;
    }

    public EstadoMonitoria getEstadoMonitoria() {
        return estadoMonitoria;
    }

    public OffsetDateTime getFechaCreacionMonitoria() {
        return fechaCreacionMonitoria;
    }

    public OffsetDateTime getFechaEvaluacionMonitoria() {
        return fechaEvaluacionMonitoria;
    }

    // Getters de conveniencia (Ley de Demeter)
    public UUID getIdCursoPeriodo() {
        return this.idMonitoria.getIdCursoPeriodo();
    }

    public UUID getIdEstudiante() {
        return this.idMonitoria.getIdEstudiante();
    }

    public Long getIdProfesor() {
        return this.idMonitoria.getIdProfesor();
    }

    public CursoPeriodo getCursoPeriodo() {
        return this.idMonitoria.getCursoPeriodo();
    }

    public Estudiante getEstudiante() {
        return this.idMonitoria.getEstudiante();
    }

    public Profesor getProfesor() {
        return this.idMonitoria.getProfesor();
    }
}
