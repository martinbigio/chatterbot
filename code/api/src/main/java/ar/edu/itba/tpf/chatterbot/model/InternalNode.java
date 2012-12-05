package ar.edu.itba.tpf.chatterbot.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.OneToMany;

/**
 * Nodo interno del árbol de consultas.
 * 
 * Contiene además de todos los datos de un <code>TreeNode</code>, el conjunto de hijos, la referencia al padre y una
 * transición de error.
 */
@Entity
public class InternalNode extends TreeNode {

    private static final long serialVersionUID = 0L;

    // @OneToMany(mappedBy = "parent", cascade={CascadeType.PERSIST, CascadeType.MERGE, CascadeType.REMOVE})
    @OneToMany(mappedBy = "parent")
    @org.hibernate.annotations.Cascade(value = { org.hibernate.annotations.CascadeType.DELETE_ORPHAN,
            org.hibernate.annotations.CascadeType.ALL })
    private Collection<TreeNode> children = new ArrayList<TreeNode>();

    InternalNode() {
    }

    /**
     * Construye un <code>TreeNode</code> completando únicamente la información del <code>Node</code> del que hereda.
     * 
     * @param action Acción a realizar cuando el chatterbot está en el nodo.
     * @param keywords Palabras clave que hacen que el chatterbot llegue a este nodo.
     * @param description Descripción del nodo.
     * @param answer Respuesta que el chatterbot emite cuando estando el el nodo.
     * @param errorTransition Transición a la que pasa el chatterbot en caso de error.
     */
    public InternalNode(Action action, String keywords, String description, String answer, TreeNode errorTransition) {
        super(action, keywords, description, answer, errorTransition);
    }

    /**
     * Dado una <code>Collection<String></code> con palabras clave, busca los nodos hijos con la mejor puntuación para
     * hacer una transición.
     * 
     * @param keywords <code>Collection<String></code> de palabras clave.
     * @return <code>Collection<TreeNode></code> con los nodos hijos que tienen la mejor puntuación para hacer la
     *         transición.
     */
    public Collection<TreeNode> getTransition(Collection<String> keywords) {
        return children;
    }

    /**
     * @return <code>Collection<TreeNode></code> con los nodos hijo de este nodo.
     */
    public Collection<TreeNode> getChildren() {
        return children;

    }

    /**
     * Agrega un nuevo nodo hijo.
     * 
     * @param child Nuevo <code>TreeNode</code> (nodo hijo).
     * @return this
     */
    public TreeNode addChild(TreeNode child) {
        children.add(child);
        child.parent = this;
        return this;
    }

    /**
     * Remueve el nodo hijo especificadol.
     * 
     * @param child <code>TreeNode</code> que se desea remover.
     */
    public void removeChild(TreeNode child) {
        children.remove(child);
        child.parent = null;
    }

    /**
     * @return True si tiene hijos, false en caso contrario.
     */
    public boolean hasTransitions() {
        return children != null && children.size() > 0;
    }
    
}
