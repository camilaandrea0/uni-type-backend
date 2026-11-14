package edu.unimag.web;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import edu.unimag.config.modulos.*;
import edu.unimag.config.modulos.api.ModuloConfigurable;
import edu.unimag.dto.cursoPorfesor.*;
import edu.unimag.dto.cursoperiodo.*;
import edu.unimag.dto.estudiante.*;
import edu.unimag.dto.facultad.*;
import edu.unimag.dto.formacion.*;
import edu.unimag.dto.materia.*;
import edu.unimag.dto.periodoacademico.*;
import edu.unimag.dto.planestudio.*;
import edu.unimag.dto.profesor.*;
import edu.unimag.dto.programa.*;
import edu.unimag.modelo.enumeracion.*;
import edu.unimag.servicio.*;
import java.awt.Desktop;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.DateTimeException;
import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.util.*;

/**
 * Servidor web minimo: usa solo el HttpServer que trae el JDK. Cada modulo de
 * la pagina se atiende con tres rutas: GET /api/{modulo} -> lista todos POST
 * /api/{modulo} -> crea PUT /api/{modulo}/{id} -> actualiza Los datos de POST y
 * PUT llegan como formulario (clave=valor&clave=valor). La respuesta es JSON
 * escrito a mano. Ademas sirve la pagina web (carpeta "web") y la abre en el
 * navegador. Ejecute esta clase en lugar de App.java (no las dos a la vez).
 *
 * Rutas adicionales:
 *   POST   /api/{modulo}/{id}/activar     -> activa / reactiva el registro
 *   POST   /api/{modulo}/{id}/desactivar  -> desactiva / suspende / retira el registro
 *   DELETE /api/{modulo}/{id}             -> elimina (solo cursos y formacion)
 * Cualquier error de validacion de los servicios (IllegalArgumentException,
 * IllegalStateException...) se responde como JSON {"error":"mensaje"} con codigo 400.
 */
public class ServidorWeb {

    private static final int PUERTO = 8080;

    private static ProfesorServicio profesores;
    private static EstudianteServicio estudiantes;
    private static FacultadServicio facultades;
    private static ProgramaServicio programas;
    private static PlanEstudioServicio planes;
    private static MateriaServicio materias;
    private static PeriodoAcademicoServicio periodos;
    private static CursoPeriodoServicio cursos;
    private static CursoProfesorServicio asignaciones;
    private static FormacionProfesorServicio formaciones;

    // Carpeta con index.html, modulo.html y assets/ (junto a misPersistencias)
    private static final Path CARPETA_WEB = Paths.get("web");

    public static void main(String[] args) throws IOException {
        iniciarServicios();

        HttpServer servidor = HttpServer.create(new InetSocketAddress(PUERTO), 0);
        servidor.createContext("/api", ServidorWeb::atender);
        servidor.createContext("/", ServidorWeb::servirArchivo);
        servidor.start(); // un solo hilo: las peticiones se atienden de a una
        System.out.println("Servidor listo en http://localhost:" + PUERTO);

        abrirNavegador();
    }

    private static void abrirNavegador() {
        if (!Files.isDirectory(CARPETA_WEB)) {
            System.out.println("No existe la carpeta " + CARPETA_WEB.toAbsolutePath()
                    + ". Copie alli index.html, modulo.html y assets/ para ver la pagina.");
            return;
        }
        try {
            // HeadlessException y UnsupportedOperationException no son IOException:
            // se atrapan todas para que el servidor siga funcionando aunque no abra el navegador
            Desktop.getDesktop().browse(URI.create("http://localhost:" + PUERTO + "/"));
        } catch (Exception e) {
            System.out.println("Abra http://localhost:" + PUERTO + "/ en el navegador.");
        }
    }

