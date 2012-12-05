package ar.edu.itba.tpf.chatterbot.dispatcher;

import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.jms.JMSException;
import javax.jms.Session;

import org.apache.log4j.Logger;
import org.springframework.context.MessageSource;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.MessageCreator;

/**
 * Bean que se encarga de recibir mensajes del servidor jabber y distribuirlos entre los nodos de la granja de
 * chatterbots.
 */
public class Dispatcher {

    private static final Logger logger = Logger.getLogger(Dispatcher.class);

    /**
     * Máximo tiempo que debe estar inactiva una conversación para que se condiere terminada.
     */
    private static final long MAX_INACTIVE_MILLIS = 30000;

    /**
     * Intervalo de tiempo en el que se van a buscar conversaciones terminadas.
     */
    protected static final long POLLING_PERIOD = 10000;

    private MessageSource messageSource;

    private Map<String, ClientState> clients;
    private Set<ServerState> servers;

    private JabberClient jabberClient;
    private Thread timeoutThread;

    /**
     * Inicializa el dispatcher sin clientes ni servidores.
     */
    public Dispatcher() {
        this.clients = new HashMap<String, ClientState>();
        this.servers = new HashSet<ServerState>();
    }

    public void init() {
        /*
         * Lanzamos un thread que haga polling sobre las conversaciones para detectar timeouts.
         */
        timeoutThread = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    
                    logger.debug("Verificando conversaciones terminadas.");
                    GregorianCalendar calendar = new GregorianCalendar();
                    calendar.setTime(new Date());

		    List<String> removeList = new ArrayList<String>();
                    for (final String user : clients.keySet()) {
                        if (calendar.getTimeInMillis() - clients.get(user).getLastMessage().getTimeInMillis() > MAX_INACTIVE_MILLIS) {
                            logger.info("Finalizando conversacion con usuario " + user + " por timeout");

                            ServerState serverState = clients.get(user).getServerState();
                            serverState.decrementChatterbotCount();
                            serverState.getJmsTemplate().send("inputQueue", new MessageCreator() {
                                public javax.jms.Message createMessage(Session session) throws JMSException {
                                    return session.createTextMessage(user + "##EOC");
                                }
                            });
			    removeList.add(user);
                        }
                    }

		    for (String user: removeList){
                            clients.remove(user);
		    }

                    try {
                        Thread.sleep(POLLING_PERIOD);
                    } catch (InterruptedException e) {
                        logger.debug("Thread interrumpido.");
                        return;
                    }
                }
            }
        });

        timeoutThread.start();

    }
    
    public void destroy() {
        timeoutThread.interrupt();
    }

    /**
     * Establece el <code>JabberClient</code> a utilizar y registra un listener para los mensajes recibidos.
     * 
     * @param jabberClient Cliente Jabber para enviar y recibir mensajes XMPP.
     */
    public void setJabberClient(JabberClient jabberClient) {
        this.jabberClient = jabberClient;

        this.jabberClient.setObserver(new JabberClientObserver() {

            /**
             * @see ar.edu.itba.tpf.chatterbot.dispatcher.JabberClientObserver#onMessage(String, String)
             */
            public void onMessage(final String user, final String message) {
                ServerState node = getChatterbotByUser(user);

                if (message.trim().length() == 0) {
                    return;
                }
                
                /*
                 * Si no hay servidores disponibles, responder mensaje de error al usuario.
                 */
                if (node == null) {
                    String answer = messageSource.getMessage("nochatterbots", null, null);
                    Dispatcher.this.jabberClient.sendMessage(user, answer);
                    return;
                }

                /*
                 * En caso contrario, derivar el mensaje al servidor correspondiente.
                 */
                try {
                    node.getJmsTemplate().send("inputQueue", new MessageCreator() {
                        public javax.jms.Message createMessage(Session session) throws JMSException {
                            return session.createTextMessage(user + "#" + message);
                        }
                    });
                } catch (JmsException e) {
                    String answer = messageSource.getMessage("internalerror", null, null);
                    Dispatcher.this.jabberClient.sendMessage(user, answer);
                    logger.error("Error al enviar mensaje al servidor " + node.getServer().getName());
                }
            }

            /**
             * @see ar.edu.itba.tpf.chatterbot.dispatcher.JabberClientObserver#onFinalize(String)
             */
            public void onFinalize(final String user) {
                ServerState node = getChatterbotByUser(user);
                node.decrementChatterbotCount();

                node.getJmsTemplate().send("inputQueue", new MessageCreator() {
                    public javax.jms.Message createMessage(Session session) throws JMSException {
                        return session.createTextMessage(user + "##EOC");
                    }
                });
                clients.remove(user);
            }

        });
    }

    public void setMessageSource(MessageSource messageSource) {
        this.messageSource = messageSource;
    }

    /**
     * Agrega un servidor para ser utilizado en el dispatching.
     * 
     * @param server Servidor a ser agregado.
     */
    public void addServer(ServerState server) {
        servers.add(server);
    }

    /**
     * Elimina un servidor de la lista disponible para dispatching.
     * 
     * @param server Servidor a ser eliminado.
     */
    public void removeServer(ServerState server) {
        /*
         * Dar por terminadas todas las conversaciones que contengan a este servidor.
         */
        Set<String> clientsToRemove = new HashSet<String>();
        for (String user : clients.keySet()) {
            if (clients.get(user).getServerState().equals(server)) {
                clientsToRemove.add(user);
            }
        }
        for (final String s : clientsToRemove) {
            server.decrementChatterbotCount();
            try {
            server.getJmsTemplate().send("inputQueue", new MessageCreator() {
                public javax.jms.Message createMessage(Session session) throws JMSException {
                    return session.createTextMessage(s + "##EOC");
                }
            });
            } catch (JmsException e) {
                logger.error("Error al comunicarse con el servidor " + server.getServer().getName());
            }
            logger.debug("Eliminando usuario " + s + " porque el servidor " + server.getServer().getName()
                    + " fue dado de baja.");
            clients.remove(s);
        }

        servers.remove(server);
    }

    /**
     * @return Lista de servidores disponibles para dispatching.
     */
    public Collection<ServerState> getServerStates() {
        Collection<ServerState> ret = new HashSet<ServerState>();
        for (ServerState s : servers) {
            ret.add(s);
        }
        return ret;
    }

    /**
     * Obtiene el servidor y la referencia a la queue para un determinado usuario. Si no tiene asociado ningún servidor,
     * le asocia uno, y lo retorna.
     * 
     * @param user Remitente del mensaje.
     * @return Servidor y template de jms para enviarle mensajes.
     */
    private ServerState getChatterbotByUser(String user) {
        ClientState c = clients.get(user);
        if (c == null) {
            /*
             * Es un cliente nuevo, nos fijamos si hay servidores disponibles.
             */
            if (servers.size() > 0) {

                /*
                 * Buscamos el servidor menos cargado.
                 */
                ServerState bestServer = null;
                float leastLoad = Float.MAX_VALUE;
                for (ServerState server : servers) {
                    if (server.getLoad() < leastLoad) {
                        leastLoad = server.getLoad();
                        bestServer = server;
                    }
                }

                /*
                 * Si el servidor menos cargado ya está al tope de su capacidad, ignorar.
                 */
                int maxChatterbots = (int) (bestServer.getServer().getMaxChatterbots() * bestServer.getServer()
                        .getMaxLoad());
                if (bestServer.getChatterbotCount() == maxChatterbots) {
                    return null;
                }

                /*
                 * Asignar el cliente al servidor.
                 */
                c = new ClientState(bestServer);
                clients.put(user, c);
                bestServer.incrementChatterbotCount();
            } else {
                return null;
            }
        }

        /*
         * Actualizar la fecha del ultimo mensaje.
         */
        c.updateLastMessage();
        return c.getServerState();
    }

}
