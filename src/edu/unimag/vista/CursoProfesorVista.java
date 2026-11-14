package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.ControladorCursoProfesor;
import edu.unimag.dto.cursoPorfesor.cursoProfesorActualizar;
import edu.unimag.dto.cursoPorfesor.cursoProfesorCrearDto;
import edu.unimag.dto.cursoPorfesor.cursoProfesorDto;
import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.profesor.ProfesorDto;
import edu.unimag.modelo.enumeracion.TipoAsignacion;
import java.util.List;
import java.util.UUID;

public class CursoProfesorVista implements SystemModule{
    private final ControladorCursoProfesor controlador;

    public CursoProfesorVista(ControladorCursoProfesor controlador) {
        if (controlador==null) {
            throw  new IllegalArgumentException("El controlador es nulo");
        }
        this.controlador = controlador;
    }

    @Override
    public String getModuleName() {
        return "Asignacion de profesores a cursos";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        mostrarMenuVista(console,formatter,renderer);
    }

    private void mostrarMenuVista(Console console, ScreenFormatter formatter, TableRenderer renderer) {
     boolean ejecutar = true;
        while (ejecutar) {
            console.showMenu("MODULO ASIGNAR PROFESORES A CURSOS \n",
                    "1. Asignar Profesor a curso \n",
                    "2. Listar cursos por profe \n",
                    "3. Listar Profe por materia\n",
                    "4. Listar Profesores por materia y periodo\n",
                    "5. Listar Profesores por periodo \n",
                    "6. Cambiar asignacion de profesores en curso\n",
                    "7. Retirar un profesor (no se borra)\n",
                    "8. Reactivar un profesor(por esto es que no se borra)\n",
                    "0. Regresar al menu anterior");
            String opcion= console.readText("Digite su opcion");
            switch (opcion) {
                case "1" -> AsignarProfe(console,formatter, renderer);
                case "2" -> listarPorProfe(console, formatter, renderer);
                case "3" -> listarProfePorMateria(console, formatter, renderer);
                case "4" -> listarProfePorMateriaYPeriodo(console, formatter, renderer);
                case "5" -> listarProfesporPeriodo(console, formatter,renderer);
                case "6" -> cambiarAsignacion(console, formatter, renderer);
                case "7" -> retirarUnProfesor(console, formatter, renderer);
                case "8" -> reactivarUnProfesor(console,formatter, renderer);
                case "0" -> ejecutar= false;
                default -> console.showError("Opcion INVALIDA");
            }
        }
    }
    
    private TipoAsignacion seleccionarTipoAsignaccion(Console console ){
         console.showMessage("\n Tipos de asignacion de profesores disponible");
         for (TipoAsignacion tipo :TipoAsignacion.values()) {
            console.showMessage("  "+ tipo.getCode()+"-"+ tipo.getDescription());
        }
        while (true) {            
            Integer codigo=console.readInteger("Seleccione el tipo de profe");
            if (codigo==null||codigo==0) {
                return null;
            }
            for (TipoAsignacion tipo : TipoAsignacion.values()) {
                if (tipo.getCode()== codigo) {
                    return tipo;
                }
            }
            console.showError("Codigo de asignacion INCORRECTO");
            
        }
    }
    private void mostrarAsignacionResumida(Console console, ScreenFormatter formatter, cursoProfesorDto dto){
        console.showMessage("ID profe"+dto.profesor().id());
        console.showMessage("Nombre Profesor: "+ dto.profesor().nombre());
        console.showMessage("Curso Periodo identificador: "+ dto.cursoPeriodo().id());
        console.showMessage("Materia: "+ dto.cursoPeriodo().materia().nombre());
        console.showMessage("Periodo: " + dto.cursoPeriodo().periodo().nombre());
        console.showMessage("Asignacion: "+ dto.tipoAsignacion().getDescription());
        console.showMessage("Estado: " + dto.estado().getDescription());
        console.showMessage("Fecha de asignacion: "+ formatter.dateTime(dto.fechaasignacion()));
    }
    
    private void mostrarAsignacionDetallada(Console console, ScreenFormatter formatter, cursoProfesorDto dto){
        console.showMessage("ID profe"+dto.profesor().id());
        console.showMessage("Nombre Profesor: "+ dto.profesor().nombre());
        console.showMessage("Celular: "+formatter.optionalText(dto.profesor().celular()));
        console.showMessage("Curso Periodo identificador: "+ dto.cursoPeriodo().id());
        console.showMessage("Materia: "+ dto.cursoPeriodo().materia().nombre());
        console.showMessage("Estado Materia: "+ dto.cursoPeriodo().materia().estado().getDescription());
        console.showMessage("Periodo: " + dto.cursoPeriodo().periodo().nombre());
        console.showMessage("Asignacion: "+ dto.tipoAsignacion().getDescription());
        console.showMessage("Estado: " + dto.estado().getDescription());
        console.showMessage("Fecha de asignacion: "+ formatter.dateTime(dto.fechaasignacion()));
    }

