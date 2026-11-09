package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.ProgramaControlador;
import edu.unimag.dto.programa.ProgramaActualizarDto;
import edu.unimag.dto.programa.ProgramaCrearDto;
import edu.unimag.dto.programa.ProgramaDto;
import edu.unimag.modelo.enumeracion.NivelPrograma;

import java.util.List;
import java.util.UUID;

public class ProgramaVista implements SystemModule {

    private final ProgramaControlador controlador;

    public ProgramaVista(ProgramaControlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;
    }

    @Override
    public String getModuleName() {
        return "Programas Academicos";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        this.mostrarMenu(console);
    }

    private void mostrarMenu(Console console) {
        boolean ejecutar = true;
        while (ejecutar) {
            console.showMenu(
                    "MODULO DE PROGRAMAS ACADEMICOS",
                    "1. Registrar Programa",
                    "2. Listar Todos los Programas",
                    "3. Buscar Programa por ID",
                    "4. Actualizar Programa",
                    "5. Suspender Programa",
                    "6. Reactivar Programa",
                    "7. Listar Programas por Facultad",
                    "0. Regresar al Menu Principal"
            );

            String opcion = console.readText("Seleccione una opcion");
            switch (opcion) {
                case "1" ->
                    registrar(console);
                case "2" ->
                    listarTodos(console);
                case "3" ->
                    buscarPorId(console);
                case "4" ->
                    actualizar(console);
                case "5" ->
                    suspender(console);
                case "6" ->
                    reactivar(console);
                case "7" ->
                    listarPorFacultad(console);
                case "0" ->
                    ejecutar = false;
                default ->
                    console.showError("Opcion incorrecta");
            }
        }
    }

