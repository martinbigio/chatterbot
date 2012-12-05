package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Collection;

import ar.edu.itba.tpf.chatterbot.model.ErrorLog;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;

/**
 * Objeto de acceso a datos para logs de errores.
 */
public interface ErrorLogDAO extends GenericDAO<ErrorLog, Long> {

    /**
     * Obtiene los logs de errores que ocurrieron en un intervalo dado.
     * 
     * @param intervalCriteria Intervalo en el cual buscar errores.
     * @return La colección de errores en dicho intervalo.
     */
    public Collection<ErrorLog> findErrorLogs(IntervalCriteria intervalCriteria);
}
