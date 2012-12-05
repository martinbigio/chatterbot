package ar.edu.itba.tpf.chatterbot.service;

import java.util.Collection;
import java.util.Date;
import java.util.Map;

import ar.edu.itba.tpf.chatterbot.exception.ServerServiceException;
import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.support.ServerCriteria;

/**
 * Servicio para administración de servidores.
 */
public interface ServerService {

    /**
     * Almacena los datos de un servidor.
     * 
     * @param server Servidor que se desea almacenar.
     */
    public void persistServer(Server server);

    /**
     * Elimina un servidor.
     * 
     * @param server Servidor que se desea eliminar.
     */
    public void removeServer(Server server);

    /**
     * Obtiene la lista de servidores.
     * 
     * @return Colección de servidores.
     */
    public Collection<Server> getServers();

    /**
     * @return Cantidad de chatterbots activos en cada servidor.
     */
    public Map<Server, Integer> getActiveChatterbots() throws ServerServiceException;

    /**
     * @param serverCriteria Crierio de búsqueda.
     * @return Cantidad de chats simultáneos en cada servidor.
     */
    public Map<Date, Long> getServerLoad(ServerCriteria serverCriteria);

    /**
     * @param serverName Nombre del servidor buscado.
     * @return Servidor buscado, o NULL si no existe.
     */
    public Server getServerByName(String serverName);
    
    /**
     * Envía un request al dispatcher para que vuelva a cargar los servidores de la base de datos.
     */
    public void synchronizeServers() throws ServerServiceException;
}
