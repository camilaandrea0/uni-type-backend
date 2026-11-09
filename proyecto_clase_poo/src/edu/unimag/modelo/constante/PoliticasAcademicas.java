package edu.unimag.modelo.constante;

import java.time.format.DateTimeFormatter;

public final class PoliticasAcademicas {

    private PoliticasAcademicas() {
        throw new UnsupportedOperationException("Constantes de negocio, no instanciar");
    }

    public static final Integer MIN_CREDITOS_MATERIA = 1;
    public static final Integer MAX_CREDITOS_MATERIA = 6;
    public static final Integer MIN_CUPO_MATERIA = 1;
    public static final Integer MAX_CUPO_MATERIA = 50;

    public static final Integer NOTA_MINIMA = 0;
    public static final Integer NOTA_MAXIMA = 500;
    public static final Integer NOTA_APROBACION = 300;

    public static final String PATRON_AUDITORIA = "yyyy-MM-dd HH:mm";
    public static final DateTimeFormatter FORMATO_AUDITORIA
            = DateTimeFormatter.ofPattern(PATRON_AUDITORIA);
}
