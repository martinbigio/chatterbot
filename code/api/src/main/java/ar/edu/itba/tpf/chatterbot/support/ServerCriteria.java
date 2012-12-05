package ar.edu.itba.tpf.chatterbot.support;

import java.util.Date;

import ar.edu.itba.tpf.chatterbot.model.Server;

/**
 * Clase de soporte para reportes de búsqueda indicando un servidor.
 */
public class ServerCriteria extends IntervalCriteria {

    private Server server;

    /**
     * Crea una clase de soporte para bpúsquedas indicando un servidor.
     * 
     * @param from Fecha inicial.
     * @param to Fecha final.
     * @param server Servidor en el que se desea realizar la búsqueda.
     */
    public ServerCriteria(Date from, Date to, Server server) {
        super(from, to);
        this.server = server;
    }

    /**
     * @return Servidor.
     */
    public Server getServer() {
        return server;
    }

    /**
     * @param server Servidor.
     * @return this.
     */
    public ServerCriteria setServer(Server server) {
        this.server = server;
        return this;
    }

}
