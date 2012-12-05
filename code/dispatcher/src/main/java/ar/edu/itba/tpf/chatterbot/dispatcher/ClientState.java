package ar.edu.itba.tpf.chatterbot.dispatcher;

import java.util.Date;
import java.util.GregorianCalendar;

/**
 * Clase wrapper que contiene la ultima fecha de una conversación, y el servidor asociado.
 * Se utiliza como valor en la tabla de hash del dispatcher.
 */
public class ClientState {
    private GregorianCalendar lastMessage;
    private ServerState serverState;

    /**
     * Crea un nuevo estado con un servidor dado y la fecha actual.
     *  
     * @param serverState Servidor que atenderá al nuevo usuario.
     */
    public ClientState(ServerState serverState) {
        this.serverState = serverState;
        this.lastMessage = new GregorianCalendar();
        lastMessage.setTime(new Date());
    }

    /**
     * @return Fecha y hora del mensaje más reciente.
     */
    public GregorianCalendar getLastMessage() {
        return lastMessage;
    }

    /**
     * @return Referencia al servidor que atiende la conversación.
     */
    public ServerState getServerState() {
        return serverState;
    }

    /**
     * Actualiza la fecha del mensaje mas reciente a la fecha actual.
     */
    public void updateLastMessage() {
        lastMessage.setTime(new Date());
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((lastMessage == null) ? 0 : lastMessage.hashCode());
        result = prime * result + ((serverState == null) ? 0 : serverState.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        ClientState other = (ClientState) obj;
        if (lastMessage == null) {
            if (other.lastMessage != null)
                return false;
        } else if (!lastMessage.equals(other.lastMessage))
            return false;
        if (serverState == null) {
            if (other.serverState != null)
                return false;
        } else if (!serverState.equals(other.serverState))
            return false;
        return true;
    }

    

}
