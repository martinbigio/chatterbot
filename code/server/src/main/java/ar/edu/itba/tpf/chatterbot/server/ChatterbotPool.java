package ar.edu.itba.tpf.chatterbot.server;

import java.util.Hashtable;

import javax.jms.ConnectionFactory;
import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;

import org.apache.log4j.Logger;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.BeanFactory;
import org.springframework.beans.factory.BeanFactoryAware;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;
import org.springframework.jms.listener.DefaultMessageListenerContainer;

import ar.edu.itba.tpf.chatterbot.exception.ChatterbotException;
import ar.edu.itba.tpf.chatterbot.model.Server;

/**
 * Bean que administra al pool de chatterbots. Almacena las referencias a cada instancia de chatterbot y mantiene el
 * estado de las conversaciones con cada usuario (asocia nombre de usuario con número de chatterbot).
 */
public class ChatterbotPool implements BeanFactoryAware, MessageListener {

    private static final Logger logger = Logger.getLogger(ChatterbotPool.class);

    /* Pool de chatterbots. */
    private Chatterbot[] chatterbots;

    /* Tabla que indica que chatterbot atiende a que cliente */
    private Hashtable<String, Integer> dispatchingTable;

    /* Cantidad de clientes que estan siendo atendidos */
    private int clientsCount;

    private ConnectionFactory connectionFactory;
    private BeanFactory beanFactory;
    private JmsTemplate jmsTemplate;

    private ServerConfiguration serverConfiguration;
    private Server server = null;

    public ChatterbotPool() {
        this.dispatchingTable = new Hashtable<String, Integer>();
        this.clientsCount = 0;
    }

    @Required
    public void setServerConfiguration(ServerConfiguration serverConfiguration) {
        this.serverConfiguration = serverConfiguration;
    }

    /**
     * @return Una nueva instancia de un chatterbot en cada invocación.
     */
    protected Chatterbot getChatterbot() {
        return (Chatterbot) this.beanFactory.getBean("chatterbot");
    }

    /**
     * Inicializa el pool de chatterbots. Instancia tantos chatterbots como indique el parámetro
     * <code>maxPoolSize</code> y crea las colas para los llamados asincrónicos.
     */
    public void init() throws ChatterbotException {

        Server server = this.serverConfiguration.getServer();

        this.chatterbots = new Chatterbot[server.getMaxChatterbots()];

        logger.info("Iniciando pool de chatterbots...");

        for (int i = 0; i < chatterbots.length; i++) {
            Chatterbot c = getChatterbot();
            setMessageListenerContainer(c, "chatterbot" + i);
            chatterbots[i] = c;
        }
    }

    /**
     * @see javax.jms.MessageListener#onMessage(Message)
     */
    public void onMessage(Message m) {

        if (server == null) {
            server = serverConfiguration.getServer();
        }

        final TextMessage tm = (TextMessage) m;
        String text;

        try {
            text = tm.getText();
        } catch (JMSException e) {
            logger.error("Error al recibir mensaje en el chatterbot pool");
            return;
        }

        logger.debug("Mensaje: " + text);
        final String user = text.substring(0, text.indexOf('#'));
        final String message = text.substring(text.indexOf('#') + 1, text.length());

        Integer index = dispatchingTable.get(user);

        if (index == null) {

            /* Primer mensaje de la conversacion */
            if (clientsCount < server.getMaxChatterbots()) {
                int i;
                for (i = 0; i < chatterbots.length; i++) {
                    if (!chatterbots[i].isActive()) {
                        break;
                    }
                }
                chatterbots[i].initChatterbot(user, server);
                dispatchingTable.put(user, i);
                logger.debug("Nuevo cliente: " + user + " - Chatterbot asignado: " + i);
                index = clientsCount++;
            }
        }

        if (message.equals("#EOC")) {
            logger.debug("Fin de la conversacion del cliente " + user);
            chatterbots[index].destroyChatterbot();
            dispatchingTable.remove(user);
            clientsCount--;
        } else {
		logger.debug("Mensaje: " + message + " - Al chatterrbot " + index);
            jmsTemplate.send("chatterbot" + index, new MessageCreator() {
                public javax.jms.Message createMessage(Session session) throws JMSException {
                    return tm;
                }
            });
        }
    }

    public void setBeanFactory(BeanFactory beanFactory) throws BeansException {
        this.beanFactory = beanFactory;
    }

    public void setConnectionFactory(ConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void setJmsTemplate(JmsTemplate jmsTemplate) {
        this.jmsTemplate = jmsTemplate;
    }

    private void setMessageListenerContainer(Chatterbot c, String queueName) {
        DefaultMessageListenerContainer container = new DefaultMessageListenerContainer();
        container.setConnectionFactory(connectionFactory);
        container.setDestinationName(queueName);
        container.setMessageListener(c);
        container.initialize();
    }

}
