package ar.edu.itba.tpf.chatterbot.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;

/**
 * Un mensaje dentro de una conversación entre el chatterbot y el cliente.
 * 
 * Contiene la fecha, el contenido del mensaje y la referencia al <code>Chat</code> en cuestión. Además incluye un flag
 * <code>userQuery</code> para indicar si es un mensaje del usuario o del chatterbot.
 */
@Entity
public class ChatEntry extends VersionablePersistentEntity implements Serializable {

    static final long serialVersionUID = 1L;

    @Column(name="cdate")
    private Date date;
    private String message;
    private boolean userQuery;

    @ManyToOne
    private Chat chat;

    ChatEntry() {
    }

    /**
     * Crea un nuevo mensaje y lo agrega al chat que recibe por parámetro.
     * 
     * @param date Fecha y hora del mensaje.
     * @param message Contenido del mensaje.
     * @param userQuery True si es del usuario, false si es del chatterbot.
     */
    public ChatEntry(Chat chat, Date date, String message, boolean userQuery) {
        super();
        this.chat = chat;
        this.date = date;
        this.message = message;
        this.userQuery = userQuery;
        // this.chat.addChatEntry(this);
    }

    /**
     * @return Fecha y hora del mensaje.
     */
    public Date getDate() {
        return date;
    }

    /**
     * @return Texto del mensaje.
     */
    public String getMessage() {
        return message;
    }

    /**
     * @return Si es del usuario o del chatterbot.
     */
    public boolean isUserQuery() {
        return userQuery;
    }

    /**
     * @return Chat al que pertenece el mensaje.
     */
    public Chat getChat() {
        return this.chat;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("[LOG date: " + date);
        sb.append(" message: " + message + "]");
        return sb.toString();
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((date == null) ? 0 : date.hashCode());
        result = prime * result + ((message == null) ? 0 : message.hashCode());
        result = prime * result + (userQuery ? 1231 : 1237);
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        ChatEntry other = (ChatEntry) obj;
        if (date == null) {
            if (other.date != null)
                return false;
        } else if (!date.equals(other.date))
            return false;
        if (message == null) {
            if (other.message != null)
                return false;
        } else if (!message.equals(other.message))
            return false;
        if (userQuery != other.userQuery)
            return false;
        return true;
    }
}
