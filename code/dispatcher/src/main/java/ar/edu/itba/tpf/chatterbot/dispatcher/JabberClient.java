package ar.edu.itba.tpf.chatterbot.dispatcher;

import org.apache.log4j.Logger;
import org.jivesoftware.smack.PacketListener;
import org.jivesoftware.smack.Roster;
import org.jivesoftware.smack.XMPPConnection;
import org.jivesoftware.smack.XMPPException;
import org.jivesoftware.smack.filter.PacketFilter;
import org.jivesoftware.smack.filter.PacketTypeFilter;
import org.jivesoftware.smack.packet.Message;
import org.jivesoftware.smack.packet.Packet;

import ar.edu.itba.tpf.chatterbot.exception.ChatterbotClientException;

/**
 * Bean que ofrece servicios para enviar y recibir mensajes a través del procotolo XMPP.
 * 
 * Primero se deben setear las propiedades server, username y password. Luego, en el método <code>init</code> se realiza
 * la conexión al servidor. Durante todo el tiempo que el bean esté en memoria, el cliente permanecerá conectado. Al
 * destruirlo se desconecta del servidor. Para enviar mensajes de manera sincrónica se utiliza el método
 * <code>sendMessage</code>. Para recibir mensajes se debe registrar un observer, que va a ser invocado cuando llegue un
 * mensaje nuevo.
 */
public class JabberClient {

    private static final Logger logger = Logger.getLogger(JabberClient.class);

    private String server;
    private String username;
    private String password;
    private JabberClientObserver observer;
    private XMPPConnection connection;

    /**
     * Inicializa el bean realizando el login al servidor Jabber, y dejando al cliente online.
     * 
     * @throws ChatterbotClientException Si falla la conexión, retornará en esta excepción información del error.
     */
    public void init() throws ChatterbotClientException {

        try {
            logger.info("Conectando con el servidor Jabber.");
            connection = new XMPPConnection(server);
            connection.login(username, password);
        } catch (XMPPException e) {
            logger.warn("Error de conexión con el servidor Jabber, intentando registrar el usuario chatterbot.");
            if (connection != null && connection.getAccountManager() != null) {
                try {
                    connection.getAccountManager().createAccount("chatterbot", "chatterbot");
                    connection.login(username, password);
                } catch (XMPPException e2) {
                    logger.error("Error de conexión con el servidor Jabber.");
                    throw new ChatterbotClientException("No se puede conectar con el servidor Jabber.", e);
                }
            } else {
                logger.error("Error de conexión con el servidor Jabber.");
                throw new ChatterbotClientException("No se puede conectar con el servidor Jabber.", e);
            }
        }

        Roster.setDefaultSubscriptionMode(Roster.SUBSCRIPTION_ACCEPT_ALL);
        PacketFilter messageFilter = new PacketTypeFilter(Message.class);
        connection.addPacketListener(new PacketListener() {
            public void processPacket(Packet packet) {
                if (!(packet instanceof Message)) {
                    return;
                }
                final Message m = (Message) packet;

                logger.debug("Mensaje recibido del usuario " + m.getFrom());

                /*
                 * Si el mensaje es de tipo chat se informa al observer. Sino, significa que el usuario cerró la
                 * ventana. Igualmente esto depende del cliente, puede ocurrir que cierre la ventana y no se envíe nada.
                 */
                if (m.getType().equals(Message.Type.CHAT)) {
                    observer.onMessage(m.getFrom(), m.getBody());
                } else {
                    observer.onFinalize(m.getFrom());
                }
            }
        }, messageFilter);

    }

    /**
     * Finaliza la conexión con el servidor Jabber.
     */
    public void destroy() {
        connection.close();
    }

    /**
     * Envía un mensaje a un determinado usuario.
     * 
     * @param user Usuario al cual enviarle el mensaje.
     * @param message Mensaje a ser enviado.
     */
    public void sendMessage(String user, String message) {
        Message m = new Message(user, Message.Type.CHAT);
        m.setBody(message);
        m.setFrom("chatterbot");
        connection.sendPacket(m);
    }

    /**
     * @param server Servidor jabber al cual conectarse.
     */
    public void setServer(String server) {
        this.server = server;
    }

    /**
     * @param username Usuario con el cual loguearse.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    /**
     * @param password Contraseña del usuario jabber.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @param observer Observer que va a ser invocado al recibir un nuevo mensaje.
     */
    public void setObserver(JabberClientObserver observer) {
        this.observer = observer;
    }

}
