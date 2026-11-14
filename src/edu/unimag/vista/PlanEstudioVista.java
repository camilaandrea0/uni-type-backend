package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.PlanEstudioControlador;
import edu.unimag.dto.planestudio.PlanEstudioActualizarDto;
import edu.unimag.dto.planestudio.PlanEstudioCrearDto;
import edu.unimag.dto.planestudio.PlanEstudioDto;
import edu.unimag.dto.programa.ProgramaDto;

import java.util.List;
import java.util.UUID;

public class PlanEstudioVista implements SystemModule {

    private final PlanEstudioControlador controlador;

    public PlanEstudioVista(PlanEstudioControlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;
    }

    @Override
    public String getModuleName() {
        return "Planes de Estudio";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        this.mostrarMenu(console, formatter);
    }

    private void mostrarMenu(Console console, ScreenFormatter formatter) {
        boolean ejecutar = true;
        while (ejecutar) {
            console.showMenu(
                    "MODULO DE PLANES DE ESTUDIO",
                    "1. Registrar Plan de Estudio",
                    "2. Listar Todos los Planes",
                    "3. Buscar Plan por ID",
                    "4. Actualizar Version",
                    "5. Activar Plan",
                    "6. Inactivar Plan",
                    "7. Listar Planes por Programa",
                    "0. Regresar al Menu Principal"
            );

            String opcion = console.readText("Seleccione una opcion");
            switch (opcion) {
                case "1" ->
                    registrar(console, formatter);
                case "2" ->
                    listarTodos(console, formatter);
                case "3" ->
                    buscarPorId(console, formatter);
                case "4" ->
                    actualizar(console, formatter);
                case "5" ->
                    activar(console);
                case "6" ->
                    inactivar(console);
                case "7" ->
                    listarPorPrograma(console, formatter);
                case "0" ->
                    ejecutar = false;
                default ->
                    console.showError("Opcion incorrecta");
            }
        }
    }

    private void registrar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- REGISTRAR NUEVO PLAN DE ESTUDIO ---");

        UUID idPrograma = console.readUUID("ID del programa");
        if (idPrograma == null) {
            return;
        }

        ProgramaDto programa;
        try {
            programa = controlador.buscarPrograma(idPrograma);
        } catch (IllegalArgumentException e) {
            console.showError("El programa no existe. " + e.getMessage());
            return;
        }

        console.showMessage("\nInformacion del programa seleccionado:");
        console.showMessage("   Programa: " + programa.nombre());
        console.showMessage("   Facultad: " + programa.facultad().nombre());
        console.showMessage("   Nivel: " + programa.nivelPrograma().getDescription());

        String version = console.readText("\nVersion del plan de estudio");
        if (version.isEmpty()) {
            return;
        }

