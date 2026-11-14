package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableColumn;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.EstudianteControlador;
import edu.unimag.dto.estudiante.EstudianteActualizarDto;
import edu.unimag.dto.estudiante.EstudianteCrearDto;
import edu.unimag.dto.estudiante.EstudianteDto;

import java.util.List;
import java.util.UUID;

public class EstudianteVista implements SystemModule {

    private final EstudianteControlador controlador;
    private final List<TableColumn> columnasTablaEstudiante;
    private final int ANCHO_TABLA;

    public EstudianteVista(EstudianteControlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;

        this.columnasTablaEstudiante = List.of(
                new TableColumn("Codigo", "%-10s"),
                new TableColumn("Nombre", "%-30s"),
                new TableColumn("Correo", "%-30s"),
                new TableColumn("Celular", "%-15s"),
                new TableColumn("Direccion", "%-25s"),
                new TableColumn("Estado", "%-10s")
        );
        ANCHO_TABLA = 145;
    }

    @Override
    public String getModuleName() {
        return "Estudiantes";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        this.mostrarMenu(console, formatter, renderer);
    }

    private void mostrarMenu(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        boolean ejecutar = true;
        while (ejecutar) {
            int total = controlador.contarEstudiantes();

            String opcionListar = "2. Listar Estudiantes (" + total + ")";
            String opcionActualizar = total == 0
                    ? "3. Actualizar Datos de Contacto (bloqueado)"
                    : "3. Actualizar Datos de Contacto";

            console.showMenu(
                    "MENU ESTUDIANTES",
                    "1. Registrar",
                    opcionListar,
                    opcionActualizar,
                    "0. Regresar"
            );

            String opcion = console.readText("Seleccione una opcion");
            switch (opcion) {
                case "1" ->
                    registrar(console, formatter, renderer);
                case "2" ->
                    listar(console, formatter, renderer);
                case "3" -> {
                    if (total == 0) {
                        console.showMessage("\nNo hay estudiantes registrados. Registre uno primero.");
                    } else {
                        actualizar(console, formatter, renderer);
                    }
                }
                case "0" ->
                    ejecutar = false;
                default ->
                    console.showError("Opcion incorrecta");
            }
        }
    }

