package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.CursoPeriodoControlador;
import edu.unimag.dto.cursoperiodo.CursoPeriodoActualizarDto;
import edu.unimag.dto.cursoperiodo.CursoPeriodoCrearDto;
import edu.unimag.dto.cursoperiodo.CursoPeriodoDto;

import java.util.List;
import java.util.UUID;

public class CursoPeriodoVista implements SystemModule {

    private final CursoPeriodoControlador controlador;

    public CursoPeriodoVista(CursoPeriodoControlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;
    }

    @Override
    public String getModuleName() {
        return "Cursos por Periodo";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        this.mostrarMenu(console, formatter);
    }

    private void mostrarMenu(Console console, ScreenFormatter formatter) {
        boolean ejecutar = true;
        while (ejecutar) {
            console.showMenu(
                    "MODULO DE CURSOS POR PERIODO",
                    "1. Registrar Curso Periodo",
                    "2. Listar Cursos Periodos",
                    "3. Actualizar Curso Periodo",
                    "4. Eliminar Curso Periodo",
                    "0. Regresar al Menu Principal"
            );

            String opcion = console.readText("Seleccione una opcion");
            switch (opcion) {
                case "1" ->
                    registrar(console);
                case "2" ->
                    listar(console, formatter);
                case "3" ->
                    actualizar(console);
                case "4" ->
                    eliminar(console);
                case "0" ->
                    ejecutar = false;
                default ->
                    console.showError("Opcion incorrecta");
            }
        }
    }

    private void registrar(Console console) {
        console.showMessage("\n--- REGISTRAR NUEVO CURSO PERIODO ---");

        String nombre = console.readText("Nombre del curso periodo (ej: Calculo I - Grupo 1)");
        if (nombre.isEmpty()) {
            return;
        }

        Integer idMateria = console.readInteger("ID de la materia");
        if (idMateria == null) {
            return;
        }

        Integer idPeriodo = console.readInteger("ID del periodo academico");
        if (idPeriodo == null) {
            return;
        }

        Integer cupo = console.readInteger("Cupo maximo del curso");
        if (cupo == null) {
            return;
        }

        try {
            CursoPeriodoCrearDto request = new CursoPeriodoCrearDto(nombre, idMateria, idPeriodo, cupo);
            CursoPeriodoDto response = controlador.registrar(request);

            console.showMessage("\nCurso periodo registrado correctamente.");
            console.showMessage("   ID: " + response.id());
            console.showMessage("   Nombre: " + response.nombre());
            console.showMessage("   Materia: " + response.materia().nombre());
            console.showMessage("   Periodo: " + response.periodo().nombre());
            console.showMessage("   Cupo: " + response.cupo());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTADO DE CURSOS PERIODOS ---");

        try {
            List<CursoPeriodoDto> lista = controlador.listarTodos();

            if (lista.isEmpty()) {
                console.showMessage("No hay cursos periodos registrados en el sistema.");
                return;
            }

            console.showMessage("Total de cursos periodos: " + lista.size());
            console.showMessage(formatter.separator());

            for (CursoPeriodoDto c : lista) {
                console.showMessage("ID: " + c.id());
                console.showMessage("   Nombre: " + c.nombre());
                console.showMessage("   Materia: " + c.materia().nombre() + " (ID: " + c.materia().id() + ")");
                console.showMessage("   Periodo: " + c.periodo().nombre() + " (ID: " + c.periodo().id() + ")");
                console.showMessage("   Cupo: " + c.cupo());
                console.showMessage("   Fecha Creacion: " + formatter.dateTime(c.fechaCreacion()));
                console.showMessage(formatter.separator());
            }

        } catch (RuntimeException e) {
            console.showError("Error al listar cursos periodos: " + e.getMessage());
        }
    }

    private void actualizar(Console console) {
        console.showMessage("\n--- ACTUALIZAR CURSO PERIODO ---");

        UUID id = console.readUUID("ID del curso periodo a actualizar");
        if (id == null) {
            return;
        }
        try {
            String nuevoNombre = console.readText("Nuevo nombre ([Enter] para no cambiar)");
            if (nuevoNombre.isBlank()) {
                nuevoNombre = null;
            }

            Integer nuevoCupo = console.readInteger("Nuevo cupo maximo ([Enter] para no cambiar)");

            if (nuevoNombre == null && nuevoCupo == null) {
                console.showMessage("No se proporcionaron datos para actualizar. Operacion cancelada.");
                return;
            }

            CursoPeriodoActualizarDto request = new CursoPeriodoActualizarDto(id, nuevoNombre, nuevoCupo);
            CursoPeriodoDto response = controlador.actualizar(request);

            console.showMessage("\nCurso periodo actualizado correctamente.");
            console.showMessage("   Nombre actual: " + response.nombre());
            console.showMessage("   Cupo actual: " + response.cupo());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void eliminar(Console console) {
        console.showMessage("\n--- ELIMINAR CURSO PERIODO ---");
        console.showMessage("ADVERTENCIA: Esta accion intentara borrar fisicamente el registro.");

        UUID id = console.readUUID("ID del curso periodo a eliminar");
        if (id == null) {
            return;
        }
        try {
            if (!console.readConfirmation("Seguro que desea eliminar este curso periodo?")) {
                console.showMessage("Operacion cancelada por el usuario.");
                return;
            }

            boolean eliminado = controlador.eliminar(id);

            if (eliminado) {
                console.showMessage("\nCurso periodo eliminado del sistema correctamente.");
            } else {
                console.showMessage("\nNo se pudo eliminar el curso periodo (puede que ya no exista).");
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("PROTECCION DE INTEGRIDAD: " + e.getMessage());
        }
    }
}
