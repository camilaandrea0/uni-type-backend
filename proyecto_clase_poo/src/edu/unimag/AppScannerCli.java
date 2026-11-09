package edu.unimag;

import com.cleandev.cli.config.CliConfig;

import edu.unimag.vista.constante.ConfiguracionCli;

import java.util.Locale;

public class AppScannerCli {

    public static CliConfig escenario_SinConfiguracion() {
        return CliConfig.defaultConfig();
    }

    public static CliConfig escenario_SoloIdioma() {
        return CliConfig.defaultConfig()
                .toBuilder()
                .locale(ConfiguracionCli.LOCALE)
                .build();
    }

    public static CliConfig escenario_IdiomaYFechas() {
        return CliConfig.defaultConfig()
                .toBuilder()
                .locale(ConfiguracionCli.LOCALE)
                .inputDateFormat(ConfiguracionCli.FORMATO_FECHA_ENTRADA)
                .visualDateFormat(ConfiguracionCli.FORMATO_FECHA_VISUAL)
                .visualDateTimeFormat(ConfiguracionCli.FORMATO_FECHA_HORA_VISUAL)
                .build();
    }

    public static CliConfig escenario_ConfiguracionCompleta() {
        return CliConfig.defaultConfig()
                .toBuilder()
                .systemTitle(ConfiguracionCli.TITULO_SISTEMA)
                .exitMessage(ConfiguracionCli.MENSAJE_SALIDA)
                .separatorChar(ConfiguracionCli.CARACTER_SEPARADOR)
                .separatorLength(ConfiguracionCli.LONGITUD_SEPARADOR)
                .errorPrefix(ConfiguracionCli.PREFIJO_ERROR)
                .defaultNullText(ConfiguracionCli.TEXTO_NULO)
                .defaultYesText(ConfiguracionCli.TEXTO_SI)
                .defaultNoText(ConfiguracionCli.TEXTO_NO)
                .inputDateFormat(ConfiguracionCli.FORMATO_FECHA_ENTRADA)
                .visualDateFormat(ConfiguracionCli.FORMATO_FECHA_VISUAL)
                .visualDateTimeFormat(ConfiguracionCli.FORMATO_FECHA_HORA_VISUAL)
                .locale(ConfiguracionCli.LOCALE)
                .build();
    }

    public static CliConfig escenario_PersonalizarConfirmaciones() {
        return CliConfig.defaultConfig()
                .toBuilder()
                .locale(ConfiguracionCli.LOCALE)
                .defaultYesText("Aceptar")
                .defaultNoText("Cancelar")
                .defaultNullText("No disponible")
                .build();
    }

    public static CliConfig escenario_InspectarDefaults() {
        CliConfig configDefault = CliConfig.defaultConfig();
        System.out.println("\n=== DEFAULT CONFIGURATION (ENGLISH) ===");
        configDefault.printActiveConfiguration();

        CliConfig configEspanol = CliConfig.defaultConfig()
                .toBuilder()
                .locale(ConfiguracionCli.LOCALE)
                .build();
        System.out.println("\n=== CONFIGURACION EN ESPANIOL ===");
        configEspanol.printActiveConfiguration();

        System.out.println("\n=== FIN DE LA INSPECCION (no se arranca el motor) ===");
        System.exit(0);
        return null;
    }

    public static CliConfig escenario_Aleman() {
        return CliConfig.defaultConfig()
                .toBuilder()
                .locale(Locale.GERMANY)
                .build();
    }
}
