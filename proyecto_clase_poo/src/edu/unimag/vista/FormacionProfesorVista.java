package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.FormacionProfesorControlador;
import edu.unimag.dto.formacion.FormacionProfesorActualizarDto;
import edu.unimag.dto.formacion.FormacionProfesorCrearDto;
import edu.unimag.dto.formacion.FormacionProfesorDto;

import java.time.OffsetDateTime;
import java.util.List;

public class FormacionProfesorVista implements SystemModule {

    private final FormacionProfesorControlador controlador;

    public FormacionProfesorVista(FormacionProfesorControlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;
    }

    @Override
    public String getModuleName() {
        return "Formaciones de profesores";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        boolean ejecutar = true;
        while (ejecutar) {
            console.showMenu(
                    "MODULO DE FORMACIONES ACADEMICAS",
                    "1. Registrar Formacion",
                    "2. Listar Todas las Formaciones",
                    "3. Listar Formaciones por Profesor",
                    "4. Actualizar Formacion",
                    "5. Eliminar Formacion",
                    "0. Regresar al Menu Principal"
            );

            String opcion = console.readText("Seleccione una opcion");
            switch (opcion) {
                case "1" ->
                    registrar(console, formatter);
                case "2" ->
                    listarTodos(console, formatter);
                case "3" ->
                    listarPorProfesor(console, formatter);
                case "4" ->
                    actualizar(console, formatter);
                case "5" ->
                    eliminar(console);
                case "0" ->
                    ejecutar = false;
                default ->
                    console.showError("Opcion incorrecta");
            }
        }
    }

    private void registrar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- REGISTRAR NUEVA FORMACION ACADEMICA ---");

        Long idProfesor = console.readLong("ID del profesor");
        if (idProfesor == null) {
            return;
        }

        String titulo = console.readText("Titulo de la formacion (ej: Magister en Ciencias)");
        if (titulo.isEmpty()) {
            return;
        }

        OffsetDateTime fecha = console.readDate("Fecha de obtencion");
        if (fecha == null) {
            return;
        }

        try {
            FormacionProfesorCrearDto request = new FormacionProfesorCrearDto(idProfesor, titulo, fecha);
            FormacionProfesorDto response = controlador.registrar(request);

            console.showMessage("\nFormacion registrada correctamente.");
            console.showMessage("   ID Formacion: " + response.id());
            console.showMessage("   Titulo: " + response.titulo());
            console.showMessage("   Fecha: " + formatter.date(response.fecha()));
            console.showMessage("   Profesor: " + response.profesor().nombre());

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listarTodos(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTADO DE FORMACIONES ACADEMICAS ---");

        try {
            List<FormacionProfesorDto> formaciones = controlador.listarTodos();

            if (formaciones.isEmpty()) {
                console.showMessage("No hay formaciones academicas registradas en el sistema.");
                return;
            }

            console.showMessage("Total de formaciones: " + formaciones.size());
            console.showMessage(formatter.separator());

            for (FormacionProfesorDto f : formaciones) {
                console.showMessage("ID: " + f.id());
                console.showMessage("   Titulo: " + f.titulo());
                console.showMessage("   Fecha: " + formatter.date(f.fecha()));
                console.showMessage("   Profesor: " + f.profesor().nombre() + " (ID: " + f.profesor().id() + ")");
                console.showMessage(formatter.separator());
            }

        } catch (RuntimeException e) {
            console.showError("Error al listar formaciones: " + e.getMessage());
        }
    }

    private void listarPorProfesor(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR FORMACIONES POR PROFESOR ---");

        Long idProfesor = console.readLong("ID del profesor");
        if (idProfesor == null) {
            return;
        }

        try {
            List<FormacionProfesorDto> filtradas = controlador.listarPorProfesor(idProfesor);

            if (filtradas.isEmpty()) {
                console.showMessage("No se encontraron formaciones para el profesor ID: " + idProfesor);
                return;
            }

            console.showMessage("Formaciones del profesor ID: " + idProfesor);
            console.showMessage("Total: " + filtradas.size());
            console.showMessage(formatter.separator());

            for (FormacionProfesorDto f : filtradas) {
                console.showMessage("ID: " + f.id());
                console.showMessage("   Titulo: " + f.titulo());
                console.showMessage("   Fecha: " + formatter.date(f.fecha()));
                console.showMessage(formatter.separator());
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar formaciones: " + e.getMessage());
        }
    }

    private void actualizar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- ACTUALIZAR FORMACION ACADEMICA ---");

        Long id = console.readLong("ID de la formacion a actualizar");
        if (id == null) {
            return;
        }

        try {
            String nuevoTitulo = console.readText("Nuevo titulo ([Enter] para no cambiar)");
            if (nuevoTitulo.isBlank()) {
                nuevoTitulo = null;
            }

            OffsetDateTime nuevaFecha = console.readDate("Nueva fecha ([Enter] para no cambiar)");

            if (nuevoTitulo == null && nuevaFecha == null) {
                console.showMessage("No se proporcionaron datos para actualizar. Operacion cancelada.");
                return;
            }

            FormacionProfesorActualizarDto request = new FormacionProfesorActualizarDto(id, nuevoTitulo, nuevaFecha);
            FormacionProfesorDto response = controlador.actualizar(request);

            console.showMessage("\nFormacion actualizada correctamente.");
            console.showMessage("   Titulo actual: " + response.titulo());
            console.showMessage("   Fecha actual: " + formatter.date(response.fecha()));

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void eliminar(Console console) {
        console.showMessage("\n--- ELIMINAR FORMACION ACADEMICA ---");
        console.showMessage("ADVERTENCIA: Esta accion es irreversible.");

        Long id = console.readLong("ID de la formacion a eliminar");
        if (id == null) {
            return;
        }

        try {
            if (!console.readConfirmation("SEGURO de que desea eliminar esta formacion?")) {
                console.showMessage("Operacion cancelada por el usuario.");
                return;
            }

            boolean eliminado = controlador.eliminar(id);

            if (eliminado) {
                console.showMessage("\nFormacion eliminada del sistema correctamente.");
            } else {
                console.showMessage("\nNo se pudo eliminar la formacion (puede que ya no exista).");
            }

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("PROTECCION DE INTEGRIDAD: " + e.getMessage());
        }
    }
}
