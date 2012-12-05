package ar.edu.itba.tpf.chatterbot.server;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.exception.ServerServiceException;
import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.service.ServerService;

/**
 * Clase utilizada para levantar la información del servidor del archivo properties y actualizarla en la base de datos.
 * 
 * Primero busca el servidor por nombre. Si lo encuentra, actualiza los datos. Si no lo encuentra, lo crea, lo habilita
 * y ejecuta un synchronize en el dispatcher, para que lo empiece a utilizar.
 */
public class ServerConfiguration {

    private static final Logger logger = Logger.getLogger(ServerConfiguration.class);
    private static final int DISPATCHER_POLLING_PERIOD = 3000;

    private String name;
    private String hostname;
    private int port;
    private int maxChatterbots;
    private float maxLoad;

    private ServerService serverService;

    @Required
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    /**
     * @param hostname Host del servidor.
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @param maxChatterbots Maxima cantidad de chatterbots admitida.
     */
    public void setMaxChatterbots(int maxChatterbots) {
        this.maxChatterbots = maxChatterbots;
    }

    /**
     * @param maxLoad Máxima carga permitida (porcentaje sobre max chatterbots).
     */
    public void setMaxLoad(float maxLoad) {
        this.maxLoad = maxLoad;
    }

    /**
     * @param name Nombre del servidor.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @param port Puerto en el que escucha el servidor.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return La referencia a un objeto <code>Server</code> que representa a este servidor.
     */
    public Server getServer() {
        Server s = serverService.getServerByName(name);

        if (s == null) {
            s = new Server(name, hostname, port, maxChatterbots, maxLoad, true);
        } else {
            s.setHostname(hostname);
            s.setMaxChatterbots(maxChatterbots);
            s.setMaxLoad(maxLoad);
            s.setPort(port);
            s.setEnabled(true);
        }

        serverService.persistServer(s);

        new Thread(new Runnable() {
            public void run() {
                boolean synchronizeOk = false;

                while (!synchronizeOk) {
                    synchronizeOk = true;
                    try {
                        serverService.synchronizeServers();
                    } catch (ServerServiceException e) {
                        synchronizeOk = false;
                        logger.error("Error al sincronizar servidores en el dispatcher. Itentando nuevamente en "
                                + DISPATCHER_POLLING_PERIOD + " milisegundos.");
                    }
                    try {
                        Thread.sleep(DISPATCHER_POLLING_PERIOD);
                    } catch (InterruptedException e) {
                    }
                }
            }
        }).start();
        return s;
    }
}
