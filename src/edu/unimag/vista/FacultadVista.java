package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.FacultadControlador;
import edu.unimag.dto.facultad.FacultadActualizarDto;
import edu.unimag.dto.facultad.FacultadCrearDto;
import edu.unimag.dto.facultad.FacultadDto;

import java.util.List;

public class FacultadVista implements SystemModule {

    private final FacultadControlador controlador;

    public FacultadVista(FacultadControlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;
    }

    @Override
    public String getModuleName() {
        return "Facultades";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        this.mostrarMenu(console, formatter);
    }

    private void mostrarMenu(Console console, ScreenFormatter formatter) {
        boolean ejecutar = true;
        while (ejecutar) {
            console.showMenu(
                    "MODULO DE FACULTADES",
                    "1. Registrar Facultad",
                    "2. Listar Facultades",
                    "3. Actualizar Nombre",
                    "4. Suspender Facultad",
                    "5. Reactivar Facultad",
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
        console.showMessage("\n--- REGISTRAR NUEVA FACULTAD ---");

        String nombre = console.readText("Nombre de la facultad");
        if (nombre.isEmpty()) {
            return;
        }

        try {
            FacultadCrearDto request = new FacultadCrearDto(nombre);
            FacultadDto response = controlador.registrar(request);

            console.showMessage("\nFacultad registrada correctamente.");
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
        console.showMessage("\n--- LISTADO DE FACULTADES ---");

        try {
            List<FacultadDto> lista = controlador.listarTodas();

            if (lista.isEmpty()) {
                console.showMessage("No hay facultades registradas en el sistema.");
                return;
            }

            console.showMessage("Total de facultades: " + lista.size());
            console.showMessage(formatter.separator());

            for (FacultadDto f : lista) {
                console.showMessage("ID: " + f.id());
                console.showMessage("   Nombre: " + f.nombre());
                console.showMessage("   Estado: " + f.estado().getDescription());
                console.showMessage(formatter.separator());
            }

        } catch (RuntimeException e) {
            console.showError("Error al listar facultades: " + e.getMessage());
        }
    }

    private void actualizar(Console console) {
        console.showMessage("\n--- ACTUALIZAR NOMBRE DE FACULTAD ---");

        Long id = console.readLong("ID de la facultad");
        if (id == null) {
            return;
        }

        try {
            String nuevoNombre = console.readText("Nuevo nombre ([Enter] para cancelar)");
            if (nuevoNombre.isBlank()) {
                console.showMessage("Cancelado: No se proporciono un nuevo nombre.");
                return;
            }

            FacultadActualizarDto request = new FacultadActualizarDto(id, nuevoNombre);
            FacultadDto response = controlador.actualizar(request);

            console.showMessage("\nNombre actualizado correctamente.");
            console.showMessage("   Nuevo nombre: " + response.nombre());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void suspender(Console console) {
        console.showMessage("\n--- SUSPENDER FACULTAD ---");
        console.showMessage("ADVERTENCIA: Esta accion desactivara la facultad.");
        console.showMessage("Las facultades NO se pueden eliminar fisicamente, solo suspender.");

        Long id = console.readLong("ID de la facultad a suspender");
        if (id == null) {
            return;
        }

        try {
            if (!console.readConfirmation("SEGURO que desea suspender esta facultad?")) {
                console.showMessage("Operacion cancelada por el usuario.");
                return;
            }

            FacultadDto response = controlador.suspender(id);

            console.showMessage("\nFacultad suspendida correctamente.");
            console.showMessage("   Estado actual: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void reactivar(Console console) {
        console.showMessage("\n--- REACTIVAR FACULTAD ---");

        Long id = console.readLong("ID de la facultad a reactivar");
        if (id == null) {
            return;
        }

        try {
            FacultadDto response = controlador.reactivar(id);

            console.showMessage("\nFacultad reactivada correctamente.");
            console.showMessage("   Estado actual: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }
}
