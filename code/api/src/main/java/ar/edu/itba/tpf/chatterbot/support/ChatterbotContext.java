package ar.edu.itba.tpf.chatterbot.support;

import java.util.Map;

/**
 * Un contexto para un chatterbot.
 * 
 * Contiene el último mensaje del cliente y una mapa con las variables de sesión que el usuario ha completado.
 */
public class ChatterbotContext {

    private String clientMessage;
    private Map<String, Object> sessionVariables;

    /**
     * @return Último mensaje enviado por el cliente.
     */
    public String getClientMessage() {
        return clientMessage;
    }

    /**
     * @param clientMessage Último mensaje enviado por el cliente.
     * @return this.
     */
    public ChatterbotContext setClientMessage(String clientMessage) {
        this.clientMessage = clientMessage;
        return this;
    }

    /**
     * Obtiene el valor de una variable de sesión del chatterbot.
     * 
     * @param varname Nombre de la variabla de sesión de la que se quiere saber su valor.
     * @return Valor asociado a la variable de sesión o null en caso de que no esté definida.
     */
    public Object getSessionVariable(String varname) {
        return sessionVariables.get(varname);
    }

    /**
     * Establece el valor de una variable de sesión del chatterbot.
     * 
     * @param varname Nombre de la variable de sesión.
     * @param data Valor asociado a la variable de sesión.
     * @return this.
     */
    public ChatterbotContext setSessionVariable(String varname, Object data) {
        this.sessionVariables.put(varname, data);
        return this;
    }
}