        try {
            PlanEstudioCrearDto request = new PlanEstudioCrearDto(version, idPrograma);
            PlanEstudioDto response = controlador.registrar(request);

            console.showMessage("\nPlan de estudio registrado correctamente.");
            mostrarPlanDetallado(console, formatter, response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listarTodos(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTADO DE PLANES DE ESTUDIO ---");

        try {
            List<PlanEstudioDto> lista = controlador.listarTodos();

            if (lista.isEmpty()) {
                console.showMessage("No hay planes de estudio registrados en el sistema.");
                return;
            }

            console.showMessage("Total de planes: " + lista.size());
            console.showMessage(formatter.separator());

            for (PlanEstudioDto p : lista) {
                mostrarPlanResumen(console, formatter, p);
                console.showMessage(formatter.separator());
            }

        } catch (RuntimeException e) {
            console.showError("Error al listar planes: " + e.getMessage());
        }
    }

    private void buscarPorId(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- BUSCAR PLAN DE ESTUDIO POR ID ---");

        Long id = console.readLong("ID del plan de estudio");
        if (id == null) {
            return;
        }

        try {
            PlanEstudioDto response = controlador.buscarPorId(id);

            console.showMessage("\nPlan de estudio encontrado.");
            mostrarPlanDetallado(console, formatter, response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void actualizar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- ACTUALIZAR VERSION DEL PLAN ---");

        Long id = console.readLong("ID del plan a actualizar");
        if (id == null) {
            return;
        }

        try {
            PlanEstudioDto planActual = controlador.buscarPorId(id);

            console.showMessage("\nInformacion actual del plan:");
            mostrarPlanDetallado(console, formatter, planActual);

            String nuevaVersion = console.readText("\nNueva version del plan");
            if (nuevaVersion.isBlank()) {
                console.showMessage("Operacion cancelada: No se proporciono una nueva version.");
                return;
            }

            PlanEstudioActualizarDto request = new PlanEstudioActualizarDto(id, nuevaVersion);
            PlanEstudioDto response = controlador.actualizar(request);

            console.showMessage("\nVersion actualizada correctamente.");
            mostrarPlanDetallado(console, formatter, response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void activar(Console console) {
        console.showMessage("\n--- ACTIVAR PLAN DE ESTUDIO ---");

        Long id = console.readLong("ID del plan a activar");
        if (id == null) {
            return;
        }

        try {
            PlanEstudioDto response = controlador.activar(id);

            console.showMessage("\nPlan de estudio activado correctamente.");
            console.showMessage("   Estado actual: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void inactivar(Console console) {
        console.showMessage("\n--- INACTIVAR PLAN DE ESTUDIO ---");
        console.showMessage("ADVERTENCIA: Esta accion desactivara el plan de estudio.");
        console.showMessage("Los planes NO se pueden eliminar fisicamente, solo inactivar.");

        Long id = console.readLong("ID del plan a inactivar");
        if (id == null) {
            return;
        }

        try {
            if (!console.readConfirmation("SEGURO que desea inactivar este plan?")) {
                console.showMessage("Operacion cancelada por el usuario.");
                return;
            }

            PlanEstudioDto response = controlador.inactivar(id);

            console.showMessage("\nPlan de estudio inactivado correctamente.");
            console.showMessage("   Estado actual: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listarPorPrograma(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR PLANES POR PROGRAMA ---");

        UUID idPrograma = console.readUUID("ID del programa");
        if (idPrograma == null) {
            return;
        }

        try {
            List<PlanEstudioDto> lista = controlador.listarPorPrograma(idPrograma);

            if (lista.isEmpty()) {
                console.showMessage("No se encontraron planes de estudio para este programa.");
                return;
            }

            console.showMessage("Planes del programa: " + lista.get(0).programa().nombre());
            console.showMessage("Facultad: " + lista.get(0).programa().facultad().nombre());
            console.showMessage("Total: " + lista.size());
            console.showMessage(formatter.separator());

            for (PlanEstudioDto p : lista) {
                mostrarPlanResumen(console, formatter, p);
                console.showMessage(formatter.separator());
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar planes: " + e.getMessage());
        }
    }

    // Métodos auxiliares
    private void mostrarPlanResumen(Console console, ScreenFormatter formatter, PlanEstudioDto p) {
        console.showMessage("ID: " + p.id());
        console.showMessage("   Version: " + p.version());
        console.showMessage("   Programa: " + p.programa().nombre());
        console.showMessage("   Facultad: " + p.programa().facultad().nombre());
        console.showMessage("   Nivel: " + p.programa().nivelPrograma().getDescription());
        console.showMessage("   Estado: " + p.estado().getDescription());
        console.showMessage("   Fecha creacion: " + formatter.dateTime(p.fechaCreacion()));
    }

    private void mostrarPlanDetallado(Console console, ScreenFormatter formatter, PlanEstudioDto p) {
        console.showMessage("   ID: " + p.id());
        console.showMessage("   Version: " + p.version());
        console.showMessage("   Programa: " + p.programa().nombre());
        console.showMessage("   Facultad: " + p.programa().facultad().nombre());
        console.showMessage("   Nivel: " + p.programa().nivelPrograma().getDescription());
        console.showMessage("   Estado: " + p.estado().getDescription());
        console.showMessage("   Fecha creacion: " + formatter.dateTime(p.fechaCreacion()));
    }
}
