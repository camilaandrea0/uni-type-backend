package edu.unimag.vista;

import com.cleandev.cli.core.SystemModule;
import com.cleandev.cli.io.Console;
import com.cleandev.cli.io.ScreenFormatter;
import com.cleandev.cli.io.TableRenderer;
import edu.unimag.controlador.ControladorPlanEstudioMateria;
import edu.unimag.dto.materia.MateriaDto;
import edu.unimag.dto.planestudio.PlanEstudioDto;
import edu.unimag.dto.planestudiomateria.PlanEstudioMateriaActualizarDto;
import edu.unimag.dto.planestudiomateria.PlanEstudioMateriaCrearDto;
import edu.unimag.dto.planestudiomateria.PlanEstudioMateriaDto;
import java.util.List;

public class PlanEstudioMateriaVista implements SystemModule {

    private final ControladorPlanEstudioMateria controlador;

    public PlanEstudioMateriaVista(ControladorPlanEstudioMateria controlador) {
        if (controlador == null) {
            throw new IllegalArgumentException("El controlador no puede ser nulo");
        }
        this.controlador = controlador;
    }

    @Override
    public String getModuleName() {
        return "Materias por plan de estudio";
    }

    @Override
    public void execute(Console console, ScreenFormatter formatter, TableRenderer renderer) {
        mostrarMenu(console, formatter);
    }

    private void mostrarMenu(Console console, ScreenFormatter formatter) {
        boolean ejecutar = true;
        while (ejecutar) {
            console.showMenu(
                    "MODULO DE MATERIAS POR PLAN DE ESTUDIO",
                    "1. Asignar materia a un plan",
                    "2. Listar materias de un plan",
                    "3. Listar planes que contienen una materia",
                    "4. Actualizar semestre u orden de una materia",
                    "5. Quitar materia de un plan",
                    "6. Buscar una asignacion",
                    "7. Listar todas las asignaciones",
                    "0. Regresar al menu anterior"
            );

            String opcion = console.readText("Seleccione una opcion");
            switch (opcion) {
                case "1" ->
                    asignar(console, formatter);
                case "2" ->
                    listarPorPlan(console, formatter);
                case "3" ->
                    listarPorMateria(console, formatter);
                case "4" ->
                    actualizar(console, formatter);
                case "5" ->
                    quitar(console, formatter);
                case "6" ->
                    buscar(console, formatter);
                case "7" ->
                    listarTodos(console, formatter);
                case "0" ->
                    ejecutar = false;
                default ->
                    console.showError("Opcion incorrecta");
            }
        }
    }

    private void asignar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- ASIGNAR MATERIA A UN PLAN DE ESTUDIO ---");

        Long idPlanEstudio = leerPlanValidado(console);
        if (idPlanEstudio == null) {
            return;
        }
        Integer idMateria = leerMateriaValidada(console);
        if (idMateria == null) {
            return;
        }

        Integer semestre = console.readInteger("Semestre dentro del plan (1 a 20)");
        if (semestre == null) {
            return;
        }
        Integer orden = console.readInteger("Orden de la materia (1 a 100)");
        if (orden == null) {
            return;
        }

