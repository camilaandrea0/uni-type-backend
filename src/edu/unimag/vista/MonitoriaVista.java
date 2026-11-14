package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.ControladorMonitoria;
import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.estudiante.EstudianteDto;
import edu.unimag.dto.monitoria.MonitoriaCrearDto;
import edu.unimag.dto.monitoria.MonitoriaDto;
import edu.unimag.dto.monitoria.MonitoriaEvaluarDto;
import edu.unimag.dto.profesor.ProfesorDto;
import java.util.List;
import java.util.UUID;

public class MonitoriaVista implements SystemModule {

    private final ControladorMonitoria controlador;

    public MonitoriaVista(ControladorMonitoria controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;
    }

    @Override
    public String getModuleName() {
        return "Monitorias academicas";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        mostrarMenu(console, formatter);
    }

    private void mostrarMenu(Console console, ScreenFormatter formatter) {
        boolean ejecutar = true;
        while (ejecutar) {
            console.showMenu(
                    "MODULO DE MONITORIAS",
                    "1. Crear monitoria",
                    "2. Evaluar monitoria",
                    "3. Eliminar monitoria",
                    "4. Buscar monitoria",
                    "5. Listar todas las monitorias",
                    "6. Listar monitorias por curso periodo",
                    "7. Listar monitorias por estudiante",
                    "8. Listar monitorias por profesor",
                    "0. Regresar al menu anterior"
            );

            String opcion = console.readText("Seleccione una opcion");
            switch (opcion) {
                case "1" ->
                    crear(console, formatter);
                case "2" ->
                    evaluar(console, formatter);
                case "3" ->
                    eliminar(console, formatter);
                case "4" ->
                    buscar(console, formatter);
                case "5" ->
                    listarTodos(console, formatter);
                case "6" ->
                    listarPorCursoPeriodo(console, formatter);
                case "7" ->
                    listarPorEstudiante(console, formatter);
                case "8" ->
                    listarPorProfesor(console, formatter);
                case "0" ->
                    ejecutar = false;
                default ->
                    console.showError("Opcion incorrecta");
            }
        }
    }

