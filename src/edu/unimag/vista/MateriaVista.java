package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.MateriaControlador;
import edu.unimag.dto.materia.MateriaActualizarDto;
import edu.unimag.dto.materia.MateriaCrearDto;
import edu.unimag.dto.materia.MateriaDto;

import java.util.List;

public class MateriaVista implements SystemModule {

    private final MateriaControlador controlador;

    public MateriaVista(MateriaControlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;
    }

    @Override
    public String getModuleName() {
        return "Materias";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        boolean ejecutar = true;
        while (ejecutar) {
            console.showMenu(
                    "MODULO DE MATERIAS",
                    "1. Registrar Materia",
                    "2. Listar Materias",
                    "3. Actualizar (Nombre/Creditos/Cupo)",
                    "4. Suspender Materia",
                    "5. Reactivar Materia",
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
        console.showMessage("\n--- REGISTRAR NUEVA MATERIA ---");

        String nombre = console.readText("Nombre de la materia");
        if (nombre.isEmpty()) {
            return;
        }

        Integer creditos = console.readInteger("Cantidad de creditos (1-6)");
        if (creditos == null) {
            return;
        }

        Integer cupo = console.readInteger("Cupo maximo (1-50)");
        if (cupo == null) {
            return;
        }

        try {
            MateriaCrearDto request = new MateriaCrearDto(nombre, creditos, cupo);
            MateriaDto response = controlador.registrar(request);

            console.showMessage("\nMateria registrada correctamente.");
            console.showMessage("   ID asignado: " + response.id());
            console.showMessage("   Nombre: " + response.nombre());
            console.showMessage("   Creditos: " + response.creditos());
            console.showMessage("   Cupo maximo: " + response.cupoMaximo());
            console.showMessage("   Estado: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTADO DE MATERIAS ---");

        try {
            List<MateriaDto> lista = controlador.listarTodas();

            if (lista.isEmpty()) {
                console.showMessage("No hay materias registradas en el sistema.");
                return;
            }

            console.showMessage("Total de materias: " + lista.size());
            console.showMessage(formatter.separator());

            for (MateriaDto m : lista) {
                console.showMessage("ID: " + m.id());
                console.showMessage("   Nombre: " + m.nombre());
                console.showMessage("   Creditos: " + m.creditos() + " | Cupo: " + m.cupoMaximo());
                console.showMessage("   Estado: " + m.estado().getDescription());
                console.showMessage(formatter.separator());
            }

        } catch (RuntimeException e) {
            console.showError("Error al listar materias: " + e.getMessage());
        }
    }

    private void actualizar(Console console) {
        console.showMessage("\n--- ACTUALIZAR MATERIA ---");

        Integer id = console.readInteger("ID de la materia");
        if (id == null) {
            return;
        }
        try {
            String nuevoNombre = console.readText("Nuevo nombre ([Enter] para no cambiar)");
            if (nuevoNombre.isBlank()) {
                nuevoNombre = null;
            }

            Integer nuevosCreditos = console.readInteger("Nuevos creditos (1-6, [Enter] para no cambiar)");
            Integer nuevoCupo = console.readInteger("Nuevo cupo maximo (1-50, [Enter] para no cambiar)");

            if (nuevoNombre == null && nuevosCreditos == null && nuevoCupo == null) {
                console.showMessage("No se proporcionaron datos para actualizar. Operacion cancelada.");
                return;
            }

            MateriaActualizarDto request = new MateriaActualizarDto(id, nuevoNombre, nuevosCreditos, nuevoCupo);
            MateriaDto response = controlador.actualizar(request);

            console.showMessage("\nMateria actualizada correctamente.");
            console.showMessage("   Nombre actual: " + response.nombre());
            console.showMessage("   Creditos actuales: " + response.creditos());
            console.showMessage("   Cupo actual: " + response.cupoMaximo());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void suspender(Console console) {
        console.showMessage("\n--- SUSPENDER MATERIA ---");
        console.showMessage("ADVERTENCIA: Esta accion desactivara la materia.");
        console.showMessage("Las materias NO se pueden eliminar fisicamente, solo suspender.");

        Integer id = console.readInteger("ID de la materia a suspender");
        if (id == null) {
            return;
        }
        try {
            if (!console.readConfirmation("SEGURO que desea suspender esta materia?")) {
                console.showMessage("Operacion cancelada por el usuario.");
                return;
            }

            MateriaDto response = controlador.suspender(id);

            console.showMessage("\nMateria suspendida correctamente.");
            console.showMessage("   Estado actual: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void reactivar(Console console) {
        console.showMessage("\n--- REACTIVAR MATERIA ---");

        Integer id = console.readInteger("ID de la materia a reactivar");
        if (id == null) {
            return;
        }
        try {
            MateriaDto response = controlador.reactivar(id);

            console.showMessage("\nMateria reactivada correctamente.");
            console.showMessage("   Estado actual: " + response.estado().getDescription());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }
}
