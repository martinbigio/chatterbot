package ar.edu.itba.tpf.chatterbot.dispatcher;

import org.springframework.jms.core.JmsTemplate;

import ar.edu.itba.tpf.chatterbot.model.Server;

/**
 * Clase utilizada para almacenar el estado de un servidor en la granja.
 * 
 * Contiene la referencia al servidor y al <code>JmsTemplate</code> abierto para hablar con el broker de ActiveMQ de
 * dicho servidor.
 */
public class ServerState {

    private int chatterbotCount;
    private Server server;
    private JmsTemplate jmsTemplate;

    /**
     * Crea un nuevo estado de un servidor.
     * 
     * @param server Referencia al servidor.
     * @param jmsTemplate Template para poder enviarle mensajes por JMS.
     */
    public ServerState(Server server, JmsTemplate jmsTemplate) {
        this.server = server;
        this.jmsTemplate = jmsTemplate;
    }

    /**
     * @return Template JMS del servidor.
     */
    public JmsTemplate getJmsTemplate() {
        return jmsTemplate;
    }

    /**
     * @return Servidor
     */
    public Server getServer() {
        return server;
    }

    /**
     * @return Cantidad de chatterbots activos en este servidor.
     */
    public int getChatterbotCount() {
        return chatterbotCount;
    }

    /**
     * Incrementa en 1 la cantidad de chatterbots activos.
     */
    public void incrementChatterbotCount() {
        chatterbotCount++;
    }

    /**
     * Decrementa en 1 la cantidad de chatterbots activos.
     */
    public void decrementChatterbotCount() {
        chatterbotCount--;
    }

    /**
     * @return Porcentaje de carga del servidor.
     */
    public float getLoad() {
        return chatterbotCount / (server.getMaxChatterbots() * server.getMaxLoad());
    }

}
