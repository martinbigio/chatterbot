package ar.edu.itba.tpf.chatterbot.server;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.jms.JMSException;
import javax.jms.Message;
import javax.jms.MessageListener;
import javax.jms.Session;
import javax.jms.TextMessage;
import javax.xml.ws.WebServiceException;

import org.apache.log4j.Logger;
import org.springframework.jms.JmsException;
import org.springframework.jms.core.JmsTemplate;
import org.springframework.jms.core.MessageCreator;

import ar.edu.itba.tpf.chatterbot.exception.ChatterbotServiceException;
import ar.edu.itba.tpf.chatterbot.model.Action;
import ar.edu.itba.tpf.chatterbot.model.Chat;
import ar.edu.itba.tpf.chatterbot.model.ChatEntry;
import ar.edu.itba.tpf.chatterbot.model.ErrorLog;
import ar.edu.itba.tpf.chatterbot.model.GlobalNode;
import ar.edu.itba.tpf.chatterbot.model.InternalNode;
import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.model.TreeNode;
import ar.edu.itba.tpf.chatterbot.service.ChatterbotService;
import ar.edu.itba.tpf.chatterbot.service.LoggingService;
import ar.edu.itba.tpf.chatterbot.service.ServerService;

/**
 * Esta clase representa al agente que atiende las consultas con el usuario.
 */
public class Chatterbot implements MessageListener {

    private static final Logger logger = Logger.getLogger(Chatterbot.class);

    /* Constantes que se utilizan como convenciones con las acciones */
    private static final String LAST_USER_MESSAGE_SESSION_VARNAME = "lastUserMessage";
    private static final String ERROR_SESSION_VARNAME = "error";
    private static final String FATAL_ERROR_SESSION_VARNAME = "fatalError";

    /* Nodo actual del árbol de decisión */
    private TreeNode currentNode;

    /* Sesión del chatterbot */
    private List<String> sessionVariablesKeys;
    private List<String> sessionVariablesValues;

    /* Log de la conversación */
    private Chat chat;

    /* Último mensaje recibido del usuario */
    private String lastClientMessage;

    /* JMSTemplate que se utiliza para recibir y enviar mensajes */
    private JmsTemplate jmsTemplate;

    /* Serivicios */
    private ChatterbotService chatterbotService;
    private ServerService serverService;
    private LoggingService loggingService;

    /* Servidor en el que está corriendo el chatterbot */
    private Server server;

    /* Cuenta XMPP del usuario que el chatterbot está atendiendo */
    private String currentUser;

    private boolean active;

    /**
     * Construye un agente que atiende a un cliente.
     * 
     * @param jmsTemplate Template para recibir y enviar mensaje JMS.
     * @param chatterbotService Serivico de chatterbot.
     * @param loggingService Servicio de logging.
     */
    public Chatterbot(JmsTemplate jmsTemplate, ChatterbotService chatterbotService, LoggingService loggingService,
            ServerService serverService) {
        this.jmsTemplate = jmsTemplate;
        this.chatterbotService = chatterbotService;
        this.serverService = serverService;
        this.loggingService = loggingService;
        this.active = false;
    }

    /**
     * Inicializa el estado del chatterbot para que comience a atender a un nuevo cliente.
     * 
     * @param client Cliente al que atenderá el chatterbot.
     * @param server Servidor en el que corre el chatterbot. Este argumento es necesario para luego generar reportes por
     *            servidor sobre las conversaciones.
     */
    public void initChatterbot(String client, Server server) {
        logger.debug("Inicializando chatterbot.");
        this.currentNode = chatterbotService.getRootNode();
        if (this.currentNode == null) {
            logger.error("No se puede obtener el nodo raiz de la base de datos.");
        }
        this.server = server;
        this.currentUser = client;
        this.sessionVariablesKeys = new ArrayList<String>();
        this.sessionVariablesValues = new ArrayList<String>();
        this.chat = new Chat(client, server, false);
        this.active = true;
    }

    /**
     * Método callback de <code>MessageListener</code> que es invocado cada vez que llega un mensaje del
     * <code>JabberClient</code>. Toma el mensaje, lo procesa, y encola la respuesta en la cola de salida.
     */
    public void onMessage(Message m) {

        TextMessage tm = (TextMessage) m;
        String text;

        /*
         * Obtenemos el mensaje que envía el dispatcher.
         */
        try {
            text = tm.getText();
        } catch (JMSException e) {
            logger.error("Error al leer el mensaje del dispatcher.");
            return;
        }

        final String user = text.substring(0, text.indexOf('#'));
        final String message = text.substring(text.indexOf('#') + 1, text.length());

        logger.debug("Mensaje recibido del usuario " + user);

        /*
         * Procesamos la consulta y enviamos la respuesta al dispatcher.
         */
        try {
            logger.debug("Enviando respuesta para el usuario " + user);
            jmsTemplate.send("outputQueue", new MessageCreator() {
                public javax.jms.Message createMessage(Session session) throws JMSException {
                    String answer;

                    try {
                        answer = processMessage(message);
                    } catch (ChatterbotServiceException e) {
                        logger.error("Error al procesar pregunta del usuario: " + e.getMessage());
                        answer = "Error interno del sistema. Por favor contacte al administrador.";
                    }
                    return session.createTextMessage(user + "#" + answer);
                }
            });
        } catch (JmsException e) {
            logger.error("Error al enviar respuesta al dispatcher. Usuario: " + user);
        }

    }


