package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.ControladorCursoEstudiante;
import edu.unimag.dto.cursoestudiante.CursoEstudianteCrearDto;
import edu.unimag.dto.cursoestudiante.CursoEstudianteDto;
import edu.unimag.dto.cursoestudiante.CursoEstudianteEvaluarDto;
import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;
import edu.unimag.dto.estudiante.EstudianteDto;
import java.util.List;
import java.util.UUID;

public class CursoEstudianteVista implements SystemModule {

    private final ControladorCursoEstudiante controlador;

    public CursoEstudianteVista(ControladorCursoEstudiante controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;
    }

    @Override
    public String getModuleName() {
        return "Inscripcion de estudiantes a cursos";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        mostrarMenu(console, formatter);
    }

    private void mostrarMenu(Console console, ScreenFormatter formatter) {
        boolean ejecutar = true;
        while (ejecutar) {
            console.showMenu(
                    "MODULO DE INSCRIPCIONES DE ESTUDIANTES",
                    "1. Inscribir estudiante en un curso",
                    "2. Admitir una inscripcion",
                    "3. Rechazar una inscripcion",
                    "4. Retirar una inscripcion",
                    "5. Evaluar una inscripcion (aprobar/reprobar)",
                    "6. Buscar inscripcion",
                    "7. Listar todas las inscripciones",
                    "8. Listar cursos por estudiante",
                    "9. Listar estudiantes por curso periodo",
                    "10. Listar inscripciones por materia",
                    "11. Listar inscripciones por materia y periodo",
                    "12. Listar inscripciones por periodo",
                    "0. Regresar al menu anterior"
            );

            String opcion = console.readText("Seleccione una opcion");
            switch (opcion) {
                case "1" ->
                    inscribir(console, formatter);
                case "2" ->
                    admitir(console, formatter);
                case "3" ->
                    rechazar(console, formatter);
                case "4" ->
                    retirar(console, formatter);
                case "5" ->
                    evaluar(console, formatter);
                case "6" ->
                    buscar(console, formatter);
                case "7" ->
                    listarTodos(console, formatter);
                case "8" ->
                    listarPorEstudiante(console, formatter);
                case "9" ->
                    listarPorCursoPeriodo(console, formatter);
                case "10" ->
                    listarPorMateria(console, formatter);
                case "11" ->
                    listarPorMateriaYPeriodo(console, formatter);
                case "12" ->
                    listarPorPeriodo(console, formatter);
                case "0" ->
                    ejecutar = false;
                default ->
                    console.showError("Opcion incorrecta");
            }
        }
    }

