package ar.edu.itba.tpf.chatterbot.model;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Una conversación completa entre un cliente y un chatterbot.
 * 
 * Contiene todos los mensajes de la conversación, representados a través de objetos <code>ChatEntry</code>, así como
 * información general del chat, como fecha, duración, si fue satisfactorio o no, y nombre del nodo hoja.
 */
@Entity
public class Chat extends VersionablePersistentEntity {

    private static final long serialVersionUID = 1L;

    @Column(name = "chat_client")
    private String client;
    private String finalNode;

    @Column(name = "is_successful")
    private boolean successful;

    @Column(name = "chat_length")
    private Integer length;
    
    @OneToMany(mappedBy = "chat", cascade = CascadeType.ALL, fetch = FetchType.EAGER )
    private List<ChatEntry> chatEntries;

    @ManyToOne(cascade = CascadeType.ALL)
    private Server server;

    Chat() {
    }

    /**
     * Crea un nuevo chat, sin mensajes y en estado no satisfactorio.
     * 
     * @param client Nombre del usuario Jabber del cliente.
     * @param server Servidor que atiende la consulta.
     */
    public Chat(String client, Server server) {
        this.client = client;
        this.server = server;
        this.chatEntries = new ArrayList<ChatEntry>();
        this.finalNode = "";
        this.successful = false;
        this.length = 0;
//        this.server.addChat(this);
    }

    /**
     * Crea un nuevo chat, sin mensajes.
     * 
     * @param client Nombre del usuario Jabber del cliente.
     * @param server Servidor que atiende la consulta.
     * @param successful Estado satisfactorio de la conversacion.
     */
    public Chat(String client, Server server, boolean successful) {
        this.client = client;
        this.server = server;
        this.chatEntries = new ArrayList<ChatEntry>();
        this.finalNode = "";
        this.successful = successful;
        this.length = 0;
    }

    /**
     * @return Nombre de usuario Jabber del cliente.
     */
    public String getClient() {
        return this.client;
    }

    /**
     * @param client Nombre de usuario Jabber del cliente.
     * @return this
     */
    public Chat setClient(String client) {
        this.client = client;
        return this;
    }

    /**
     * @return Fecha y hora de comienzo del chat.
     */
    public Date getStartDate() {
        if (chatEntries.isEmpty()) {
            return null;
        } else {
            return chatEntries.iterator().next().getDate();
        }
    }

    /**
     * @return Fecha y hora de finalización del chat.
     */
    public Date getEndDate() {
        if (chatEntries.isEmpty()) {
            return null;
        } else {
            return chatEntries.get(chatEntries.size() - 1).getDate();
        }
    }

    /**
     * @return Lista de mensajes de la conversación.
     */
    public List<ChatEntry> getChatEntries() {
        return this.chatEntries;
    }

    /**
     * Agrega un nuevo mensaje a la conversación.
     * 
     * @param chatEntry Mensaje a agregar.
     * @return this
     */
    public Chat addChatEntry(ChatEntry chatEntry) {
        this.chatEntries.add(chatEntry);

        Date minDate = chatEntry.getDate(), maxDate = chatEntry.getDate();
        for (ChatEntry e : this.chatEntries) {
            if (e.getDate().before(minDate)) {
                minDate = e.getDate();
            }
            if (e.getDate().after(maxDate)) {
                maxDate = e.getDate();
            }
        }
        this.length = (int) ((maxDate.getTime() - minDate.getTime()) / (1000 * 60));

        return this;
    }

    /**
     * @return Si la conversación fue satisfactoria o no.
     */
    public boolean isSuccessful() {
        return this.successful;
    }

    /**
     * @param successful Si la conversación fue satisfactoria o no.
     * @return this
     */
    public Chat setSuccessful(boolean successful) {
        this.successful = successful;
        return this;
    }

    /**
     * @return Servidor que atendió al cliente.
     */
    public Server getServer() {
        return this.server;
    }

    public void setServer(Server server) {
        this.server = server;
    }
    
    /**
     * @return Nombre del nodo del estado final de la conversación.
     */
    public String getFinalNode() {
        return this.finalNode;
    }

    /**
     * @param finalNode Nombre del nodo del estado final de la conversación.
     */
    public void setFinalNode(String finalNode) {
        this.finalNode = finalNode;
    }

    /**
     * @return Duración del chat en minutos.
     */
    public Integer getLength() {
        return length;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((chatEntries == null) ? 0 : chatEntries.hashCode());
        result = prime * result + ((client == null) ? 0 : client.hashCode());
        result = prime * result + ((finalNode == null) ? 0 : finalNode.hashCode());
        result = prime * result + ((server == null) ? 0 : server.hashCode());
        result = prime * result + (successful ? 1231 : 1237);
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
        final Chat other = (Chat) obj;
        if (chatEntries == null) {
            if (other.chatEntries != null)
                return false;
        } else if (!chatEntries.equals(other.chatEntries))
            return false;
        if (client == null) {
            if (other.client != null)
                return false;
        } else if (!client.equals(other.client))
            return false;
        if (finalNode == null) {
            if (other.finalNode != null)
                return false;
        } else if (!finalNode.equals(other.finalNode))
            return false;
        if (server == null) {
            if (other.server != null)
                return false;
        } else if (!server.equals(other.server))
            return false;
        if (successful != other.successful)
            return false;
        return true;
    }

    @Override
    public String toString() {
        return client + " " + server + " " + chatEntries + " " + finalNode + " " + successful;
    }

}
