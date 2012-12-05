package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Collection;
import java.util.Date;

import ar.edu.itba.tpf.chatterbot.model.Chat;
import ar.edu.itba.tpf.chatterbot.support.ChatCount;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;
import ar.edu.itba.tpf.chatterbot.support.LoggingCriteria;

/**
 * Objeto de acceso a datos para chats.
 */
public interface ChatDAO extends GenericDAO<Chat, Long> {

    /**
     * Para el intervalo de fechas indicado, retorna la cantidad de chats por día.
     * 
     * @param intervalCriteria Intervalo de fechas para la búsqueda.
     * @return Colección con la cantidad de chats por día.
     */
    public Collection<ChatCount<Date>> findChatsPerDay(IntervalCriteria intervalCriteria);

    /**
     * Busca la cantidad de chats satisfactorios en un período dado.
     * 
     * @param intervalCriteria Intervalo de fechas para la búsqueda.
     * @return Un arreglo de long indicando, el número de chats successful y el número total de chats.
     */
    public Long[] findSuccessfulChatsRatio(IntervalCriteria intervalCriteria);

    /**
     * Para el intervalo de fechas indicado, retorna la cantidad de chats atendidos en intervalos de clase de un minuto.
     * 
     * @param intervalCriteria Intervalo de fechas para la búsqueda.
     * @return Histograma de tiempos. Cada objeto <code>ChatCount</code> representa un intervalo de clase (de duración
     *         un minuto) con la cantidad de chats atendidos en el mismo.
     */
    public Collection<ChatCount<Integer>> findAverageChatTime(IntervalCriteria intervalCriteria);

    /**
     * Busca todos los chats que satisfacen el criterio especificado.
     * 
     * @param loggingCriteria Criterio de búsqueda.
     * @return Colección de conversaciones que satisfacen el criterio de búsqueda.
     */
    public Collection<Chat> findChats(LoggingCriteria loggingCriteria);

    /**
     * Obtiene la cantidad de veces que fue visitado cada nodo hoja en un intervalo dado.
     * 
     * @param intervalCriteria Intervalo en el cual hacer la consulta.
     * @return Cantidad de veces que fue visitado cada nodo en dicho intervalo.
     */
    public Collection<ChatCount<String>> findMostVisitedNodes(IntervalCriteria intervalCriteria);
}
