package edu.unimag.config;

import java.nio.file.Path;
import java.nio.file.Paths;

public enum RutaPersistencia {
    PROFESORES("profesores.txt"),
    FACULTADES("facultades.txt"),
    ESTUDIANTES("estudiantes.txt"),
    FORMACIONES_PROFESORES("formaciones_profesores.txt"),
    MATERIAS("materias.txt"),
    PERIODOS_ACADEMICOS("periodos.txt"),
    CURSOS_PERIODOS("cursos_periodos.txt"),
    CURSOS_PROFESORES("cursos_profesores.txt"),
    PROGRAMAS("programas.txt"),
    PLANES_ESTUDIO("planes_estudio.txt");

    private final Path ruta;

    RutaPersistencia(String nombreArchivo) {
        this.ruta = Paths.get(
                System.getProperty("user.dir"),
                "misPersistencias",
                nombreArchivo
        );
    }

    public Path obtenerRuta() {
        return ruta;
    }
}
