package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableColumn;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.ProfesorControlador;
import edu.unimag.dto.profesor.ProfesorActualizarDto;
import edu.unimag.dto.profesor.ProfesorCrearDto;
import edu.unimag.dto.profesor.ProfesorDto;

import java.util.List;

public class ProfesorVista implements SystemModule {

    private final ProfesorControlador controlador;
    private final List<TableColumn> columnasTablaProfesor;
    private final int ANCHO_TABLA;

    public ProfesorVista(ProfesorControlador controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;

        this.columnasTablaProfesor = List.of(
                new TableColumn("ID", "%-6s"),
                new TableColumn("Nombre", "%-35s"),
                new TableColumn("Celular", "%-15s"),
                new TableColumn("Estado", "%-10s")
        );
        ANCHO_TABLA = 90;
    }

    @Override
    public String getModuleName() {
        return "Profesores";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        this.mostrarMenu(console, formatter, renderer);
    }

    private void mostrarMenu(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        boolean ejecutar = true;
        while (ejecutar) {
            int total = controlador.contarProfesores();

            String opcionListar = "2. Listar Profesores (" + total + ")";
            String opcionActualizar = total == 0
                    ? "3. Actualizar Profesor (bloqueado)"
                    : "3. Actualizar Profesor";

            console.showMenu(
                    "MODULO DE PROFESORES",
                    "1. Registrar Profesor",
                    opcionListar,
                    opcionActualizar,
                    "0. Regresar al Menu Principal"
            );

            String opcion = console.readText("Seleccione una opcion");
            switch (opcion) {
                case "1" ->
                    registrar(console, formatter, renderer);
                case "2" ->
                    listar(console, formatter, renderer);
                case "3" -> {
                    if (total == 0) {
                        console.showMessage("\nNo hay profesores registrados");
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
        console.showMessage("\n--- REGISTRAR NUEVO PROFESOR ---");

        String nombre = console.readText("Nombre del profesor [Enter para cancelar]");
        if (nombre == null || nombre.isBlank()) {
            console.showMessage("Operacion cancelada");
            return;
        }

        String celular = null;
        while (celular == null) {
            String input = console.readText("Celular (minimo 7 digitos)");

            if (controlador.validarCelular(input)) {
                celular = input.trim();
            } else {
                console.showError("El celular debe tener minimo 7 digitos. Intente de nuevo");
            }
        }

        try {
            ProfesorCrearDto request = new ProfesorCrearDto(nombre, celular);
            ProfesorDto response = controlador.registrar(request);

            console.showMessage("\nProfesor registrado correctamente");
            renderer.renderSingle(
                    columnasTablaProfesor,
                    response,
                    profesor -> extraerDatosFila(profesor, formatter),
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
        try {
            List<ProfesorDto> lista = controlador.listarTodos();
            if (lista.isEmpty()) {
                console.showMessage("No hay profesores registrados");
                return;
            }

            console.showMessage("\nLISTADO DE PROFESORES: " + lista.size());
            renderer.render(
                    columnasTablaProfesor,
                    lista,
                    profesor -> extraerDatosFila(profesor, formatter),
                    ANCHO_TABLA
            );
            console.pause();

        } catch (RuntimeException e) {
            console.showError("Error al listar profesores: " + e.getMessage());
        }
    }

    private void actualizar(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        console.showMessage("\n--- ACTUALIZAR DATOS DEL PROFESOR ---");

        Long id = console.readLong("ID profesor actualizar");
        if (id == null) {
            console.showMessage("Operacion actualizar cancelada");
            return;
        }

        try {
            ProfesorDto actual = controlador.buscarPorId(id);

            console.showMessage("\nDatos actuales:");
            renderer.renderSingle(
                    columnasTablaProfesor,
                    actual,
                    profesor -> extraerDatosFila(profesor, formatter),
                    ANCHO_TABLA
            );

            String nuevoNombre = console.readText("\nNuevo nombre ([Enter] para no cambiar)");
            if (nuevoNombre != null && !nuevoNombre.isBlank()) {
                if (!controlador.validarNombre(nuevoNombre)) {
                    console.showError("El nombre no es valido. Se mantiene el nombre actual");
                    nuevoNombre = null;
                }
            } else {
                nuevoNombre = null;
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
                    console.showError("Celular minimo 7 digitos. Intente de nuevo");
                }
            }

            Integer nuevoEstado = leerNuevoEstado(console, formatter, actual);

            if (nuevoNombre == null && nuevoCelular == null && nuevoEstado == null) {
                console.showMessage("No hay datos para actualizar");
                return;
            }

            ProfesorActualizarDto request = new ProfesorActualizarDto(
                    id,
                    nuevoNombre,
                    nuevoCelular,
                    nuevoEstado
            );

            ProfesorDto response = controlador.actualizar(request);

            console.showMessage("\nDatos del profesor actualizados correctamente");
            renderer.renderSingle(
                    columnasTablaProfesor,
                    response,
                    profesor -> extraerDatosFila(profesor, formatter),
                    ANCHO_TABLA
            );
            console.pause();

        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private Integer leerNuevoEstado(Console console, ScreenFormatter formatter, ProfesorDto actual) {
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

    private String textoEstado(ProfesorDto profesor, ScreenFormatter formatter) {
        String descripcionEstado = (profesor != null && profesor.estado() != null)
                ? profesor.estado().getDescription()
                : null;

        return formatter.optionalText(descripcionEstado);
    }

    private Object[] extraerDatosFila(ProfesorDto p, ScreenFormatter formatter) {
        return new Object[]{
            String.valueOf(p.id()),
            p.nombre(),
            formatter.optionalText(p.celular()),
            textoEstado(p, formatter)
        };
    }
}
