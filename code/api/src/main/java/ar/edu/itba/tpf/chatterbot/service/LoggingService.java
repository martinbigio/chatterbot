package ar.edu.itba.tpf.chatterbot.service;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import ar.edu.itba.tpf.chatterbot.model.Chat;
import ar.edu.itba.tpf.chatterbot.model.ErrorLog;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;
import ar.edu.itba.tpf.chatterbot.support.LoggingCriteria;

/**
 * Servicios para información acerca de los chats para reportes.
 */
public interface LoggingService {

    /**
     * Almacena una conversacion.
     * 
     * @param chat Conversación que se desea almacenar.
     */
    public void persistChat(Chat chat);

    /**
     * Obtiene un histograma con la cantidad de chats por dia.
     * 
     * @param intervalCriteria Intervalo en el que se realiza la búsqueda.
     * @return Mapa con la cantidad de chats realizados en cada fecha.
     */
    public Map<Date, Long> getChatsPerDay(IntervalCriteria intervalCriteria);

    /**
     * Obtiene el porcentaje de chats que fueron atendidos en forma satisfactoria.
     * 
     * @param intervalCriteria Intervalo de fechas en la que se realiza la búsqueda.
     * @return Un arreglo de long indicando, el número de chats successful y el número total de chats.
     */
    public Long[] getSuccessfulChatsRatio(IntervalCriteria intervalCriteria);

    /**
     * Obtiene un histograma con la cantidad de chats de diferentes duraciones.
     * 
     * @param intervalCriteria Intervalo de fechas en la que se realiza la búsqueda.
     * @return Mapa con la cantidad de chats que duraron diferente cantidad de minutos.
     */
    public Map<String, Long> getAverageChatTime(IntervalCriteria intervalCriteria);

    /**
     * Obtiene todos los chats que satisfacen un criterio de búsqueda.
     * 
     * @param loggingCriteria Criterio de búsqueda que contiene palabras clave.
     * @return Colección con todos los chats cuyo log tiene uno o más de las palabras clave que se indican en el
     *         criterio de búsqueda.
     */
    public Collection<Chat> getChats(LoggingCriteria loggingCriteria);

    /**
     * Almacena un error ocurrido en la aplicación.
     * 
     * @param errorLog Información del error ocurrido.
     */
    public void persistErrorLog(ErrorLog errorLog);

    /**
     * Elimina un ErrorLog.
     * 
     * @param server ErrorLog que se desea eliminar.
     */
    public void removeErrorLog(ErrorLog errorLog);

    /**
     * Obtiene todos los errores ocurridos en un determinado intervalo de tiempo.
     * 
     * @param intervalCriteria Intervalo de tiempo en el cual buscar errores.
     * @return Colección de errores ocurridos en dicho intervalo.
     */
    public Collection<ErrorLog> getErrorLogs(IntervalCriteria intervalCriteria);

    /**
     * Obtiene los nodos hoja del árbol de consultas y la cantidad de veces que fueron visitados en el intervalo dado.
     * 
     * @param intervalCriteria Intervalo en el cual hacer la consulta.
     * @return Listado de nodos con la cantidad de veces que fueron visitados.
     */
    public Map<String, Long> getMostVisitedNodes(IntervalCriteria intervalCriteria);
}
