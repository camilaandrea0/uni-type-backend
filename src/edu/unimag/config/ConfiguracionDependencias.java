package edu.unimag.config;

import edu.unimag.config.modulos.api.ModuloConfigurable;
import com.cleandev.cli.core.SystemModule;
import edu.unimag.config.modulos.*;

import java.util.ArrayList;
import java.util.List;

public class ConfiguracionDependencias implements AutoCloseable {

    private final List<ModuloConfigurable> modulosConfigurados = new ArrayList<>();
    private final List<SystemModule> modulos = new ArrayList<>();

    public ConfiguracionDependencias() {

        // NIVEL 1: MÓDULOS INDEPENDIENTES (entidades base sin dependencias)
        // *********************************************************************
        // Profesor: entidad base con Long
        ConfiguracionModuloProfesor configProfesor = new ConfiguracionModuloProfesor();
        modulosConfigurados.add(configProfesor);
        modulos.add(configProfesor.construirVista());

        // Estudiante: entidad base con UUID
        ConfiguracionModuloEstudiante configEstudiante = new ConfiguracionModuloEstudiante();
        modulosConfigurados.add(configEstudiante);
        modulos.add(configEstudiante.construirVista());

        // Facultad: entidad base con Long
        ConfiguracionModuloFacultad configFacultad = new ConfiguracionModuloFacultad();
        modulosConfigurados.add(configFacultad);
        modulos.add(configFacultad.construirVista());

        // Materia: entidad base con Integer
        ConfiguracionModuloMateria configMateria = new ConfiguracionModuloMateria();
        modulosConfigurados.add(configMateria);
        modulos.add(configMateria.construirVista());

        // PeriodoAcademico: entidad base con Integer
        ConfiguracionModuloPeriodoAcademico configPeriodo = new ConfiguracionModuloPeriodoAcademico();
        modulosConfigurados.add(configPeriodo);
        modulos.add(configPeriodo.construirVista());
        // *********************************************************************

        // NIVEL 2: MÓDULOS CON 1 DEPENDENCIA (relaciones simples)
        // *********************************************************************
        // FormacionProfesor tiene Profesor (relación 1:N)
        ConfiguracionModuloFormacionProfesor configFormacion = new ConfiguracionModuloFormacionProfesor(
                configProfesor.getProfesorRepositorio()
        );
        modulosConfigurados.add(configFormacion);
        modulos.add(configFormacion.construirVista());

        // Programa tiene Facultad (relación mutable con cambio de facultad)
        ConfiguracionModuloPrograma configPrograma = new ConfiguracionModuloPrograma(
                configFacultad.getFacultadRepositorio()
        );
        modulosConfigurados.add(configPrograma);
        modulos.add(configPrograma.construirVista());

        // NIVEL 3: MÓDULOS CON 2 DEPENDENCIAS (cadena bifurcada simple)
        // *********************************************************************
        // CursoPeriodo tiene Materia y PeriodoAcademico (cadena bifurcada de nivel 2)
        ConfiguracionModuloCursoPeriodo configCursoPeriodo = new ConfiguracionModuloCursoPeriodo(
                configMateria.getMateriaRepositorio(),
                configPeriodo.getPeriodoAcademicoRepositorio()
        );
        modulosConfigurados.add(configCursoPeriodo);
        modulos.add(configCursoPeriodo.construirVista());

        // NIVEL 4: MÓDULOS CON 3 DEPENDENCIAS
        // PlanEstudio tiene Programa y Facultad (cadena de hidratación de 2 niveles)
        ConfiguracionModuloPlanEstudio configPlanEstudio = new ConfiguracionModuloPlanEstudio(
                configPrograma.getProgramaRepositorio(),
                configPrograma.getProgramaServicio(),
                configFacultad.getFacultadRepositorio()
        );
        modulosConfigurados.add(configPlanEstudio);
        modulos.add(configPlanEstudio.construirVista());

        // NIVEL 5: MÓDULOS CON PK COMPUESTA
        // *********************************************************************
        ConfiguracionModuloCursoProfesor configCursoProfesor= new ConfiguracionModuloCursoProfesor(
                configCursoPeriodo.getCursoPeriodoRepositorio(),
                configCursoPeriodo.getCursoPeriodoServicio(),
                configProfesor.getProfesorRepositorio(),
                configProfesor.getProfesorServicio(),
                configMateria.getMateriaRepositorio(), 
                configPeriodo.getPeriodoAcademicoRepositorio());
        
        modulosConfigurados.add(configCursoProfesor);
        modulos.add(configCursoProfesor.construirVista());

        // NIVEL 6: MODULOS ACADEMICOS CON PK COMPUESTA
        // *********************************************************************
        // Inscripcion de estudiantes a cursos (curso periodo + estudiante)
        ConfiguracionModuloCursoEstudiante configCursoEstudiante = new ConfiguracionModuloCursoEstudiante(
                configEstudiante.getEstudianteRepositorio(),
                configEstudiante.getEstudianteServicio(),
                configCursoPeriodo.getCursoPeriodoRepositorio(),
                configCursoPeriodo.getCursoPeriodoServicio(),
                configMateria.getMateriaRepositorio(),
                configPeriodo.getPeriodoAcademicoRepositorio()
        );
        modulosConfigurados.add(configCursoEstudiante);
        modulos.add(configCursoEstudiante.construirVista());

        // Materias asignadas a un plan de estudio (plan de estudio + materia)
        ConfiguracionModuloPlanEstudioMateria configPlanEstudioMateria = new ConfiguracionModuloPlanEstudioMateria(
                configPlanEstudio.getPlanEstudioRepositorio(),
                configPlanEstudio.getPlanEstudioServicio(),
                configMateria.getMateriaRepositorio(),
                configMateria.getMateriaServicio(),
                configPrograma.getProgramaRepositorio(),
                configFacultad.getFacultadRepositorio()
        );
        modulosConfigurados.add(configPlanEstudioMateria);
        modulos.add(configPlanEstudioMateria.construirVista());

        // Monitorias academicas (curso periodo + estudiante + profesor)
        ConfiguracionModuloMonitoria configMonitoria = new ConfiguracionModuloMonitoria(
                configCursoPeriodo.getCursoPeriodoRepositorio(),
                configCursoPeriodo.getCursoPeriodoServicio(),
                configEstudiante.getEstudianteRepositorio(),
                configEstudiante.getEstudianteServicio(),
                configProfesor.getProfesorRepositorio(),
                configProfesor.getProfesorServicio(),
                configMateria.getMateriaRepositorio(),
                configPeriodo.getPeriodoAcademicoRepositorio()
        );
        modulosConfigurados.add(configMonitoria);
        modulos.add(configMonitoria.construirVista());
    } 

    public List<SystemModule> getModulos() {
        return modulos;
    }

    @Override
    public void close() {
        for (int i = modulosConfigurados.size() - 1; i >= 0; i--) {
            ModuloConfigurable modulo = modulosConfigurados.get(i);
            try {
                modulo.cerrarRecursos();
            } catch (Exception e) {
                System.err.println("ADVERTENCIA: Error cerrando modulo "
                        + modulo.getClass().getSimpleName() + ": " + e.getMessage());
            }
        }
        modulosConfigurados.clear();
    }
}