    /**
     * Destruye el contexto actual del chatterbot y lo deja listo para atender un nuevo cliente.
     */
    public void destroyChatterbot() {
        logger.debug("Finalizando conversación con el usuario " + currentUser);

        if (chat.getChatEntries().size() > 0) {
            server = serverService.getServerByName(server.getName());
            chat.setServer(server);
            chat.getServer().addChat(chat);
            serverService.persistServer(server);
        }
        this.currentUser = "";
        this.chat = null;
        this.active = false;
    }

    /**
     * Procesa un mensaje del cliente y genera la respuesta del chatterbot. El chatterbot puede modificar su estado
     * (nodo y variables de sesión).
     * 
     * @param message Mensaje enviado por el cliente.
     * @return Respuesta del chatterbot.
     */
    private String processMessage(String message) throws ChatterbotServiceException {

        lastClientMessage = message;
        String answert = null;
        TreeNode oldNode = currentNode;

        if (currentNode == null) {
            throw new ChatterbotServiceException("Error interno del sistema. Por favor contacte al administrador.",
                    null);
        }
        /*
         * Obtener el próximo nodo del árbol.
         */
        logger.debug("Procesando nodos hijos del nodo actual");
        currentNode = chatterbotService.getTransition((InternalNode) currentNode, message);

        
        if (currentNode == null) {
            currentNode = oldNode;

            /*
             * Determinar si existe una transición hacia un nodo global .
             */
            logger.debug("Buscando en la lista de nodos globales.");
            GlobalNode globalNode = chatterbotService.getGlobalTransition(message);
            if (globalNode == null) {
                /*
                 * Mostrar el menu de opciones disponibles.
                 */
                logger.debug("No hay coincidencia con nodos globales.");
                answert = "No entiendo su consulta. Posibles opciones:";
                for (TreeNode t : ((InternalNode) oldNode).getChildren()) {
                    answert += "\n" + t.getDescription();
                }
            } else {
                logger.debug("Coincidencia con el nodo global " + globalNode.getDescription());
                if (!executeAction(globalNode.getAction())) {
                    answert = getSessionVariableValue(sessionVariablesKeys, sessionVariablesValues, ERROR_SESSION_VARNAME);
                    if (answert == null) {
                        answert = "Error interno del sistema al ejecutar la acción asociada.";
                    }
                } else {
                    answert = generateAnswert(globalNode.getAnswer());
                }
            }

        } else {
            if (!executeAction(currentNode.getAction())) {
                answert = getSessionVariableValue(sessionVariablesKeys, sessionVariablesValues, ERROR_SESSION_VARNAME);
                if (answert == null) {
                    answert = "Error interno del sistema al ejecutar la acción asociada.";
                }
            } else {
                answert = generateAnswert(currentNode.getAnswer());
            }
        }
        cleanErrorSessionVariable();
        
        /*
         * Guardar la pregunta y respuesta en la conversación.
         */
        chat.addChatEntry(new ChatEntry(chat, new Date(), message, true));
        chat.addChatEntry(new ChatEntry(chat, new Date(), answert, false));

        /*
         * Si se llega a un nodo hoja, termina la conversación y se debe guardar el log de la consulta.
         */
        if (!currentNode.hasTransitions()) {
            chat.setSuccessful(true);

            server = serverService.getServerByName(server.getName());
            chat.setServer(server);
            chat.getServer().addChat(chat);
            chat.setFinalNode(currentNode.getDescription());
            serverService.persistServer(server);

            chat = new Chat(currentUser, server);
            currentNode = chatterbotService.getRootNode();
            answert += "\n\n" + currentNode.getAnswer();
        }

        return answert;
    }

