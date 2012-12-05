package ar.edu.itba.tpf.chatterbot.model;

import javax.persistence.Entity;

/**
 * Un nodo global de la "inteligencia" del chatterbot.
 * 
 * Contiene descripción, conjunto de palabras claves, respuesta y acción.
 */
@Entity
public class GlobalNode extends AbstractNode {

    private static final long serialVersionUID = 1L;

    GlobalNode() {
    }

    /**
     * Crea un <code>GlobalNode</code> completando toda su información.
     * 
     * @param action Acción a realizar cuando el chatterbot está en el nodo.
     * @param keywords Palabras clave que hacen que el chatterbot llegue a este nodo.
     * @param description Descripción del nodo.
     * @param answer Respuesta que el chatterbot emite cuando estando el el nodo.
     */
    public GlobalNode(Action action, String keywords, String description, String answer) {
        super(action, keywords, description, answer);
    }

    
}
