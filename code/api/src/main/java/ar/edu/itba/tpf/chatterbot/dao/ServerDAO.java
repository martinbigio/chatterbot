package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Collection;
import java.util.Date;

import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.support.ChatCount;
import ar.edu.itba.tpf.chatterbot.support.ServerCriteria;

/**
 * Objeto de acceso a datos para servidores.
 */
public interface ServerDAO extends GenericDAO<Server, Long> {
    
    /**
     * @param name Nombre del servidor buscado.
     * @return Servidor, o NULL si no existe.
     */
    public Server findServerByName(String name);

    /**
     * Obtiene la carga histórica de un servidor en un período dado.
     * 
     * @param serverCriteria Servidor e intervalo de tiempo.
     * @return Carga histórica.
     */
    public Collection<ChatCount<Date>> findServerLoad(ServerCriteria serverCriteria);

}