    private void crear(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- CREAR MONITORIA ---");

        UUID idCursoPeriodo = leerCursoValidado(console);
        if (idCursoPeriodo == null) {
            return;
        }
        UUID idEstudiante = leerEstudianteValidado(console, formatter);
        if (idEstudiante == null) {
            return;
        }
        Long idProfesor = leerProfesorValidado(console);
        if (idProfesor == null) {
            return;
        }

        try {
            MonitoriaCrearDto request = new MonitoriaCrearDto(
                    idCursoPeriodo,
                    idEstudiante,
                    idProfesor
            );
            MonitoriaDto response = controlador.crear(request);

            console.showMessage("\nMonitoria creada correctamente.");
            mostrarDetallado(console, formatter, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void evaluar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- EVALUAR MONITORIA ---");

        UUID idCursoPeriodo = console.readUUID("ID del curso periodo");
        if (idCursoPeriodo == null) {
            return;
        }
        UUID idEstudiante = console.readUUID("ID del estudiante");
        if (idEstudiante == null) {
            return;
        }
        Long idProfesor = console.readLong("ID del profesor");
        if (idProfesor == null) {
            return;
        }

        Integer calificacion = console.readInteger("Calificacion (0 a 500)");
        if (calificacion == null) {
            return;
        }
        String comentario = console.readText("Comentario (opcional)");

        try {
            MonitoriaEvaluarDto request = new MonitoriaEvaluarDto(
                    idCursoPeriodo,
                    idEstudiante,
                    idProfesor,
                    calificacion,
                    comentario
            );
            MonitoriaDto response = controlador.evaluar(request);

            console.showMessage("\nMonitoria evaluada correctamente.");
            mostrarDetallado(console, formatter, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void eliminar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- ELIMINAR MONITORIA ---");

        UUID idCursoPeriodo = console.readUUID("ID del curso periodo");
        if (idCursoPeriodo == null) {
            return;
        }
        UUID idEstudiante = console.readUUID("ID del estudiante");
        if (idEstudiante == null) {
            return;
        }
        Long idProfesor = console.readLong("ID del profesor");
        if (idProfesor == null) {
            return;
        }

        try {
            MonitoriaDto actual = controlador.buscarPorId(
                    idCursoPeriodo,
                    idEstudiante,
                    idProfesor
            );
            mostrarDetallado(console, formatter, actual);

            if (!console.readConfirmation("Confirma que desea eliminar esta monitoria?")) {
                console.showMessage("Operacion cancelada por el usuario.");
                return;
            }

            controlador.eliminar(idCursoPeriodo, idEstudiante, idProfesor);
            console.showMessage("\nMonitoria eliminada correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void buscar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- BUSCAR MONITORIA ---");

        UUID idCursoPeriodo = console.readUUID("ID del curso periodo");
        if (idCursoPeriodo == null) {
            return;
        }
        UUID idEstudiante = console.readUUID("ID del estudiante");
        if (idEstudiante == null) {
            return;
        }
        Long idProfesor = console.readLong("ID del profesor");
        if (idProfesor == null) {
            return;
        }

        try {
            MonitoriaDto response = controlador.buscarPorId(
                    idCursoPeriodo,
                    idEstudiante,
                    idProfesor
            );
            mostrarDetallado(console, formatter, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listarTodos(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTADO DE MONITORIAS ---");
        try {
            mostrarLista(console, formatter, controlador.listarTodos());
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar monitorias: " + e.getMessage());
        }
    }

    private void listarPorCursoPeriodo(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR MONITORIAS POR CURSO PERIODO ---");
        UUID idCursoPeriodo = console.readUUID("ID del curso periodo");
        if (idCursoPeriodo == null) {
            return;
        }
        try {
            mostrarLista(console, formatter, controlador.listarPorCursoPeriodo(idCursoPeriodo));
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar monitorias: " + e.getMessage());
        }
    }

    private void listarPorEstudiante(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR MONITORIAS POR ESTUDIANTE ---");
        UUID idEstudiante = console.readUUID("ID del estudiante");
        if (idEstudiante == null) {
            return;
        }
        try {
            mostrarLista(console, formatter, controlador.listarPorEstudiante(idEstudiante));
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar monitorias: " + e.getMessage());
        }
    }

    private void listarPorProfesor(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR MONITORIAS POR PROFESOR ---");
        Long idProfesor = console.readLong("ID del profesor");
        if (idProfesor == null) {
            return;
        }
        try {
            mostrarLista(console, formatter, controlador.listarPorProfesor(idProfesor));
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar monitorias: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Apoyo
    // ------------------------------------------------------------------
    private UUID leerCursoValidado(Console console) {
        UUID idCursoPeriodo = console.readUUID("ID del curso periodo");
        if (idCursoPeriodo == null) {
            return null;
        }
        try {
            CursoPeriodoDto curso = controlador.buscarCursoPeriodo(idCursoPeriodo);
            console.showMessage("Curso: " + curso.nombre());
            console.showMessage("Materia: " + curso.materia().nombre());
            console.showMessage("Periodo: " + curso.periodo().nombre());
            return idCursoPeriodo;
        } catch (IllegalArgumentException e) {
            console.showError("No existe el curso periodo indicado.");
            return null;
        }
    }

    private UUID leerEstudianteValidado(Console console, ScreenFormatter formatter) {
        UUID idEstudiante = console.readUUID("ID del estudiante");
        if (idEstudiante == null) {
            return null;
        }
        try {
            EstudianteDto estudiante = controlador.buscarEstudiante(idEstudiante);
            console.showMessage("Estudiante: " + estudiante.nombre()
                    + " (codigo " + estudiante.codigo() + ")");
            console.showMessage("Estado: " + estudiante.estado().getDescription());
            return idEstudiante;
        } catch (IllegalArgumentException e) {
            console.showError("No existe el estudiante indicado.");
            return null;
        }
    }

    private Long leerProfesorValidado(Console console) {
        Long idProfesor = console.readLong("ID del profesor");
        if (idProfesor == null) {
            return null;
        }
        try {
            ProfesorDto profesor = controlador.buscarProfesor(idProfesor);
            console.showMessage("Profesor: " + profesor.nombre());
            console.showMessage("Estado: " + profesor.estado().getDescription());
            return idProfesor;
        } catch (IllegalArgumentException e) {
            console.showError("No existe el profesor indicado.");
            return null;
        }
    }

    private void mostrarLista(Console console, ScreenFormatter formatter, List<MonitoriaDto> lista) {
        if (lista.isEmpty()) {
            console.showMessage("No hay monitorias para mostrar.");
            return;
        }
        console.showMessage("Total de monitorias: " + lista.size());
        console.showMessage(formatter.separator());
        for (MonitoriaDto dto : lista) {
            mostrarResumen(console, formatter, dto);
            console.showMessage(formatter.separator());
        }
    }

    private void mostrarResumen(Console console, ScreenFormatter formatter, MonitoriaDto dto) {
        console.showMessage("Estudiante: " + dto.estudiante().nombre()
                + " (codigo " + dto.estudiante().codigo() + ")");
        console.showMessage("   Curso: " + dto.cursoPeriodo().nombre());
        console.showMessage("   Materia: " + dto.cursoPeriodo().materia().nombre());
        console.showMessage("   Periodo: " + dto.cursoPeriodo().periodo().nombre());
        console.showMessage("   Profesor: " + dto.profesor().nombre());
        console.showMessage("   Estado: " + dto.estado().getDescription());
        console.showMessage("   Calificacion: " + textoCalificacion(dto.calificacion()));
        console.showMessage("   Fecha creacion: " + formatter.dateTime(dto.fechaCreacion()));
    }

    private void mostrarDetallado(Console console, ScreenFormatter formatter, MonitoriaDto dto) {
        console.showMessage("   Estudiante: " + dto.estudiante().nombre()
                + " (codigo " + dto.estudiante().codigo() + ")");
        console.showMessage("   Correo: " + formatter.optionalText(dto.estudiante().correo()));
        console.showMessage("   Curso: " + dto.cursoPeriodo().nombre());
        console.showMessage("   Materia: " + dto.cursoPeriodo().materia().nombre());
        console.showMessage("   Periodo: " + dto.cursoPeriodo().periodo().nombre());
        console.showMessage("   Profesor: " + dto.profesor().nombre());
        console.showMessage("   Estado: " + dto.estado().getDescription());
        console.showMessage("   Calificacion: " + textoCalificacion(dto.calificacion()));
        console.showMessage("   Comentario: " + formatter.optionalText(dto.comentario()));
        console.showMessage("   Fecha creacion: " + formatter.dateTime(dto.fechaCreacion()));
        console.showMessage("   Fecha evaluacion: " + formatter.dateTime(dto.fechaEvaluacion()));
    }

    private String textoCalificacion(Integer calificacion) {
        return (calificacion == null) ? "Sin calificar" : String.valueOf(calificacion);
    }
}
