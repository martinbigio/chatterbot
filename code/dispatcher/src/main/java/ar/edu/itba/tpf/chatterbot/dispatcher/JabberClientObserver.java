package ar.edu.itba.tpf.chatterbot.dispatcher;

/**
 * Observer que se debe implementar para recibir mensajes del <code>JabberClient</code>.
 * 
 * El usuario de dicho bean debe registrarle como observer una clase que implemente esta interfaz. De esta manera se le
 * informará a través del método <code>onMessage</code> el arribo de nuevos mensajes.
 */
public interface JabberClientObserver {

    /**
     * Se invoca cuando llega un nuevo mensaje al <code>JabberClient</code>.
     * 
     * @param user Usuario que envía el mensaje.
     * @param message Contenido del mensaje.
     */
    public void onMessage(String user, String message);
    
    /**
     * Se invoca cuando el usuario finaliza la conversación.
     * 
     * @param user Usuario que está finalizando la conversación.
     */
    public void onFinalize(String user);
}