    private void registrar(Console console) {
        console.showMessage("\n--- REGISTRAR NUEVO PROGRAMA ---");

        String nombre = console.readText("Nombre del programa");
        if (nombre.isEmpty()) {
            return;
        }

        NivelPrograma nivel = seleccionarNivel(console);
        if (nivel == null) {
            return;
        }

        Long idFacultad = console.readLong("ID de la facultad");
        if (idFacultad == null) {
            return;
        }

        try {
            ProgramaCrearDto request = new ProgramaCrearDto(nombre, nivel, idFacultad);
            ProgramaDto response = controlador.registrar(request);

            console.showMessage("\nPrograma registrado correctamente.");
            mostrarProgramaDetallado(console, response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listarTodos(Console console) {
        console.showMessage("\n--- LISTADO DE PROGRAMAS ---");

        try {
            List<ProgramaDto> lista = controlador.listarTodos();

            if (lista.isEmpty()) {
                console.showMessage("No hay programas registrados en el sistema.");
                return;
            }

            console.showMessage("Total de programas: " + lista.size());

            for (ProgramaDto p : lista) {
                mostrarProgramaResumen(console, p);
            }

        } catch (RuntimeException e) {
            console.showError("Error al listar programas: " + e.getMessage());
        }
    }

    private void buscarPorId(Console console) {
        console.showMessage("\n--- BUSCAR PROGRAMA POR ID ---");

        UUID id = console.readUUID("ID del programa");
        if (id == null) {
            return;
        }

        try {
            ProgramaDto response = controlador.buscarPorId(id);

            console.showMessage("\nPrograma encontrado.");
            mostrarProgramaDetallado(console, response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void actualizar(Console console) {
        console.showMessage("\n--- ACTUALIZAR PROGRAMA ---");

        UUID id = console.readUUID("ID del programa a actualizar");
        if (id == null) {
            return;
        }

        try {
            ProgramaDto programaActual = controlador.buscarPorId(id);

            console.showMessage("\nInformacion actual del programa:");
            mostrarProgramaDetallado(console, programaActual);

            String nuevoNombre = console.readText("Nuevo nombre ([Enter] para no cambiar)");
            if (nuevoNombre.isBlank()) {
                nuevoNombre = null;
            }

            NivelPrograma nuevoNivel = null;
            if (console.readConfirmation("Desea cambiar el nivel academico?")) {
                nuevoNivel = seleccionarNivel(console);
                if (nuevoNivel == null) {
                    return;
                }
            }

            Long nuevoIdFacultad = null;
            if (console.readConfirmation("Desea cambiar la facultad?")) {
                nuevoIdFacultad = solicitarCambioFacultad(console, programaActual);
                if (nuevoIdFacultad == null) {
                    return;
                }
            }

            if (nuevoNombre == null && nuevoNivel == null && nuevoIdFacultad == null) {
                console.showMessage("No se realizaron cambios. Operacion cancelada.");
                return;
            }

            ProgramaActualizarDto request = new ProgramaActualizarDto(id, nuevoNombre, nuevoNivel, nuevoIdFacultad);
            ProgramaDto response = controlador.actualizar(request);

            console.showMessage("\nPrograma actualizado correctamente.");
            mostrarProgramaDetallado(console, response);

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void suspender(Console console) {
        console.showMessage("\n--- SUSPENDER PROGRAMA ---");
        console.showMessage("ADVERTENCIA: Esta accion desactivara el programa.");
        console.showMessage("Los programas NO se pueden eliminar fisicamente, solo suspender.");

        UUID id = console.readUUID("ID del programa a suspender");
        if (id == null) {
            return;
        }

        try {
            if (!console.readConfirmation("SEGURO que desea suspender este programa?")) {
                console.showMessage("Operacion cancelada por el usuario.");
                return;
            }

            ProgramaDto response = controlador.suspender(id);

            console.showMessage("\nPrograma suspendido correctamente.");
            console.showMessage("   Estado actual: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void reactivar(Console console) {
        console.showMessage("\n--- REACTIVAR PROGRAMA ---");

        UUID id = console.readUUID("ID del programa a reactivar");
        if (id == null) {
            return;
        }

        try {
            ProgramaDto response = controlador.reactivar(id);

            console.showMessage("\nPrograma reactivado correctamente.");
            console.showMessage("   Estado actual: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listarPorFacultad(Console console) {
        console.showMessage("\n--- LISTAR PROGRAMAS POR FACULTAD ---");

        Long idFacultad = console.readLong("ID de la facultad");
        if (idFacultad == null) {
            return;
        }

        try {
            List<ProgramaDto> lista = controlador.listarPorFacultad(idFacultad);

            if (lista.isEmpty()) {
                console.showMessage("No se encontraron programas para esta facultad.");
                return;
            }

            console.showMessage("Programas de la facultad: " + lista.get(0).facultad().nombre());
            console.showMessage("Total: " + lista.size());

            for (ProgramaDto p : lista) {
                mostrarProgramaResumen(console, p);
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar programas: " + e.getMessage());
        }
    }

    // Métodos auxiliares
    private NivelPrograma seleccionarNivel(Console console) {
        console.showMessage("\nSeleccione el nivel academico:");
        for (NivelPrograma nivel : NivelPrograma.values()) {
            console.showMessage("   " + nivel.getCode() + " - " + nivel.getDescription());
        }
        console.showMessage("   0 - Cancelar");

        while (true) {
            Integer codigo = console.readInteger("Ingrese el codigo del nivel");
            if (codigo == null || codigo == 0) {
                return null;
            }

            for (NivelPrograma nivel : NivelPrograma.values()) {
                if (nivel.getCode() == codigo) {
                    return nivel;
                }
            }

            console.showError("Codigo de nivel invalido. Intente de nuevo.");
        }
    }

    private Long solicitarCambioFacultad(Console console, ProgramaDto programaActual) {
        console.showMessage("\nFacultad actual: " + programaActual.facultad().nombre());

        Long nuevoId = console.readLong("Nuevo ID de facultad ([Enter] para cancelar)");
        if (nuevoId == null) {
            return null;
        }

        if (nuevoId.equals(programaActual.facultad().id())) {
            console.showMessage("La facultad es la misma. No hay cambios.");
            return null;
        }

        console.showMessage("\nEsta a punto de mover:");
        console.showMessage("   Programa: " + programaActual.nombre());
        console.showMessage("   De: " + programaActual.facultad().nombre());
        console.showMessage("   A: Facultad con ID " + nuevoId);

        if (!console.readConfirmation("Continuar con el cambio?")) {
            console.showMessage("Cambio de facultad cancelado.");
            return null;
        }

        return nuevoId;
    }

    private void mostrarProgramaResumen(Console console, ProgramaDto p) {
        console.showMessage("ID: " + p.id());
        console.showMessage("   Nombre: " + p.nombre());
        console.showMessage("   Nivel: " + p.nivelPrograma().getDescription());
        console.showMessage("   Facultad: " + p.facultad().nombre());
        console.showMessage("   Estado: " + p.estado().getDescription());
    }

    private void mostrarProgramaDetallado(Console console, ProgramaDto p) {
        console.showMessage("   ID: " + p.id());
        console.showMessage("   Nombre: " + p.nombre());
        console.showMessage("   Nivel: " + p.nivelPrograma().getDescription()
                + " (codigo: " + p.nivelPrograma().getCode() + ")");
        console.showMessage("   Facultad: " + p.facultad().nombre());
        console.showMessage("   Estado: " + p.estado().getDescription());
    }
}