    private void AsignarProfe(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("");
        Long idProfesor= console.readLong("Digite el id del profe");
        if (idProfesor==null) {
            return;
        }
        ProfesorDto profesor;
        try {
         profesor= controlador.buscarProfesor(idProfesor);   
        } catch (IllegalArgumentException e) {
            console.showError("No existe dicho profesor");
            return;
        }
        console.showMessage("\nProfesor seleccionado");
        console.showMessage("Nombre :"+ profesor.nombre());
        
        UUID idCursoPeriodo= console.readUUID("Ingrese el del curso periodo");
        if (idCursoPeriodo==null) {
            return;
        }
        CursoPeriodoDto cursoPeriodo;
        try {
            cursoPeriodo= controlador.buscarCursoPeriodo(idCursoPeriodo);
        } catch (IllegalArgumentException e) {
            console.showError("No existe el curso periodo");
            return;
        }
        console.showMessage("\nDatos del curso periodo");
        console.showMessage("   Nombre :"+ cursoPeriodo.nombre());
        console.showMessage("   Materia:"+ cursoPeriodo.materia().nombre());
        console.showMessage("   Periodo:"+ cursoPeriodo.periodo().nombre());
        TipoAsignacion asignacion=seleccionarTipoAsignaccion(console);
        if (asignacion==null) {
            return;
        }
        try {
            cursoProfesorCrearDto request=new cursoProfesorCrearDto(idCursoPeriodo, idProfesor, asignacion);
            cursoProfesorDto response= controlador.asignar(request);
            
            console.showMessage("Se asigno el profesor correctamente");
            mostrarAsignacionDetallada(console, formatter, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error en la validacion");
        }catch(RuntimeException e){
            console.showError("Error en el tiempo de ejecucion");
        }
    }

    private void listarPorProfe(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("Vamos a Listar los cursos por profesores ");
        Long idProfesor= console.readLong("Digite la id del profesor a listar");
        if (idProfesor==null) {
            return;
        }
        try {
            List<cursoProfesorDto> lista=controlador.listarPorProfesor(idProfesor);
            if (lista.isEmpty()) {
                console.showMessage("Ese profe no tiene curso en ningun periodo");
                return;
            }
            console.showMessage("Profesor: "+lista.get(0).profesor().nombre());
            console.showMessage("Cantidad de asignaciones: "+ lista.size());
            console.showMessage(formatter.separator());
            
            for (cursoProfesorDto profesorDto : lista) {
                
                mostrarAsignacionResumida(console, formatter, profesorDto);
                console.showMessage(formatter.separator());
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error en la validacion");
        }catch(RuntimeException e){
            console.showError("Error en el tiempo de ejecucion");
        }
    }

    private void listarProfePorMateria(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("Vamos a Listar los profesores por Materia");
        Integer idMateria= console.readInteger("Digite la id de la materia");
        if (idMateria==null) {
            return;
        }
        try {
            List<cursoProfesorDto> lista=controlador.listarPorMateria(idMateria);
            if (lista.isEmpty()) {
                console.showMessage("Esta materia no tiene cursos en ningun periodo");
                return;
            }
            console.showMessage("Materia: "+lista.get(0).cursoPeriodo().materia().nombre());
            console.showMessage("Cantidad de asignaciones: "+ lista.size());
            console.showMessage(formatter.separator());
            
            for (cursoProfesorDto dto : lista) {
                
                mostrarAsignacionResumida(console, formatter, dto);
                console.showMessage(formatter.separator());
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error en la validacion");
        }catch(RuntimeException e){
            console.showError("Error en el tiempo de ejecucion");
        }
    }

    private void listarProfePorMateriaYPeriodo(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("Vamos a Listar los cursos por Materia y periodo");
        Integer idMateria= console.readInteger("Digite la id de la materia");
        if (idMateria==null) {
            return;
        }
        Integer idPeriodo= console.readInteger("Digite la id del periodo academico");
        if (idPeriodo==null) {
            return;
        }
        try {
            List<cursoProfesorDto> lista=controlador.listarPorMateriaYperiodo(idMateria, idPeriodo);
            if (lista.isEmpty()) {
                console.showMessage("Esta materia no tiene cursos en este periodo");
                return;
            }
            console.showMessage("Materia: "+lista.get(0).cursoPeriodo().materia().nombre());
            console.showMessage("Periodo: "+lista.get(0).cursoPeriodo().periodo().nombre());
            console.showMessage("Cantidad de asignaciones: "+ lista.size());
            console.showMessage(formatter.separator());
            
            for (cursoProfesorDto dto : lista) {
                
                mostrarAsignacionResumida(console, formatter, dto);
                console.showMessage(formatter.separator());
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error en la validacion");
        }catch(RuntimeException e){
            console.showError("Error en el tiempo de ejecucion");
        }
    }

    private void listarProfesporPeriodo(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("Vamos a Listar los profesores por periodo");
        Integer idPeriodo= console.readInteger("Digite la id del periodo academico");
        if (idPeriodo==null) {
            return;
        }
        try {
            List<cursoProfesorDto> lista=controlador.listarPorPeriodoAcademico(idPeriodo);
            if (lista.isEmpty()) {
                console.showMessage("Este periodo no tiene cursos");
                return;
            }
            console.showMessage("Periodo: "+lista.get(0).cursoPeriodo().periodo().nombre());
            console.showMessage("Cantidad de asignaciones: "+ lista.size());
            console.showMessage(formatter.separator());
            
            for (cursoProfesorDto dto : lista) {
                
                mostrarAsignacionResumida(console, formatter, dto);
                console.showMessage(formatter.separator());
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error en la validacion");
        }catch(RuntimeException e){
            console.showError("Error en el tiempo de ejecucion");
        }
    }

    private void cambiarAsignacion(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("Cambiar la Asignacion de un profesor");
        Long idProfesor= console.readLong("Ingrese el codigo del profesor");
        if (idProfesor==null) {
            return;
        }
        UUID idCursoPeriodo= console.readUUID("Ingrese la id ");
        if (idCursoPeriodo==null) {
            return;
        }
        try {
          List<cursoProfesorDto> list= controlador.listarPorProfesor(idProfesor);
          cursoProfesorDto asignacionActual=null;
            for (cursoProfesorDto dto : list) {
                if (dto.cursoPeriodo().id().equals(idCursoPeriodo)) {
                    asignacionActual=dto;
                }
            }
            if (asignacionActual==null) {
                console.showError("No hay asignaciones para ese profesor");
                return;
            }
            console.showMessage("\n datos de la asignacion");
            mostrarAsignacionDetallada(console, formatter, asignacionActual);
            TipoAsignacion nuevaAsignacion= seleccionarTipoAsignaccion(console);
            if (nuevaAsignacion==null) {
                return;
            }
            cursoProfesorActualizar asignacion= new cursoProfesorActualizar(idCursoPeriodo, idProfesor, nuevaAsignacion);
            cursoProfesorDto response= controlador.cambiarTipoAsignacion(asignacion);
            console.showMessage("Actualizado correctamente");
            mostrarAsignacionDetallada(console, formatter, response);
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error en la validacion");
        }catch(RuntimeException e){
            console.showError("Error en el tiempo de ejecucion");
        }
        
    }

    private void retirarUnProfesor(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("\n Retirar un curso y profesor \n");
        Long idProfesor= console.readLong("Ingrese el codigo del profesor");
        if (idProfesor==null) {
            return;
        }
        UUID idCursoPeriodo= console.readUUID("Ingrese la id ");
        if (idCursoPeriodo==null) {
            return;
        }
        try {
            if (console.readConfirmation("Esta seguro que quiere desactivar el curso del profesor ?")) {
                cursoProfesorDto response= controlador.retirar(idCursoPeriodo, idProfesor);
                if (response==null) {
                    console.showError("El profesor no estaba asignado al curso");
                    return;
                }
                console.showMessage("\n Curso retirado correctamente \n");
                mostrarAsignacionDetallada(console, formatter, response);
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error en la validacion");
        }catch(RuntimeException e){
            console.showError("Error en el tiempo de ejecucion");
        }
    }

    private void reactivarUnProfesor(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("\n Reactivar un curso y profesor \n");
        Long idProfesor= console.readLong("Ingrese el codigo del profesor");
        if (idProfesor==null) {
            return;
        }
        UUID idCursoPeriodo= console.readUUID("Ingrese la id ");
        if (idCursoPeriodo==null) {
            return;
        }
        try {
            
                cursoProfesorDto response= controlador.reactivar(idCursoPeriodo, idProfesor);
                if (response==null) {
                    console.showError("El profesor no estaba asignado al curso");
                    return;
                }
                console.showMessage("\n Curso reactivado correctamente \n");
                mostrarAsignacionDetallada(console, formatter, response);
            
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error en la validacion");
        }catch(RuntimeException e){
            console.showError("Error en el tiempo de ejecucion");
        }
    }
}
