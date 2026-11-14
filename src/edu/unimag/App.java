package edu.unimag;

import com.cleandev.cli.config.CliConfig;
import com.cleandev.cli.core.CliEngine;
import com.cleandev.cli.core.SystemModule;

import edu.unimag.config.ConfiguracionDependencias;

import java.util.List;

public class App {

    public static void main(String[] args) {
        try {
            iniciarAplicacion();
        } catch (Exception e) {
            System.err.println("ERROR: No se puede iniciar la App");
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    private static void iniciarAplicacion() {
        CliConfig cliConfig = AppScannerCli.escenario_ConfiguracionCompleta();
        CliEngine motorConsola = new CliEngine(cliConfig);
        try (ConfiguracionDependencias config = new ConfiguracionDependencias()) {
            registrarModulos(motorConsola, config);
            motorConsola.start();
        }
    }

    private static void registrarModulos(CliEngine engine, ConfiguracionDependencias config) {
        List<SystemModule> modulos = config.getModulos();
        for (SystemModule modulo : modulos) {
            engine.registerModule(modulo);
        }
    }
}
