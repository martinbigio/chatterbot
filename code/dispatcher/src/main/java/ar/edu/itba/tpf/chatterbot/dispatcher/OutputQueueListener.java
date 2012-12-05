package ar.edu.itba.tpf.chatterbot.dispatcher;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;

/**
 * Listener para las colas de salida de cada uno de los servidores.
 * 
 * Este bean implementa el listener de JMS, y es usado por el <code>DispatcherManager</code> para registrarlo como
 * listener de cada una de las output queues de los servidores de la granja. Lo único que hace es en el arribo de un
 * nuevo mensaje, invocar a <code>sendMessage</code> del <code>JabberClient</code> para que lo envíe al usuario
 * correspondiente.
 */
public class OutputQueueListener implements MessageListener {

    private static final Logger logger = Logger.getLogger(OutputQueueListener.class);

    private JabberClient jabberClient;

    /**
     * @param jabberClient Cliente Jabber al cual enviarle los mensajes.
     */
    public void setJabberClient(JabberClient jabberClient) {
        this.jabberClient = jabberClient;
    }

    /**
     * @see MessageListener#onMessage(Message)
     */
    public void onMessage(Message m) {
        TextMessage tm = (TextMessage) m;
        String text;

        try {
            text = tm.getText();
        } catch (JMSException e) {
            logger.error("Error al leer mensaje de la output queue.");
            return;
        }

        final String user = text.substring(0, text.indexOf('#'));
        final String message = text.substring(text.indexOf('#') + 1, text.length());

        logger.debug("Respuesta recibida para el usuario: " + user);
        jabberClient.sendMessage(user, message);
    }
}