    // ------------------------------------------------------------------
    // Armado de los servicios (igual que ConfiguracionDependencias)
    // ------------------------------------------------------------------
    private static void iniciarServicios() {
        ConfiguracionModuloProfesor cProfesor = new ConfiguracionModuloProfesor();
        ConfiguracionModuloEstudiante cEstudiante = new ConfiguracionModuloEstudiante();
        ConfiguracionModuloFacultad cFacultad = new ConfiguracionModuloFacultad();
        ConfiguracionModuloMateria cMateria = new ConfiguracionModuloMateria();
        ConfiguracionModuloPeriodoAcademico cPeriodo = new ConfiguracionModuloPeriodoAcademico();
        ConfiguracionModuloFormacionProfesor cFormacion
                = new ConfiguracionModuloFormacionProfesor(cProfesor.getProfesorRepositorio());
        ConfiguracionModuloPrograma cPrograma
                = new ConfiguracionModuloPrograma(cFacultad.getFacultadRepositorio());
        ConfiguracionModuloCursoPeriodo cCurso = new ConfiguracionModuloCursoPeriodo(
                cMateria.getMateriaRepositorio(), cPeriodo.getPeriodoAcademicoRepositorio());
        ConfiguracionModuloPlanEstudio cPlan = new ConfiguracionModuloPlanEstudio(
                cPrograma.getProgramaRepositorio(), cPrograma.getProgramaServicio(),
                cFacultad.getFacultadRepositorio());
        ConfiguracionModuloCursoProfesor cCursoProfesor = new ConfiguracionModuloCursoProfesor(
                cCurso.getCursoPeriodoRepositorio(), cCurso.getCursoPeriodoServicio(),
                cProfesor.getProfesorRepositorio(), cProfesor.getProfesorServicio(),
                cMateria.getMateriaRepositorio(), cPeriodo.getPeriodoAcademicoRepositorio());

        List<ModuloConfigurable> modulos = List.of(cProfesor, cEstudiante, cFacultad, cMateria,
                cPeriodo, cFormacion, cPrograma, cCurso, cPlan, cCursoProfesor);

        profesores = cProfesor.getProfesorServicio();
        estudiantes = cEstudiante.getEstudianteServicio();
        facultades = cFacultad.getFacultadServicio();
        programas = cPrograma.getProgramaServicio();
        planes = cPlan.getPlanEstudioServicio();
        materias = cMateria.getMateriaServicio();
        periodos = cPeriodo.getPeriodoAcademicoServicio();
        cursos = cCurso.getCursoPeriodoServicio();
        asignaciones = cCursoProfesor.getCursoProfesorServicio();
        formaciones = cFormacion.getFormacionProfesorServicio();

        // Al cerrar el programa se cierran los archivos, en orden inverso
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            for (int i = modulos.size() - 1; i >= 0; i--) {
                try {
                    modulos.get(i).cerrarRecursos();
                } catch (Exception e) {
                    /* se ignora al cerrar */ }
            }
        }));
    }

    // ------------------------------------------------------------------
    // Pagina web: entrega los archivos de la carpeta "web"
    // ------------------------------------------------------------------
    private static void servirArchivo(HttpExchange ex) throws IOException {
        String ruta = ex.getRequestURI().getPath();
        if (ruta.equals("/")) {
            ruta = "/index.html";
        }

        Path base = CARPETA_WEB.toAbsolutePath().normalize();
        Path archivo = base.resolve(ruta.substring(1)).normalize();
        // startsWith(base) evita que pidan archivos fuera de la carpeta (../)
        if (!archivo.startsWith(base) || !Files.isRegularFile(archivo)) {
            byte[] aviso = "No encontrado".getBytes(StandardCharsets.UTF_8);
            ex.sendResponseHeaders(404, aviso.length);
            try (OutputStream salida = ex.getResponseBody()) {
                salida.write(aviso);
            }
            return;
        }

        String nombre = archivo.getFileName().toString().toLowerCase();
        String tipo = "application/octet-stream";
        if (nombre.endsWith(".html")) {
            tipo = "text/html; charset=utf-8";
        } else if (nombre.endsWith(".css")) {
            tipo = "text/css; charset=utf-8";
        } else if (nombre.endsWith(".js")) {
            tipo = "text/javascript; charset=utf-8";
        } else if (nombre.endsWith(".svg")) {
            tipo = "image/svg+xml";
        } else if (nombre.endsWith(".png")) {
            tipo = "image/png";
        } else if (nombre.endsWith(".jpg") || nombre.endsWith(".jpeg")) {
            tipo = "image/jpeg";
        } else if (nombre.endsWith(".ico")) {
            tipo = "image/x-icon";
        }

        byte[] bytes = Files.readAllBytes(archivo);
        ex.getResponseHeaders().add("Content-Type", tipo);
        ex.getResponseHeaders().add("Cache-Control", "no-cache");
        ex.sendResponseHeaders(200, bytes.length);
        try (OutputStream salida = ex.getResponseBody()) {
            salida.write(bytes);
        }
    }

    // ------------------------------------------------------------------
    // Atencion de cada peticion HTTP
    // ------------------------------------------------------------------
    private static void atender(HttpExchange ex) throws IOException {
        // Permite que la pagina (Live Server, puerto 5500) llame a este servidor (puerto 8080)
        ex.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
        ex.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, PUT, DELETE, OPTIONS");
        ex.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");

        String metodo = ex.getRequestMethod();
        if (metodo.equals("OPTIONS")) {
            try (ex) {
                ex.sendResponseHeaders(204, -1);
            }
            return;
        }

        int codigo = 200;
        String respuesta;
        try {
            // "", "api", modulo, id, accion  (ej: /api/estudiantes/<uuid>/desactivar)
            String[] partes = ex.getRequestURI().getPath().split("/");
            String modulo = partes.length > 2 ? partes[2] : "";
            String id = partes.length > 3 ? URLDecoder.decode(partes[3], StandardCharsets.UTF_8) : null;
            String accion = partes.length > 4 ? partes[4] : null;
            if (id != null && id.isBlank()) {
                id = null;
            }
            Map<String, String> datos = leerFormulario(ex);

            if (metodo.equals("GET") && id != null) {
                respuesta = aJson(obtener(modulo, id));            // un solo registro
            } else if (metodo.equals("GET")) {
                // los filtros llegan en la direccion: /api/profesores?estado=1
                respuesta = aJson(listar(modulo, parsear(ex.getRequestURI().getRawQuery())));
            } else if (metodo.equals("POST") && id == null) {
                crear(modulo, datos);
                respuesta = "{\"ok\":true}";
            } else if ((metodo.equals("POST") || metodo.equals("PUT")) && id != null && accion != null) {
                cambiarEstado(modulo, id, accion);                 // activar / desactivar
                respuesta = "{\"ok\":true}";
            } else if (metodo.equals("PUT") && id != null) {
                actualizar(modulo, id, datos);
                respuesta = "{\"ok\":true}";
            } else if (metodo.equals("DELETE") && id != null) {
                eliminar(modulo, id);
                respuesta = "{\"ok\":true}";
            } else {
                codigo = 405;
                respuesta = respuestaError("Metodo no permitido");
            }
        } catch (IllegalArgumentException | IllegalStateException e) {
            // Validaciones del proyecto (DTOs, servicios, modelos): el mensaje llega tal cual a la pagina.
            // NumberFormatException tambien cae aqui porque es una IllegalArgumentException.
            codigo = 400;
            respuesta = respuestaError(mensajeDe(e));
        } catch (UnsupportedOperationException e) {
            // Ej: "No se permite borrado fisico" en los repositorios que no lo admiten
            codigo = 400;
            respuesta = respuestaError(mensajeDe(e));
        } catch (RuntimeException | IOException e) {
            // Cualquier otro fallo inesperado: se muestra en la consola y se avisa a la pagina
            e.printStackTrace();
            codigo = 500;
            respuesta = respuestaError("Error del sistema: " + mensajeDe(e));
        }

        byte[] bytes = respuesta.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json; charset=utf-8");
        ex.sendResponseHeaders(codigo, bytes.length);
        try (OutputStream salida = ex.getResponseBody()) {
            salida.write(bytes);
        }
    }

    private static String mensajeDe(Exception e) {
        return e.getMessage() != null && !e.getMessage().isBlank() ? e.getMessage() : e.toString();
    }

    private static String respuestaError(String mensaje) {
        return "{\"error\":\"" + escapar(mensaje) + "\"}";
    }

    // ------------------------------------------------------------------
    // Cada DTO se convierte en una "fila" (clave -> texto) que viaja a la pagina
    // ------------------------------------------------------------------
    private static Map<String, String> filaProfesor(ProfesorDto p) {
        return fila("id", p.id(), "nombre", p.nombre(), "celular", p.celular(), "estado", codigo(p.estado()));
    }

    private static Map<String, String> filaEstudiante(EstudianteDto e) {
        return fila("id", e.id(), "codigo", e.codigo(), "nombre", e.nombre(), "telefono", e.celular(),
                "correo", e.correo(), "direccion", e.direccion(), "estado", codigo(e.estado()));
    }

    private static Map<String, String> filaFacultad(FacultadDto f) {
        return fila("id", f.id(), "nombre", f.nombre(), "estado", codigo(f.estado()));
    }

    private static Map<String, String> filaPrograma(ProgramaDto p) {
        return fila("id", p.id(), "nombre", p.nombre(), "nivel", p.nivelPrograma().getCode(),
                "facultad", p.facultad().id(), "estado", codigo(p.estado()));
    }

    private static Map<String, String> filaPlan(PlanEstudioDto p) {
        return fila("id", p.id(), "version", p.version(), "programa", p.programa().id(), "estado", codigo(p.estado()));
    }

    private static Map<String, String> filaMateria(MateriaDto m) {
        return fila("id", m.id(), "nombre", m.nombre(), "creditos", m.creditos(),
                "cupo", m.cupoMaximo(), "estado", codigo(m.estado()));
    }

    private static Map<String, String> filaPeriodo(PeriodoAcademicoDto p) {
        return fila("id", p.id(), "nombre", p.nombre(), "estado", codigo(p.estado()));
    }

    private static Map<String, String> filaCurso(CursoPeriodoDto c) {
        return fila("id", c.id(), "nombre", c.nombre(), "materia", c.materia().id(),
                "periodo", c.periodo().id(), "cupo", c.cupo());
    }

    private static Map<String, String> filaAsignacion(cursoProfesorDto a) {
        return fila("id", a.cursoPeriodo().id() + "_" + a.profesor().id(),
                "curso", a.cursoPeriodo().id(), "profesor", a.profesor().id(),
                "tipo", a.tipoAsignacion().getCode(), "estado", a.estado().getCode());
    }

    private static Map<String, String> filaFormacion(FormacionProfesorDto f) {
        return fila("id", f.id(), "profesor", f.profesor().id(), "titulo", f.titulo(),
                "fecha", f.fecha().toLocalDate());
    }

    // ------------------------------------------------------------------
    // LISTAR (con filtros opcionales). Cada filtro llama al metodo del servicio
    // que corresponde; si no hay filtro se lista todo.
    // ------------------------------------------------------------------
    private static List<Map<String, String>> listar(String modulo, Map<String, String> f) {
        List<Map<String, String>> filas = new ArrayList<>();
        switch (modulo) {
            case "profesores" ->  {
                String estado = valor(f, "estado"); // 1 = activos, 2 = inactivos
                List<ProfesorDto> lista = "1".equals(estado) ? profesores.listarActivos()
                        : "2".equals(estado) ? profesores.listarInactivos() : profesores.listarTodos();
                for (ProfesorDto p : lista) {
                    filas.add(filaProfesor(p));
                }
            }
            case "estudiantes" ->  {
                String estado = valor(f, "estado");
                List<EstudianteDto> lista = "1".equals(estado) ? estudiantes.listarActivos()
                        : "2".equals(estado) ? estudiantes.listarInactivos() : estudiantes.listarTodos();
                for (EstudianteDto e : lista) {
                    filas.add(filaEstudiante(e));
                }
            }
            case "facultades" -> {
                for (FacultadDto x : facultades.listarTodas()) {
                    filas.add(filaFacultad(x));
                }
            }
            case "programas" ->  {
                String facultad = valor(f, "facultad");
                List<ProgramaDto> lista = facultad != null
                        ? programas.listarPorFacultad(aLong(facultad)) : programas.listarTodos();
                for (ProgramaDto p : lista) {
                    filas.add(filaPrograma(p));
                }
            }
            case "planes" ->  {
                String programa = valor(f, "programa");
                List<PlanEstudioDto> lista = programa != null
                        ? planes.listarPorPrograma(aUuid(programa)) : planes.listarTodos();
                for (PlanEstudioDto p : lista) {
                    filas.add(filaPlan(p));
                }
            }
            case "materias" -> {
                for (MateriaDto m : materias.listarTodas()) {
                    filas.add(filaMateria(m));
                }
            }
            case "periodos" -> {
                for (PeriodoAcademicoDto p : periodos.listarTodos()) {
                    filas.add(filaPeriodo(p));
                }
            }
            case "cursos" -> {
                for (CursoPeriodoDto c : cursos.listarTodos()) {
                    filas.add(filaCurso(c));
                }
            }
            case "asignacion" ->  {
                String profesor = valor(f, "profesor");
                String materia = valor(f, "materia");
                String periodo = valor(f, "periodo");
                List<cursoProfesorDto> lista = new ArrayList<>();
                if (materia != null && periodo != null) {
                    lista = asignaciones.listarPorMateriaPeriodo(aInt(materia), aInt(periodo));
                } else if (materia != null) {
                    lista = asignaciones.listarPorMateria(aInt(materia));
                } else if (periodo != null) {
                    lista = asignaciones.listarPorPeriodoAc(aInt(periodo));
                } else if (profesor != null) {
                    lista = asignaciones.listarPorProfesor(aLong(profesor));
                } else // sin filtros: no hay "listar todas", se junta lo de cada profesor
                {
                    for (ProfesorDto p : profesores.listarTodos()) {
                        lista.addAll(asignaciones.listarPorProfesor(p.id()));
                    }
                }
                for (cursoProfesorDto a : lista) // si ademas se pidio un profesor, se descartan los demas
                {
                    if (profesor == null || a.profesor().id().toString().equals(profesor)) {
                        filas.add(filaAsignacion(a));
                    }
                }
            }
            case "formacion" ->  {
                String profesor = valor(f, "profesor");
                List<FormacionProfesorDto> lista = profesor != null
                        ? formaciones.listarFormacionesPorProfesor(aLong(profesor))
                        : formaciones.listarFormaciones();
                for (FormacionProfesorDto x : lista) {
                    filas.add(filaFormacion(x));
                }
            }
            default -> throw new IllegalArgumentException("Modulo desconocido: " + modulo);
        }
        return filas;
    }

    // ------------------------------------------------------------------
    // OBTENER un registro por su id (buscarPorId del servicio)
    // ------------------------------------------------------------------
    private static Map<String, String> obtener(String modulo, String id) {
        switch (modulo) {
            case "profesores" -> {
                return filaProfesor(profesores.buscarPorId(aLong(id)));
            }
            case "estudiantes" -> {
                return filaEstudiante(estudiantes.buscarPorId(aUuid(id)));
            }
            case "programas" -> {
                return filaPrograma(programas.buscarPorId(aUuid(id)));
            }
            case "planes" -> {
                return filaPlan(planes.buscarPorId(aLong(id)));
            }
            case "materias" -> {
                return filaMateria(materias.buscarPorId(aInt(id)));
            }
            case "cursos" -> {
                return filaCurso(cursos.buscarPorId(aUuid(id)));
            }
            default -> {
                // facultades, periodos, asignaciones y formaciones no tienen buscarPorId: se busca en la lista
                for (Map<String, String> fila : listar(modulo, new HashMap<>())) {
                    if (fila.get("id").equals(id)) {
                        return fila;
                    }
                }
                throw new IllegalArgumentException("No existe ese registro");
            }
        }
    }

    // ------------------------------------------------------------------
    // CREAR
    // ------------------------------------------------------------------
    private static void crear(String modulo, Map<String, String> d) {
        switch (modulo) {
            case "profesores" ->
                profesores.registrarProfesor(new ProfesorCrearDto(d.get("nombre"), d.get("celular")));
            case "estudiantes" ->
                estudiantes.registrarNuevoEstudiante(new EstudianteCrearDto(d.get("codigo"), d.get("nombre"),
                        d.get("correo"), d.get("telefono"), d.get("direccion")));
            case "facultades" ->
                facultades.registrarFacultad(new FacultadCrearDto(d.get("nombre")));
            case "programas" ->
                programas.registrar(new ProgramaCrearDto(d.get("nombre"), nivel(d.get("nivel")),
                        aLong(d.get("facultad"))));
            case "planes" ->
                planes.registrar(new PlanEstudioCrearDto(d.get("version"), aUuid(d.get("programa"))));
            case "materias" ->
                materias.registrarMateria(new MateriaCrearDto(d.get("nombre"),
                        aInt(d.get("creditos")), aInt(d.get("cupo"))));
            case "periodos" ->
                periodos.registrar(new PeriodoAcademicoCrearDto(d.get("nombre")));
            case "cursos" ->
                cursos.registrar(new CursoPeriodoCrearDto(d.get("nombre"), aInt(d.get("materia")),
                        aInt(d.get("periodo")), aInt(d.get("cupo"))));
            case "asignacion" ->
                asignaciones.asignar(new cursoProfesorCrearDto(aUuid(d.get("curso")),
                        aLong(d.get("profesor")), tipo(d.get("tipo"))));
            case "formacion" ->
                formaciones.registrarFormacion(new FormacionProfesorCrearDto(aLong(d.get("profesor")),
                        d.get("titulo"), fecha(d.get("fecha"))));
            default ->
                throw new IllegalArgumentException("Modulo desconocido: " + modulo);
        }
    }

    // ------------------------------------------------------------------
    // ACTUALIZAR (datos + estado). "estado" llega como 1 (activo) o 2 (inactivo).
    // Los servicios rechazan valores "iguales al actual", por eso solo se
    // envia lo que realmente cambio (lo demas va como null).
    // ------------------------------------------------------------------
    private static void actualizar(String modulo, String id, Map<String, String> d) {
        Map<String, String> actual = obtener(modulo, id);
        boolean quiereActivo = !"2".equals(d.get("estado"));
        boolean estaActivo = !"2".equals(actual.get("estado"));

        switch (modulo) {
            case "profesores" -> {
                String nombre = cambio(actual, d, "nombre");
                String celular = cambio(actual, d, "celular");
                String estado = cambio(actual, d, "estado");
                if (hayCambios(nombre, celular, estado)) {
                    profesores.actualizar(new ProfesorActualizarDto(aLong(id), nombre, celular, entero(estado)));
                }
            }
            case "estudiantes" -> {
                String correo = cambio(actual, d, "correo");
                String celular = cambio(actual, d, "telefono");
                String direccion = cambio(actual, d, "direccion");
                String estado = cambio(actual, d, "estado");
                if (hayCambios(correo, celular, direccion, estado)) {
                    estudiantes.actualizarDatos(new EstudianteActualizarDto(aUuid(id), correo,
                            celular, direccion, entero(estado)));
                }
            }
            case "facultades" -> {
                String nombre = cambio(actual, d, "nombre");
                if (nombre != null) {
                    facultades.actualizar(new FacultadActualizarDto(aLong(id), nombre));
                }
                if (quiereActivo && !estaActivo) {
                    facultades.reactivar(aLong(id));
                }
                if (!quiereActivo && estaActivo) {
                    facultades.suspender(aLong(id));
                }
            }
            case "programas" -> {
                String nombre = cambio(actual, d, "nombre");
                String nivel = cambio(actual, d, "nivel");
                String facultad = cambio(actual, d, "facultad");
                if (hayCambios(nombre, nivel, facultad)) {
                    programas.actualizar(new ProgramaActualizarDto(aUuid(id), nombre,
                            nivel == null ? null : nivel(nivel), facultad == null ? null : aLong(facultad)));
                }
                if (quiereActivo && !estaActivo) {
                    programas.reactivar(aUuid(id));
                }
                if (!quiereActivo && estaActivo) {
                    programas.suspender(aUuid(id));
                }
            }
            case "planes" -> {
                String version = cambio(actual, d, "version");
                if (version != null) {
                    planes.actualizar(new PlanEstudioActualizarDto(aLong(id), version));
                }
                if (quiereActivo && !estaActivo) {
                    planes.activar(aLong(id));
                }
                if (!quiereActivo && estaActivo) {
                    planes.inactivar(aLong(id));
                }
            }
            case "materias" -> {
                String nombre = cambio(actual, d, "nombre");
                String creditos = cambio(actual, d, "creditos");
                String cupo = cambio(actual, d, "cupo");
                if (hayCambios(nombre, creditos, cupo)) {
                    materias.actualizar(new MateriaActualizarDto(aInt(id), nombre,
                            entero(creditos), entero(cupo)));
                }
                if (quiereActivo && !estaActivo) {
                    materias.reactivar(aInt(id));
                }
                if (!quiereActivo && estaActivo) {
                    materias.suspender(aInt(id));
                }
            }
            case "periodos" -> {
                String nombre = cambio(actual, d, "nombre");
                if (nombre != null) {
                    periodos.actualizar(new PeriodoAcademicoActualizarDto(aInt(id), nombre));
                }
                if (quiereActivo && !estaActivo) {
                    periodos.reactivar(aInt(id));
                }
                if (!quiereActivo && estaActivo) {
                    periodos.suspender(aInt(id));
                }
            }
            case "cursos" -> {
                String nombre = cambio(actual, d, "nombre");
                String cupo = cambio(actual, d, "cupo");
                if (hayCambios(nombre, cupo)) {
                    cursos.actualizar(new CursoPeriodoActualizarDto(aUuid(id), nombre, entero(cupo)));
                }
            }
            case "asignacion" ->
                actualizarAsignacion(actual, d, quiereActivo, estaActivo);
            case "formacion" -> {
                String titulo = cambio(actual, d, "titulo");
                String fecha = cambio(actual, d, "fecha");
                if (hayCambios(titulo, fecha)) {
                    formaciones.actualizarFormacion(new FormacionProfesorActualizarDto(aLong(id), titulo,
                            fecha == null ? null : fecha(fecha)));
                }
            }
            default ->
                throw new IllegalArgumentException("Modulo desconocido: " + modulo);
        }
    }

    private static void actualizarAsignacion(Map<String, String> actual, Map<String, String> d,
            boolean quiereAsignado, boolean estaAsignado) {
        UUID curso = aUuid(actual.get("curso"));
        Long profesor = aLong(actual.get("profesor"));

        String tipoNuevo = cambio(actual, d, "tipo");
        if (tipoNuevo != null) {
            asignaciones.cambiarTipoAsignacion(new cursoProfesorActualizar(curso, profesor, tipo(tipoNuevo)));
        }

        if (quiereAsignado && !estaAsignado) {
            asignaciones.reactivar(curso, profesor);
        }
        if (!quiereAsignado && estaAsignado) {
            asignaciones.retirarProfesor(curso, profesor);
        }
    }

    // ------------------------------------------------------------------
    // ACTIVAR / DESACTIVAR (POST /api/{modulo}/{id}/activar o /desactivar)
    // Cada modulo usa el metodo de su servicio, igual que las vistas de consola:
    //   profesores   -> actualizar con nuevoEstado (el servicio no tiene activar/inactivar)
    //   estudiantes  -> activar / inactivar
    //   facultades, programas, materias, periodos -> reactivar / suspender
    //   planes       -> activar / inactivar
    //   asignacion   -> reactivar / retirarProfesor  (id = idCurso_idProfesor)
    // cursos y formacion no manejan estado.
    // ------------------------------------------------------------------
    private static void cambiarEstado(String modulo, String id, String accion) {
        boolean activar;
        if (accion.equals("activar")) {
            activar = true;
        } else if (accion.equals("desactivar")) {
            activar = false;
        } else {
            throw new IllegalArgumentException("Accion desconocida: " + accion);
        }

        switch (modulo) {
            case "profesores" -> {
                int estado = activar ? EstadoEntidad.ACTIVO.getCode() : EstadoEntidad.INACTIVO.getCode();
                profesores.actualizar(new ProfesorActualizarDto(aLong(id), null, null, estado));
            }
            case "estudiantes" -> {
                if (activar) {
                    estudiantes.activar(aUuid(id));
                } else {
                    estudiantes.inactivar(aUuid(id));
                }
            }
            case "facultades" -> {
                if (activar) {
                    facultades.reactivar(aLong(id));
                } else {
                    facultades.suspender(aLong(id));
                }
            }
            case "programas" -> {
                if (activar) {
                    programas.reactivar(aUuid(id));
                } else {
                    programas.suspender(aUuid(id));
                }
            }
            case "planes" -> {
                if (activar) {
                    planes.activar(aLong(id));
                } else {
                    planes.inactivar(aLong(id));
                }
            }
            case "materias" -> {
                if (activar) {
                    materias.reactivar(aInt(id));
                } else {
                    materias.suspender(aInt(id));
                }
            }
            case "periodos" -> {
                if (activar) {
                    periodos.reactivar(aInt(id));
                } else {
                    periodos.suspender(aInt(id));
                }
            }
            case "asignacion" -> {
                int corte = id.lastIndexOf('_'); // el id de la fila es "<uuid del curso>_<id del profesor>"
                if (corte < 0) {
                    throw new IllegalArgumentException("El ID de la asignacion no es valido");
                }
                UUID curso = aUuid(id.substring(0, corte));
                Long profesor = aLong(id.substring(corte + 1));
                if (activar) {
                    asignaciones.reactivar(curso, profesor);
                } else {
                    asignaciones.retirarProfesor(curso, profesor);
                }
            }
            case "cursos", "formacion" ->
                throw new IllegalArgumentException("Este modulo no maneja estados (activo/inactivo)");
            default ->
                throw new IllegalArgumentException("Modulo desconocido: " + modulo);
        }
    }

    // ------------------------------------------------------------------
    // ELIMINAR (DELETE /api/{modulo}/{id}). Solo existe en los servicios de
    // cursos por periodo (eliminar) y formacion de profesores (eliminarFormacion).
    // El resto de modulos no borra: se desactivan.
    // ------------------------------------------------------------------
    private static void eliminar(String modulo, String id) {
        boolean eliminado;
        switch (modulo) {
            case "cursos" ->
                eliminado = cursos.eliminar(aUuid(id));
            case "formacion" ->
                eliminado = formaciones.eliminarFormacion(aLong(id));
            case "profesores", "estudiantes", "facultades", "programas", "planes",
                    "materias", "periodos", "asignacion" ->
                throw new IllegalArgumentException("Este modulo no permite eliminar registros. Use desactivar.");
            default ->
                throw new IllegalArgumentException("Modulo desconocido: " + modulo);
        }
        if (!eliminado) {
            throw new IllegalStateException("No se pudo eliminar el registro (puede que ya no exista).");
        }
    }

    // ------------------------------------------------------------------
    // Utilidades pequeñas
    // ------------------------------------------------------------------
    /**
     * Valor de un filtro, o null si no vino o esta vacio.
     */
    private static String valor(Map<String, String> filtros, String clave) {
        String v = filtros.get(clave);
        return (v == null || v.isBlank()) ? null : v;
    }

    /**
     * Valor nuevo del campo, o null si es igual al que ya estaba guardado.
     */
    private static String cambio(Map<String, String> actual, Map<String, String> nuevo, String clave) {
        String valor = nuevo.get(clave);
        return (valor == null || valor.equals(actual.get(clave))) ? null : valor;
    }

    private static boolean hayCambios(String... valores) {
        for (String v : valores) {
            if (v != null) {
                return true;
            }
        }
        return false;
    }

    private static Integer entero(String texto) {
        return aInt(texto);
    }

    private static String codigo(EstadoEntidad e) {
        return String.valueOf(e.getCode());
    }

    private static NivelPrograma nivel(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null; // el DTO responde "El nivel académico es obligatorio"
        }
        for (NivelPrograma n : NivelPrograma.values()) {
            if (String.valueOf(n.getCode()).equals(codigo)) {
                return n;
            }
        }
        throw new IllegalArgumentException("Nivel de programa invalido");
    }

    private static TipoAsignacion tipo(String codigo) {
        if (codigo == null || codigo.isBlank()) {
            return null; // el DTO responde "La asignacion del profesor no pueder ser null"
        }
        for (TipoAsignacion t : TipoAsignacion.values()) {
            if (String.valueOf(t.getCode()).equals(codigo)) {
                return t;
            }
        }
        throw new IllegalArgumentException("Tipo de asignacion invalido");
    }

    private static OffsetDateTime fecha(String texto) {
        if (texto == null || texto.isBlank()) {
            return null; // el DTO responde "La fecha es obligatoria"
        }
        try {
            return LocalDate.parse(texto.trim()).atStartOfDay().atOffset(ZoneOffset.UTC);
        } catch (DateTimeException e) {
            throw new IllegalArgumentException("La fecha '" + texto + "' no es valida (use AAAA-MM-DD)");
        }
    }

    /**
     * Texto -> Long. Vacio = null (para que el DTO diga "el ID es obligatorio").
     * Si no es un numero se lanza IllegalArgumentException con un mensaje claro.
     */
    private static Long aLong(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return Long.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + texto + "' no es un numero entero valido");
        }
    }

    private static Integer aInt(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return Integer.valueOf(texto.trim());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("'" + texto + "' no es un numero entero valido");
        }
    }

    private static UUID aUuid(String texto) {
        if (texto == null || texto.isBlank()) {
            return null;
        }
        try {
            return UUID.fromString(texto.trim());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("'" + texto + "' no es un ID (UUID) valido");
        }
    }

    /**
     * fila("id", 1, "nombre", "Ana") -> {id=1, nombre=Ana}
     */
    private static Map<String, String> fila(Object... paresClaveValor) {
        Map<String, String> fila = new LinkedHashMap<>();
        for (int i = 0; i < paresClaveValor.length; i += 2) {
            Object valor = paresClaveValor[i + 1];
            fila.put(paresClaveValor[i].toString(), valor == null ? "" : valor.toString());
        }
        return fila;
    }

    private static Map<String, String> leerFormulario(HttpExchange ex) throws IOException {
        return parsear(new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8));
    }

    /**
     * Convierte "a=1&b=2" (valores codificados como URL) en un mapa. Sirve para
     * el cuerpo y para ?filtros.
     */
    private static Map<String, String> parsear(String texto) {
        Map<String, String> datos = new HashMap<>();
        if (texto == null || texto.isBlank()) {
            return datos;
        }
        for (String par : texto.split("&")) {
            String[] kv = par.split("=", 2);
            String clave = URLDecoder.decode(kv[0], StandardCharsets.UTF_8);
            String valor = kv.length > 1 ? URLDecoder.decode(kv[1], StandardCharsets.UTF_8) : "";
            datos.put(clave, valor.trim());
        }
        return datos;
    }

    /**
     * Convierte la lista de filas a texto JSON, sin librerias.
     */
    private static String aJson(List<Map<String, String>> filas) {
        StringBuilder sb = new StringBuilder("[");
        for (int i = 0; i < filas.size(); i++) {
            if (i > 0) {
                sb.append(",");
            }
            sb.append(aJson(filas.get(i)));
        }
        return sb.append("]").toString();
    }

    /**
     * Una sola fila: {"id":"1","nombre":"Ana"}
     */
    private static String aJson(Map<String, String> fila) {
        StringBuilder sb = new StringBuilder("{");
        int j = 0;
        for (Map.Entry<String, String> campo : fila.entrySet()) {
            if (j++ > 0) {
                sb.append(",");
            }
            sb.append("\"").append(escapar(campo.getKey())).append("\":\"")
                    .append(escapar(campo.getValue())).append("\"");
        }
        return sb.append("}").toString();
    }

    private static String escapar(String texto) {
        return texto.replace("\\", "\\\\").replace("\"", "\\\"")
                .replace("\n", "\\n").replace("\r", "\\r").replace("\t", "\\t");
    }
}
