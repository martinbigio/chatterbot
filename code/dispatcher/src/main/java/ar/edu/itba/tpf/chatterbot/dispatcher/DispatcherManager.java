package ar.edu.itba.tpf.chatterbot.dispatcher;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import javax.jms.ConnectionFactory;

import org.apache.activemq.ActiveMQConnectionFactory;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jms.connection.SingleConnectionFactory;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.service.ServerService;

/**
 * Bean utilizado para administrar los servidores de la granja.
 */
public class DispatcherManager {

    private Dispatcher dispatcher;
    private OutputQueueListener outputQueueListener;
    private ServerService serverService;
    private static final Logger logger = Logger.getLogger(DispatcherManager.class);

    public void init() {
        for (Server s : serverService.getServers()) {
            if (s.isEnabled()) {
                addServer(s);
            }
        }
    }

    /**
     * @param dispatcher Referencia al dispatcher.
     */
    public void setDispatcher(Dispatcher dispatcher) {
        this.dispatcher = dispatcher;
    }

    /**
     * @param outputQueueListener Listener a registrar en las colas de salida de cada uno de los servidores.
     */
    public void setOutputQueueListener(OutputQueueListener outputQueueListener) {
        this.outputQueueListener = outputQueueListener;
    }

    /**
     * Agrega un nuevo servidor a la granja. Abre una conexión al ActiveMQ del mismo y almacena el template de JMS en el
     * objeto <code>ServerState</code> asociado.
     * 
     * @param server Información del servidor a agregar.
     */
    public void addServer(Server server) {

        ConnectionFactory connectionFactory = new ActiveMQConnectionFactory("tcp://" + server.getHostname() + ":"
                + server.getPort());

        SingleConnectionFactory sgf = new SingleConnectionFactory();
        sgf.setTargetConnectionFactory(connectionFactory);

        JmsTemplate jmsTemplate = new JmsTemplate(sgf);
        ServerState node = new ServerState(server, jmsTemplate);
        dispatcher.addServer(node);

        DefaultMessageListenerContainer listener = new DefaultMessageListenerContainer();
        listener.setConnectionFactory(connectionFactory);
        listener.setDestinationName("outputQueue");
        listener.setMessageListener(outputQueueListener);
        listener.initialize();
    }

    @Required
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }

    /**
     * Elimina un servidor de la lista de activos. Es invocado cuando un servidor es dado de baja.
     * 
     * @param serverName Nombre del servidor a desactivar.
     */
    public void removeServer(String serverName) {
        Server server = serverService.getServerByName(serverName);
        if (server == null) {
            logger.error("No se encuentra el servidor " + serverName + " en la lista de servidores activos.");
            return;
        }

        Collection<ServerState> serverStates = dispatcher.getServerStates();
        for (ServerState s : serverStates) {
            if (s.getServer().equals(server)) {
                logger.debug("Deshabilitando el servidor " + server.getName());
                dispatcher.removeServer(s);
                server.setEnabled(false);
                serverService.persistServer(server);
            }
        }
    }
    
    /**
     * Actualiza el estado de los servidores en función de los datos de la base.
     */
    public void synchronizeServerState() {
        
        
        logger.debug("Sincronizando Servidores");
        Collection<Server> newServers = serverService.getServers();
        Collection<ServerState> oldServers = dispatcher.getServerStates();

        for (ServerState i : oldServers) {
            if (!newServers.contains(i.getServer())) {
                logger.debug("Dando de baja servidor");
                dispatcher.removeServer(i);
            } else {
                for (Server s : newServers) {
                    if (s.equals(i.getServer())) {
                        if (!s.isEnabled()) {
                            logger.debug("Dando de baja servidor");
                            dispatcher.removeServer(i);
                        }
                        break;
                    }
                }
            }
        }

        for (Server i : newServers) {
            boolean found = false;
            for (ServerState s : oldServers) {
                if (s.getServer().equals(i)) {
                    found = true;
                    break;
                }
            }
            if (!found && i.isEnabled()) {
                logger.info("Dando de alta searvidor");
                addServer(i);
            }
        }
    }

    /**
     * @return Cantidad de chatterbots activos y cantidad total para cada servidor.
     */
    public Map<String, Integer[]> getServerStats() {
        Collection<ServerState> servers = dispatcher.getServerStates();
        HashMap<String, Integer[]> retval = new HashMap<String, Integer[]>();
        for (ServerState i : servers) {
            int n = i.getChatterbotCount();
            int max = i.getServer().getMaxChatterbots();
            retval.put(i.getServer().getName(), new Integer[] { n, max });
        }
        return retval;
    }

}