    private void inscribir(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- INSCRIBIR ESTUDIANTE EN UN CURSO ---");

        UUID idEstudiante = leerEstudianteValidado(console, formatter);
        if (idEstudiante == null) {
            return;
        }

        UUID idCursoPeriodo = leerCursoValidado(console, formatter);
        if (idCursoPeriodo == null) {
            return;
        }

        try {
            CursoEstudianteCrearDto request = new CursoEstudianteCrearDto(idEstudiante, idCursoPeriodo);
            CursoEstudianteDto response = controlador.inscribir(request);

            console.showMessage("\nInscripcion registrada correctamente.");
            mostrarDetallado(console, formatter, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void admitir(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- ADMITIR UNA INSCRIPCION ---");
        ejecutarCambioEstado(console, formatter, "admitir");
    }

    private void rechazar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- RECHAZAR UNA INSCRIPCION ---");
        ejecutarCambioEstado(console, formatter, "rechazar");
    }

    private void retirar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- RETIRAR UNA INSCRIPCION ---");
        ejecutarCambioEstado(console, formatter, "retirar");
    }

    private void ejecutarCambioEstado(Console console, ScreenFormatter formatter, String accion) {
        UUID idEstudiante = console.readUUID("ID del estudiante");
        if (idEstudiante == null) {
            return;
        }
        UUID idCursoPeriodo = console.readUUID("ID del curso periodo");
        if (idCursoPeriodo == null) {
            return;
        }

        try {
            CursoEstudianteDto response = switch (accion) {
                case "admitir" ->
                    controlador.admitir(idEstudiante, idCursoPeriodo);
                case "rechazar" ->
                    controlador.rechazar(idEstudiante, idCursoPeriodo);
                default ->
                    controlador.retirar(idEstudiante, idCursoPeriodo);
            };

            console.showMessage("\nInscripcion actualizada correctamente.");
            mostrarDetallado(console, formatter, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void evaluar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- EVALUAR UNA INSCRIPCION ---");

        UUID idEstudiante = console.readUUID("ID del estudiante");
        if (idEstudiante == null) {
            return;
        }
        UUID idCursoPeriodo = console.readUUID("ID del curso periodo");
        if (idCursoPeriodo == null) {
            return;
        }

        Integer calificacion = console.readInteger("Calificacion (0 a 500)");
        if (calificacion == null) {
            return;
        }
        String observacion = console.readText("Observacion (opcional)");

        try {
            CursoEstudianteEvaluarDto request = new CursoEstudianteEvaluarDto(
                    idEstudiante,
                    idCursoPeriodo,
                    calificacion,
                    observacion
            );
            CursoEstudianteDto response = controlador.evaluar(request);

            console.showMessage("\nInscripcion evaluada correctamente.");
            mostrarDetallado(console, formatter, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void buscar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- BUSCAR INSCRIPCION ---");

        UUID idEstudiante = console.readUUID("ID del estudiante");
        if (idEstudiante == null) {
            return;
        }
        UUID idCursoPeriodo = console.readUUID("ID del curso periodo");
        if (idCursoPeriodo == null) {
            return;
        }

        try {
            CursoEstudianteDto response = controlador.buscarPorId(idEstudiante, idCursoPeriodo);
            mostrarDetallado(console, formatter, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listarTodos(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTADO DE INSCRIPCIONES ---");
        try {
            mostrarLista(console, formatter, controlador.listarTodos());
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar inscripciones: " + e.getMessage());
        }
    }

    private void listarPorEstudiante(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR CURSOS POR ESTUDIANTE ---");
        UUID idEstudiante = console.readUUID("ID del estudiante");
        if (idEstudiante == null) {
            return;
        }
        try {
            mostrarLista(console, formatter, controlador.listarPorEstudiante(idEstudiante));
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar inscripciones: " + e.getMessage());
        }
    }

    private void listarPorCursoPeriodo(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR ESTUDIANTES POR CURSO PERIODO ---");
        UUID idCursoPeriodo = console.readUUID("ID del curso periodo");
        if (idCursoPeriodo == null) {
            return;
        }
        try {
            mostrarLista(console, formatter, controlador.listarPorCursoPeriodo(idCursoPeriodo));
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar inscripciones: " + e.getMessage());
        }
    }

    private void listarPorMateria(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR INSCRIPCIONES POR MATERIA ---");
        Integer idMateria = console.readInteger("ID de la materia");
        if (idMateria == null) {
            return;
        }
        try {
            mostrarLista(console, formatter, controlador.listarPorMateria(idMateria));
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar inscripciones: " + e.getMessage());
        }
    }

    private void listarPorMateriaYPeriodo(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR INSCRIPCIONES POR MATERIA Y PERIODO ---");
        Integer idMateria = console.readInteger("ID de la materia");
        if (idMateria == null) {
            return;
        }
        Integer idPeriodo = console.readInteger("ID del periodo academico");
        if (idPeriodo == null) {
            return;
        }
        try {
            mostrarLista(console, formatter, controlador.listarPorMateriaYPeriodo(idMateria, idPeriodo));
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar inscripciones: " + e.getMessage());
        }
    }

    private void listarPorPeriodo(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR INSCRIPCIONES POR PERIODO ---");
        Integer idPeriodo = console.readInteger("ID del periodo academico");
        if (idPeriodo == null) {
            return;
        }
        try {
            mostrarLista(console, formatter, controlador.listarPorPeriodo(idPeriodo));
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar inscripciones: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Apoyo
    // ------------------------------------------------------------------
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

    private UUID leerCursoValidado(Console console, ScreenFormatter formatter) {
        UUID idCursoPeriodo = console.readUUID("ID del curso periodo");
        if (idCursoPeriodo == null) {
            return null;
        }
        try {
            CursoPeriodoDto curso = controlador.buscarCursoPeriodo(idCursoPeriodo);
            console.showMessage("Curso: " + curso.nombre());
            console.showMessage("Materia: " + curso.materia().nombre());
            console.showMessage("Periodo: " + curso.periodo().nombre());
            console.showMessage("Cupo: " + curso.cupo());
            return idCursoPeriodo;
        } catch (IllegalArgumentException e) {
            console.showError("No existe el curso periodo indicado.");
            return null;
        }
    }

    private void mostrarLista(Console console, ScreenFormatter formatter, List<CursoEstudianteDto> lista) {
        if (lista.isEmpty()) {
            console.showMessage("No hay inscripciones para mostrar.");
            return;
        }
        console.showMessage("Total de inscripciones: " + lista.size());
        console.showMessage(formatter.separator());
        for (CursoEstudianteDto dto : lista) {
            mostrarResumen(console, formatter, dto);
            console.showMessage(formatter.separator());
        }
    }

    private void mostrarResumen(Console console, ScreenFormatter formatter, CursoEstudianteDto dto) {
        console.showMessage("Estudiante: " + dto.estudiante().nombre()
                + " (codigo " + dto.estudiante().codigo() + ")");
        console.showMessage("   Curso: " + dto.cursoPeriodo().nombre());
        console.showMessage("   Materia: " + dto.cursoPeriodo().materia().nombre());
        console.showMessage("   Periodo: " + dto.cursoPeriodo().periodo().nombre());
        console.showMessage("   Estado: " + dto.estado().getDescription());
        console.showMessage("   Calificacion: " + textoCalificacion(dto.calificacion()));
        console.showMessage("   Fecha inscripcion: " + formatter.dateTime(dto.fechaInscripcion()));
        console.showMessage("   Fecha evaluacion: " + formatter.dateTime(dto.fechaEvaluacion()));
    }

    private void mostrarDetallado(Console console, ScreenFormatter formatter, CursoEstudianteDto dto) {
        console.showMessage("   Estudiante: " + dto.estudiante().nombre()
                + " (codigo " + dto.estudiante().codigo() + ")");
        console.showMessage("   Correo: " + formatter.optionalText(dto.estudiante().correo()));
        console.showMessage("   Curso: " + dto.cursoPeriodo().nombre());
        console.showMessage("   Materia: " + dto.cursoPeriodo().materia().nombre());
        console.showMessage("   Periodo: " + dto.cursoPeriodo().periodo().nombre());
        console.showMessage("   Cupo del curso: " + dto.cursoPeriodo().cupo());
        console.showMessage("   Estado: " + dto.estado().getDescription());
        console.showMessage("   Calificacion: " + textoCalificacion(dto.calificacion()));
        console.showMessage("   Observacion: " + formatter.optionalText(dto.observacion()));
        console.showMessage("   Fecha inscripcion: " + formatter.dateTime(dto.fechaInscripcion()));
        console.showMessage("   Fecha evaluacion: " + formatter.dateTime(dto.fechaEvaluacion()));
    }

    private String textoCalificacion(Integer calificacion) {
        return (calificacion == null) ? "Sin calificar" : String.valueOf(calificacion);
    }
}