    private void registrar(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("\n--- NUEVO ESTUDIANTE ---");

        String codigo = console.readText("Codigo [Enter para cancelar]");
        if (codigo == null || codigo.isBlank()) {
            console.showMessage("Operacion cancelada.");
            return;
        }
        if (!controlador.validarCodigo(codigo)) {
            console.showError("El codigo no es valido.");
            return;
        }

        String nombre = console.readText("Nombre [Enter para cancelar]");
        if (nombre == null || nombre.isBlank()) {
            console.showMessage("Operacion cancelada.");
            return;
        }
        if (!controlador.validarNombre(nombre)) {
            console.showError("El nombre no es valido.");
            return;
        }

        String correo = null;
        boolean pidiendoCorreo = true;
        while (pidiendoCorreo) {
            String input = console.readText("Correo ([Enter] para omitir)");
            if (input == null || input.isBlank()) {
                pidiendoCorreo = false;
            } else if (controlador.validarCorreo(input)) {
                correo = input.trim();
                pidiendoCorreo = false;
            } else {
                console.showError("Formato de correo invalido. Intente de nuevo.");
            }
        }

        String celular = null;
        boolean pidiendoCelular = true;
        while (pidiendoCelular) {
            String input = console.readText("Celular ([Enter] para omitir)");
            if (input == null || input.isBlank()) {
                pidiendoCelular = false;
            } else if (controlador.validarCelular(input)) {
                celular = input.trim();
                pidiendoCelular = false;
            } else {
                console.showError("El celular debe tener minimo 7 digitos. Intente de nuevo.");
            }
        }

        String direccion = console.readText("Direccion [Enter para cancelar]");
        if (direccion == null || direccion.isBlank()) {
            console.showMessage("Operacion cancelada.");
            return;
        }

        try {
            EstudianteCrearDto request = new EstudianteCrearDto(codigo, nombre, correo, celular, direccion);
            EstudianteDto response = controlador.registrar(request);

            console.showMessage("\nEstudiante registrado correctamente.");
            console.showMessage("ID asignado: " + response.id());
            renderer.renderSingle(
                    columnasTablaEstudiante,
                    response,
                    estudiante -> extraerDatosFila(estudiante, formatter),
                    ANCHO_TABLA
            );
            console.pause();

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listar(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("\n--- LISTADO DE ESTUDIANTES ---");

        try {
            List<EstudianteDto> lista = controlador.listarTodos();

            if (lista.isEmpty()) {
                console.showMessage("No hay estudiantes registrados en el sistema.");
                return;
            }

            console.showMessage("Total de estudiantes: " + lista.size());
            renderer.render(
                    columnasTablaEstudiante,
                    lista,
                    estudiante -> extraerDatosFila(estudiante, formatter),
                    ANCHO_TABLA
            );
            console.pause();

        } catch (RuntimeException e) {
            console.showError("Error al listar estudiantes: " + e.getMessage());
        }
    }

    private void actualizar(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("\n--- ACTUALIZAR DATOS DEL ESTUDIANTE ---");

        UUID id = console.readUUID("ID del estudiante");
        if (id == null) {
            return;
        }
        try {
            EstudianteDto actual = controlador.buscarPorId(id);

            console.showMessage("\nDatos actuales:");
            renderer.renderSingle(
                    columnasTablaEstudiante,
                    actual,
                    estudiante -> extraerDatosFila(estudiante, formatter),
                    ANCHO_TABLA
            );

            String nuevoCorreo = null;
            boolean pidiendoCorreo = true;
            while (pidiendoCorreo) {
                String input = console.readText("\nNuevo correo ([Enter] para no cambiar)");
                if (input == null || input.isBlank()) {
                    pidiendoCorreo = false;
                } else if (controlador.validarCorreo(input)) {
                    nuevoCorreo = input.trim();
                    pidiendoCorreo = false;
                } else {
                    console.showError("Formato de correo invalido. Intente de nuevo.");
                }
            }

            String nuevoCelular = null;
            boolean pidiendoCelular = true;
            while (pidiendoCelular) {
                String input = console.readText("Nuevo celular ([Enter] para no cambiar)");
                if (input == null || input.isBlank()) {
                    pidiendoCelular = false;
                } else if (controlador.validarCelular(input)) {
                    nuevoCelular = input.trim();
                    pidiendoCelular = false;
                } else {
                    console.showError("El celular debe tener minimo 7 digitos. Intente de nuevo.");
                }
            }

            String nuevaDireccion = console.readText("Nueva direccion ([Enter] para no cambiar)");
            if (nuevaDireccion != null && nuevaDireccion.isBlank()) {
                nuevaDireccion = null;
            }

            Integer nuevoEstado = leerNuevoEstado(console, formatter, actual);

            if (nuevoCorreo == null && nuevoCelular == null && nuevaDireccion == null && nuevoEstado == null) {
                console.showMessage("No se proporcionaron datos para actualizar. Operacion cancelada.");
                return;
            }

            EstudianteActualizarDto request = new EstudianteActualizarDto(
                    id,
                    nuevoCorreo,
                    nuevoCelular,
                    nuevaDireccion,
                    nuevoEstado
            );

            EstudianteDto response = controlador.actualizar(request);

            console.showMessage("\nDatos del estudiante actualizados correctamente.");
            renderer.renderSingle(
                    columnasTablaEstudiante,
                    response,
                    estudiante -> extraerDatosFila(estudiante, formatter),
                    ANCHO_TABLA
            );
            console.pause();

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private Integer leerNuevoEstado(Console console, ScreenFormatter formatter, EstudianteDto actual) {
        while (true) {
            console.showMessage("\nEstado actual: " + textoEstado(actual, formatter));
            console.showMessage("1. Activo");
            console.showMessage("2. Inactivo");

            String entrada = console.readText("Nuevo estado ([Enter] para no cambiar)");
            if (entrada == null || entrada.isBlank()) {
                return null;
            }

            try {
                Integer opcion = Integer.valueOf(entrada.trim());

                if (opcion != 1 && opcion != 2) {
                    console.showError("El estado debe ser 1 (Activo) o 2 (Inactivo)");
                    continue;
                }

                if ((opcion == 1 && actual.activo()) || (opcion == 2 && !actual.activo())) {
                    console.showMessage("El estado es igual al actual. No hay cambios");
                    return null;
                }

                return opcion;

            } catch (NumberFormatException e) {
                console.showError("Debe ingresar un numero valido: 1, 2 o [Enter]");
            }
        }
    }

    private String textoEstado(EstudianteDto estudiante, ScreenFormatter formatter) {
        String descripcionEstado = (estudiante != null && estudiante.estado() != null)
                ? estudiante.estado().getDescription()
                : null;

        return formatter.optionalText(descripcionEstado);
    }

    private Object[] extraerDatosFila(EstudianteDto e, ScreenFormatter formatter) {
        return new Object[]{
            e.codigo(),
            e.nombre(),
            formatter.optionalText(e.correo()),
            formatter.optionalText(e.celular()),
            formatter.optionalText(e.direccion()),
            textoEstado(e, formatter)
        };
    }
}
