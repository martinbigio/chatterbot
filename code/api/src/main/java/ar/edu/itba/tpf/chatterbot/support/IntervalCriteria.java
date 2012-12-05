package ar.edu.itba.tpf.chatterbot.support;

import java.util.Date;

/**
 * Clase de soporte para criterios de búsqueda en reportes.
 */
public class IntervalCriteria {
    private Date from;
    private Date to;

    /**
     * Crea un nuevo criterio de búsqueda completando la fecha de comienzo y fin.
     * 
     * @param from Fecha inicial.
     * @param to Fecha final.
     */
    public IntervalCriteria(Date from, Date to) {
        super();
        this.from = from;
        this.to = to;
    }

    /**
     * @return Fecha inicial.
     */
    public Date getFrom() {
        return from;
    }

    /**
     * @param from Fecha inicial.
     * @return this.
     */
    public IntervalCriteria setFrom(Date from) {
        this.from = from;
        return this;
    }

    /**
     * @return Fecha final.
     */
    public Date getTo() {
        return to;
    }

    /**
     * @param to Fecha final.
     * @return this.
     */
    public IntervalCriteria setTo(Date to) {
        this.to = to;
        return this;
    }
}
