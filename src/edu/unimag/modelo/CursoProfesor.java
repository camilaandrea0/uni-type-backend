package edu.unimag.modelo;

import com.cleandev.tpa.api.annotations.TpaConvert;
import com.cleandev.tpa.api.annotations.TpaId;
import edu.unimag.modelo.convertidor.EstadoProfesorCursoCoverter;
import edu.unimag.modelo.convertidor.TipoAsignacionConverter;
import edu.unimag.modelo.enumeracion.EstadoProfesorCurso;
import edu.unimag.modelo.enumeracion.TipoAsignacion;
import edu.unimag.modelo.pk.CursoProfesorPk;
import java.time.OffsetDateTime;
import java.util.UUID;

public class CursoProfesor {

    @TpaId
    private CursoProfesorPk idCursoProfesorPeriodo;
    private OffsetDateTime fechaAsisgnacion;
    @TpaConvert(converter = TipoAsignacionConverter.class)
    private TipoAsignacion tipoAsignacion;
    @TpaConvert(converter = EstadoProfesorCursoCoverter.class)
    private EstadoProfesorCurso estado;

    protected CursoProfesor() {
    }

    public CursoProfesor(CursoProfesorPk idCursoProfesorPeriodo, TipoAsignacion tipoAsignacion) {
        this.idCursoProfesorPeriodo = idCursoProfesorPeriodo;
        this.fechaAsisgnacion= OffsetDateTime.now();
        this.tipoAsignacion = tipoAsignacion;
        this.estado= EstadoProfesorCurso.ACTIVO;
    }

    public CursoProfesor(CursoProfesorPk idCursoProfesorPeriodo, OffsetDateTime fechaAsisgnacion, TipoAsignacion tipoAsignacion, EstadoProfesorCurso estado) {
        this.idCursoProfesorPeriodo = idCursoProfesorPeriodo;
        this.fechaAsisgnacion = (fechaAsisgnacion!=null)?fechaAsisgnacion:OffsetDateTime.now();
        this.tipoAsignacion = tipoAsignacion;
        this.estado = (estado==null)?EstadoProfesorCurso.ACTIVO: estado;
    }
    
    public void actualizarTipoAsig(TipoAsignacion nuevo){
        if (nuevo==tipoAsignacion) {
            throw new IllegalArgumentException("El tipo es diferente");
        }
        this.tipoAsignacion=nuevo;
    }
    public void retirar(){
        this.estado= estado.cambiarEstado(EstadoProfesorCurso.RETIRADO);
    }
    public void reactivar(){
        this.estado= estado.cambiarEstado(EstadoProfesorCurso.ACTIVO);
    }
    public boolean estaActivo(){
        return estado==EstadoProfesorCurso.ACTIVO;
    }

    public CursoProfesorPk getIdCursoProfesorPeriodo() {
        return idCursoProfesorPeriodo;
    }

    public OffsetDateTime getFechaAsisgnacion() {
        return fechaAsisgnacion;
    }

    public TipoAsignacion getTipoAsignacion() {
        return tipoAsignacion;
    }

    public EstadoProfesorCurso getEstado() {
        return estado;
    }
    
    public Long getIdProfesor(){
        return idCursoProfesorPeriodo.getIdProfesor();
    }
    public UUID getIdCurso(){
        return idCursoProfesorPeriodo.getIdCusrso();
    }
    public Profesor getProfesor(){
        return idCursoProfesorPeriodo.getProfesor();
    }
    public CursoPeriodo getCursoPeriodo(){
        return idCursoProfesorPeriodo.getCursoPeriodo();
    }
}