    /**
     * Ejecuta una determinada acción, y modifica el estado del chatterbot (las variables de sesión) en función de la
     * acción ejecutada.
     * 
     * @param action Acción a ser ejecutada.
     */
    private boolean executeAction(Action action) {

        if (action == null) {
            return true;
        }

        setLastUserMessageSessionVariable();
        try {
            action.execute(sessionVariablesKeys, sessionVariablesValues);
        } catch (WebServiceException e) {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            e.printStackTrace(new PrintStream(outputStream));
            loggingService.persistErrorLog(new ErrorLog("Error al consumir acción", outputStream.toString()));
            return false;
        }

        /*
         * Si la ejecución de la acción falla (pj, acción de validación), pasar a la transición de error asociada al
         * nodo. Si la ejecución arroja un error fatal, terminar la conversación enviando un mensaje genérico al usuario
         * y logear el error.
         */
        if (sessionVariablesKeys.contains(ERROR_SESSION_VARNAME)) {
            logger.error("EL WEB SERVICE NOS TIRO ESTE ERROR: " + getSessionVariableValue(
                    sessionVariablesKeys, sessionVariablesValues, ERROR_SESSION_VARNAME));
            /*
             * Solo pasar a la transición de error si es un nodo interno. Sino, como ya terminó la conversación no
             * importa.
             */
            if (currentNode instanceof InternalNode) {
                if (((InternalNode) currentNode).getErrorTransition() != null) {
                    currentNode = ((InternalNode) currentNode).getErrorTransition();
                }
            }
            return false;
            
        } else if (sessionVariablesKeys.contains(FATAL_ERROR_SESSION_VARNAME)) {
            loggingService.persistErrorLog(new ErrorLog("Error al consumir acción", getSessionVariableValue(
                    sessionVariablesKeys, sessionVariablesValues, FATAL_ERROR_SESSION_VARNAME)));
            return false;
        }

        return true;
    }

    /**
     * Elimina la clave ERROR_SESSION_VARIABLE y su correspondiente valor de las listas que representan la sesión del
     * cliente en el chatterbot.
     */
    private void cleanErrorSessionVariable() {
        for (int i = 0; i < sessionVariablesKeys.size(); i++) {
            if (sessionVariablesKeys.get(i).equals(ERROR_SESSION_VARNAME)
                    || sessionVariablesKeys.get(i).equals(FATAL_ERROR_SESSION_VARNAME)) {
                sessionVariablesKeys.remove(i);
                sessionVariablesValues.remove(i);
                return;
            }
        }

    }

    /**
     * Genera el mensaje de respuesta del chatterbot para el cliente.
     * 
     * @param message Mensaje del cliente para el chatterbot.
     * @return Mensaje que el chatterbot le envía al cliente.
     */
    private String generateAnswert(String answer) {

        if (answer == null) {
            logger.error("Falla precondicion en generateAnswert.");
            answer = "";
        }
        CharSequence inputStr = answer;
        String patternStr = "(\\$\\{[^}]*\\})";

        Pattern pattern = Pattern.compile(patternStr);
        Matcher matcher = pattern.matcher(inputStr);

        /* Reemplazar cada ocurrencia de una variable de sesión dentro de la respuesta */
        while (matcher.find()) {
            String var = matcher.group(0).substring(2, matcher.group(0).length() - 1);
            String value = getSessionVariableValue(sessionVariablesKeys, sessionVariablesValues, var);

            if (value == null) {
                return "Error, variable de sesión " + var + " no definida.";
            } else {
                answer = answer.replace("${" + var + "}", value);
            }
        }

        return answer;
    }

    /**
     * Dada una clave y 2 listas que asocian una clave con un valor, retorna la posicion correspondiente a la clave
     * indicada. En caso de no encontrar nada, devuelve -1.
     * 
     * @param keys Lista de claves.
     * @param values Lista con valores asociados a las claves.
     * @param key Clave que se desea buscar.
     * @return Valor asociado a la clave buscada o -1.
     */
    private int getSessionVariablePosition(List<String> keys, List<String> values, String key) {
        for (int i = 0; i < keys.size(); i++) {
            if (keys.get(i).equals(key)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Dada una clave y 2 listas que asocian una clave con un valor, retorna el valor correspondiente a la clave
     * indicada. En caso de no encontrar nada, devuelve null.
     * 
     * @param keys Lista de claves.
     * @param values Lista con valores asociados a las claves.
     * @param key Clave que se desea buscar.
     * @return Valor asociado a la clave buscada o null.
     */
    private String getSessionVariableValue(List<String> keys, List<String> values, String key) {
        int index = getSessionVariablePosition(keys, values, key);
        return index == -1 ? null : sessionVariablesValues.get(index);
    }

    private void setLastUserMessageSessionVariable() {
        int index = getSessionVariablePosition(sessionVariablesKeys, sessionVariablesValues,
                LAST_USER_MESSAGE_SESSION_VARNAME);
        if (index == -1) {
            sessionVariablesKeys.add(LAST_USER_MESSAGE_SESSION_VARNAME);
            sessionVariablesValues.add(lastClientMessage);
        } else {
            sessionVariablesKeys.set(index, LAST_USER_MESSAGE_SESSION_VARNAME);
            sessionVariablesValues.set(index, lastClientMessage);
        }
    }

    public boolean isActive() {
        return active;
    }
}
