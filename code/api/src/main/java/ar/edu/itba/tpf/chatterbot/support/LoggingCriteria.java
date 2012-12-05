package ar.edu.itba.tpf.chatterbot.support;

import java.util.Date;

/**
 * Clase de soporte para búsquedas en reportes a partir de palabras clave e intervalo de fechas.
 */
public class LoggingCriteria extends IntervalCriteria {

    private String keywords;

    /**
     * Crea un nuevo criterio de búsqueda.
     * 
     * @param from Fecha inicial.
     * @param to Fecha final.
     * @param keywords Palabras clave.
     */
    public LoggingCriteria(Date from, Date to, String keywords) {
        super(from, to);
        this.keywords = keywords;
    }

    /**
     * @return Palabras clave.
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * @param keywords Palabras clave.
     * @return this.
     */
    public LoggingCriteria setKeywords(String keywords) {
        this.keywords = keywords;
        return this;
    }
}
