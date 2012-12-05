package ar.edu.itba.tpf.chatterbot.model;

import javax.persistence.Entity;

/**
 * Nodo hoja del árbol de consultas.
 */
@Entity
public class LeafNode extends TreeNode {
    private static final long serialVersionUID = 0L;

    LeafNode() {
    }

    /**
     * Construye un <code>LeafNode</code> completando únicamente la información del <code>Node</code> del que hereda.
     * 
     * @param action Acción a realizar cuando el chatterbot está en el nodo.
     * @param keywords Palabras clave que hacen que el chatterbot llegue a este nodo.
     * @param description Descripción del nodo.
     * @param answer Respuesta que el chatterbot emite cuando estando el el nodo.
     * @param errorTransition Transición de error.
     */
    public LeafNode(Action action, String keywords, String description, String answer, TreeNode errorTransition) {
        super(action, keywords, description, answer, errorTransition);
    }

    @Override
    public boolean hasTransitions() {
        return false;
    }
}
