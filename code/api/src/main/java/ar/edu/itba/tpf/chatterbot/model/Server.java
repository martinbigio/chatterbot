package ar.edu.itba.tpf.chatterbot.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Un servidor de la granja de chatterbots.
 * 
 * Contiene nombre, hostname, puerto en que escucha, cantidad máxima de chatterbots que puede atender, cantidad máxima
 * de carga que se desea que tenga, un flag que indica si se encuentra habilitado o deshabilitado y el log de chats
 * ocurridos en este servidor.
 */
@Entity
@Table(uniqueConstraints = @UniqueConstraint(columnNames = { "hostname", "port" }))
public class Server extends VersionablePersistentEntity implements Serializable {

    static final long serialVersionUID = 1L;

    private String name;
    private String hostname;
    private int port;
    private int maxChatterbots;
    private float maxLoad;
    private boolean enabled;

    @OneToMany(mappedBy = "server", cascade = CascadeType.ALL)
    private Collection<Chat> chats;

    Server() {
    }

    /**
     * Crea un <code>Server</code> completando toda su información menos el log de chats ocurridos en el servidor.
     * 
     * @param name Nombre del servidor.
     * @param hostname Hostname del servidor.
     * @param port Puerto en que escucha el servidor.
     * @param maxChatterbots Cantidad máxima de chatterbox del servidor.
     * @param maxLoad Carga máxima del servidor.
     * @param enabled Estado del servidor (habilitado o deshabilitado).
     */
    public Server(String name, String hostname, int port, int maxChatterbots, float maxLoad, boolean enabled) {
        super();
        this.name = name;
        this.hostname = hostname;
        this.maxChatterbots = maxChatterbots;
        this.maxLoad = maxLoad;
        this.port = port;
        this.enabled = enabled;
        this.chats = new ArrayList<Chat>();
    }

    /**
     * @return Nombre del servidor.
     */
    public String getName() {
        return this.name;
    }

    /**
     * @param name Nombre del servidor.
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * @return Hosname del servidor.
     */
    public String getHostname() {
        return hostname;
    }

    /**
     * @param hostname del servidor.
     */
    public void setHostname(String hostname) {
        this.hostname = hostname;
    }

    /**
     * @return Puerto en que escucha el servidor.
     */
    public int getPort() {
        return port;
    }

    /**
     * @param port Puerto en que escucha el servidor.
     */
    public void setPort(int port) {
        this.port = port;
    }

    /**
     * @return Cantidad máxima de chatterbot que puede albergar el servidor.
     */
    public int getMaxChatterbots() {
        return maxChatterbots;
    }

    /**
     * 
     * @param maxChatterbots Cantidad máxima de chatterbot que puede albergar el servidor.
     */
    public void setMaxChatterbots(int maxChatterbots) {
        this.maxChatterbots = maxChatterbots;
    }

    /**
     * @return Máxima carga que se desea que tenga el servidor.
     */
    public float getMaxLoad() {
        return maxLoad;
    }

    /**
     * @param maxLoad Máxima carga que se desea que tenga el servidor.
     */
    public void setMaxLoad(float maxLoad) {
        this.maxLoad = maxLoad;
    }

    /**
     * @return Estado del servidor (habilitado o deshabilitado).
     */
    public boolean isEnabled() {
        return this.enabled;
    }

    /**
     * @param enabled Estado del servidor (habilitado o deshabilitado).
     */
    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    /**
     * @return <code>Collection<Chat></code> con el log de chats que ocurrieron en este servidor.
     */
    public Collection<Chat> getChats() {
        return chats;
    }

    /**
     * Agrega un <code>Chat</code> al log de chats de este servidor.
     * 
     * @param chat <code>Chat</code> que se desea agregar al log.
     * @return this.
     */
    public Server addChat(Chat chat) {
        this.chats.add(chat);
        return this;
    }

    @Override
    public String toString() {
        return "Hostname: " + hostname + "\n" + "Port: " + port + "\n" + "MaxChatterbots: " + maxChatterbots + "\n"
                + "MaxLoad: " + maxLoad;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((hostname == null) ? 0 : hostname.hashCode());
        result = prime * result + ((name == null) ? 0 : name.hashCode());
        result = prime * result + port;
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
        final Server other = (Server) obj;
        if (hostname == null) {
            if (other.hostname != null)
                return false;
        } else if (!hostname.equals(other.hostname))
            return false;
        if (name == null) {
            if (other.name != null)
                return false;
        } else if (!name.equals(other.name))
            return false;
        if (port != other.port)
            return false;
        return true;
    }

}
