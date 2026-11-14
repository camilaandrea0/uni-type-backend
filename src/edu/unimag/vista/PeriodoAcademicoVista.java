package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.PeriodoAcademicoControlador;
import edu.unimag.dto.periodoacademico.PeriodoAcademicoActualizarDto;
import edu.unimag.dto.periodoacademico.PeriodoAcademicoCrearDto;
import edu.unimag.dto.periodoacademico.PeriodoAcademicoDto;

import java.util.List;

public class PeriodoAcademicoVista implements SystemModule {

    private final PeriodoAcademicoControlador controlador;

    public PeriodoAcademicoVista(PeriodoAcademicoControlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;
    }

    @Override
    public String getModuleName() {
        return "Periodos Academicos";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        this.mostrarMenu(console, formatter);
    }

    private void mostrarMenu(Console console, ScreenFormatter formatter) {
        boolean ejecutar = true;
        while (ejecutar) {
            console.showMenu(
                    "MODULO DE PERIODOS ACADEMICOS",
                    "1. Registrar Periodo",
                    "2. Listar Periodos",
                    "3. Actualizar Nombre",
                    "4. Suspender Periodo",
                    "5. Reactivar Periodo",
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
                    suspender(console);
                case "5" ->
                    reactivar(console);
                case "0" ->
                    ejecutar = false;
                default ->
                    console.showError("Opcion incorrecta");
            }
        }
    }

    private void registrar(Console console) {
        console.showMessage("\n--- REGISTRAR NUEVO PERIODO ACADEMICO ---");

        String nombre = console.readText("Nombre del periodo (ej: 2024-1, 2024-2)");
        if (nombre.isEmpty()) {
            return;
        }

        try {
            PeriodoAcademicoCrearDto request = new PeriodoAcademicoCrearDto(nombre);
            PeriodoAcademicoDto response = controlador.registrar(request);

            console.showMessage("\nPeriodo registrado correctamente.");
            console.showMessage("   ID asignado: " + response.id());
            console.showMessage("   Nombre: " + response.nombre());
            console.showMessage("   Estado: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTADO DE PERIODOS ACADEMICOS ---");

        try {
            List<PeriodoAcademicoDto> lista = controlador.listarTodos();

            if (lista.isEmpty()) {
                console.showMessage("No hay periodos academicos registrados en el sistema.");
                return;
            }

            console.showMessage("Total de periodos: " + lista.size());
            console.showMessage(formatter.separator());

            for (PeriodoAcademicoDto p : lista) {
                console.showMessage("ID: " + p.id());
                console.showMessage("   Nombre: " + p.nombre());
                console.showMessage("   Estado: " + p.estado().getDescription());
                console.showMessage(formatter.separator());
            }

        } catch (RuntimeException e) {
            console.showError("Error al listar periodos: " + e.getMessage());
        }
    }

    private void actualizar(Console console) {
        console.showMessage("\n--- ACTUALIZAR NOMBRE DEL PERIODO ---");

        Integer id = console.readInteger("ID del periodo a actualizar");
        if (id == null) {
            return;
        }
        try {
            String nuevoNombre = console.readText("Nuevo nombre del periodo ([Enter] para cancelar)");
            if (nuevoNombre.isBlank()) {
                console.showMessage("Operacion cancelada: No se proporciono un nuevo nombre.");
                return;
            }

            PeriodoAcademicoActualizarDto request = new PeriodoAcademicoActualizarDto(id, nuevoNombre);
            PeriodoAcademicoDto response = controlador.actualizar(request);

            console.showMessage("\nNombre del periodo actualizado correctamente.");
            console.showMessage("   Nombre actual: " + response.nombre());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void suspender(Console console) {
        console.showMessage("\n--- SUSPENDER PERIODO ACADEMICO ---");
        console.showMessage("ADVERTENCIA: Esta accion desactivara el periodo academico.");
        console.showMessage("Los periodos academicos NO se pueden eliminar fisicamente, solo suspender.");

        Integer id = console.readInteger("ID del periodo a suspender");
        if (id == null) {
            return;
        }
        try {
            if (!console.readConfirmation("SEGURO que desea suspender este periodo?")) {
                console.showMessage("Operacion cancelada por el usuario.");
                return;
            }

            PeriodoAcademicoDto response = controlador.suspender(id);

            console.showMessage("\nPeriodo academico suspendido correctamente.");
            console.showMessage("   Estado actual: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void reactivar(Console console) {
        console.showMessage("\n--- REACTIVAR PERIODO ACADEMICO ---");

        Integer id = console.readInteger("ID del periodo a reactivar");
        if (id == null) {
            return;
        }

        try {
            PeriodoAcademicoDto response = controlador.reactivar(id);

            console.showMessage("\nPeriodo academico reactivado correctamente.");
            console.showMessage("   Estado actual: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }
}