        try {
            PlanEstudioMateriaCrearDto request = new PlanEstudioMateriaCrearDto(
                    idPlanEstudio,
                    idMateria,
                    semestre,
                    orden
            );
            PlanEstudioMateriaDto response = controlador.asignar(request);

            console.showMessage("\nMateria asignada al plan correctamente.");
            mostrarDetallado(console, formatter, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listarPorPlan(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR MATERIAS DE UN PLAN ---");
        Long idPlanEstudio = console.readLong("ID del plan de estudio");
        if (idPlanEstudio == null) {
            return;
        }
        try {
            mostrarLista(console, formatter, controlador.listarPorPlan(idPlanEstudio));
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar asignaciones: " + e.getMessage());
        }
    }

    private void listarPorMateria(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTAR PLANES QUE CONTIENEN UNA MATERIA ---");
        Integer idMateria = console.readInteger("ID de la materia");
        if (idMateria == null) {
            return;
        }
        try {
            mostrarLista(console, formatter, controlador.listarPorMateria(idMateria));
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar asignaciones: " + e.getMessage());
        }
    }

    private void actualizar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- ACTUALIZAR SEMESTRE U ORDEN ---");

        Long idPlanEstudio = leerPlanValidado(console);
        if (idPlanEstudio == null) {
            return;
        }
        Integer idMateria = leerMateriaValidada(console);
        if (idMateria == null) {
            return;
        }

        try {
            PlanEstudioMateriaDto actual = controlador.buscarPorId(idPlanEstudio, idMateria);
            console.showMessage("\nAsignacion actual:");
            mostrarDetallado(console, formatter, actual);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
            return;
        }

        Integer nuevoSemestre = console.readInteger("Nuevo semestre (deje vacio para no cambiar)");
        Integer nuevoOrden = console.readInteger("Nuevo orden (deje vacio para no cambiar)");
        if (nuevoSemestre == null && nuevoOrden == null) {
            console.showMessage("Operacion cancelada: no se indico ningun cambio.");
            return;
        }

        try {
            PlanEstudioMateriaActualizarDto request = new PlanEstudioMateriaActualizarDto(
                    idPlanEstudio,
                    idMateria,
                    nuevoSemestre,
                    nuevoOrden
            );
            PlanEstudioMateriaDto response = controlador.actualizar(request);

            console.showMessage("\nAsignacion actualizada correctamente.");
            mostrarDetallado(console, formatter, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void quitar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- QUITAR MATERIA DE UN PLAN ---");

        Long idPlanEstudio = console.readLong("ID del plan de estudio");
        if (idPlanEstudio == null) {
            return;
        }
        Integer idMateria = console.readInteger("ID de la materia");
        if (idMateria == null) {
            return;
        }

        try {
            PlanEstudioMateriaDto actual = controlador.buscarPorId(idPlanEstudio, idMateria);
            mostrarDetallado(console, formatter, actual);

            if (!console.readConfirmation("Confirma que desea quitar la materia del plan?")) {
                console.showMessage("Operacion cancelada por el usuario.");
                return;
            }

            controlador.quitar(idPlanEstudio, idMateria);
            console.showMessage("\nMateria quitada del plan correctamente.");
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void buscar(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- BUSCAR ASIGNACION ---");

        Long idPlanEstudio = console.readLong("ID del plan de estudio");
        if (idPlanEstudio == null) {
            return;
        }
        Integer idMateria = console.readInteger("ID de la materia");
        if (idMateria == null) {
            return;
        }

        try {
            PlanEstudioMateriaDto response = controlador.buscarPorId(idPlanEstudio, idMateria);
            mostrarDetallado(console, formatter, response);
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error del sistema: " + e.getMessage());
        }
    }

    private void listarTodos(Console console, ScreenFormatter formatter) {
        console.showMessage("\n--- LISTADO DE ASIGNACIONES PLAN-MATERIA ---");
        try {
            mostrarLista(console, formatter, controlador.listarTodos());
        } catch (IllegalArgumentException | IllegalStateException e) {
            console.showError("Error de validacion: " + e.getMessage());
        } catch (RuntimeException e) {
            console.showError("Error al listar asignaciones: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Apoyo
    // ------------------------------------------------------------------
    private Long leerPlanValidado(Console console) {
        Long idPlanEstudio = console.readLong("ID del plan de estudio");
        if (idPlanEstudio == null) {
            return null;
        }
        try {
            PlanEstudioDto plan = controlador.buscarPlanEstudio(idPlanEstudio);
            console.showMessage("Plan: " + plan.version());
            console.showMessage("Programa: " + plan.programa().nombre());
            console.showMessage("Facultad: " + plan.programa().facultad().nombre());
            return idPlanEstudio;
        } catch (IllegalArgumentException e) {
            console.showError("No existe el plan de estudio indicado.");
            return null;
        }
    }

    private Integer leerMateriaValidada(Console console) {
        Integer idMateria = console.readInteger("ID de la materia");
        if (idMateria == null) {
            return null;
        }
        try {
            MateriaDto materia = controlador.buscarMateria(idMateria);
            console.showMessage("Materia: " + materia.nombre());
            console.showMessage("Creditos: " + materia.creditos());
            return idMateria;
        } catch (IllegalArgumentException e) {
            console.showError("No existe la materia indicada.");
            return null;
        }
    }

    private void mostrarLista(Console console, ScreenFormatter formatter, List<PlanEstudioMateriaDto> lista) {
        if (lista.isEmpty()) {
            console.showMessage("No hay asignaciones para mostrar.");
            return;
        }
        console.showMessage("Total de asignaciones: " + lista.size());
        console.showMessage(formatter.separator());
        for (PlanEstudioMateriaDto dto : lista) {
            mostrarResumen(console, formatter, dto);
            console.showMessage(formatter.separator());
        }
    }

    private void mostrarResumen(Console console, ScreenFormatter formatter, PlanEstudioMateriaDto dto) {
        console.showMessage("Plan: " + dto.planEstudio().version()
                + " (ID " + dto.planEstudio().id() + ")");
        console.showMessage("   Programa: " + dto.planEstudio().programa().nombre());
        console.showMessage("   Materia: " + dto.materia().nombre()
                + " (ID " + dto.materia().id() + ")");
        console.showMessage("   Semestre: " + dto.semestre());
        console.showMessage("   Orden: " + dto.orden());
    }

    private void mostrarDetallado(Console console, ScreenFormatter formatter, PlanEstudioMateriaDto dto) {
        console.showMessage("   Plan: " + dto.planEstudio().version()
                + " (ID " + dto.planEstudio().id() + ")");
        console.showMessage("   Estado del plan: " + dto.planEstudio().estado().getDescription());
        console.showMessage("   Programa: " + dto.planEstudio().programa().nombre());
        console.showMessage("   Facultad: " + dto.planEstudio().programa().facultad().nombre());
        console.showMessage("   Nivel: " + dto.planEstudio().programa().nivelPrograma().getDescription());
        console.showMessage("   Materia: " + dto.materia().nombre()
                + " (ID " + dto.materia().id() + ")");
        console.showMessage("   Creditos: " + dto.materia().creditos());
        console.showMessage("   Cupo maximo: " + dto.materia().cupoMaximo());
        console.showMessage("   Semestre: " + dto.semestre());
        console.showMessage("   Orden: " + dto.orden());
    }
}
