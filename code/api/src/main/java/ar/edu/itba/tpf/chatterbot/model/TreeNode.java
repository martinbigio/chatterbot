package ar.edu.itba.tpf.chatterbot.model;

import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

/**
 * Un nodo del árbol de decisión del chatterbot.
 * 
 * Es superclase de los nodos internos y hoja.
 */

@Entity
public abstract class TreeNode extends AbstractNode {

    private static final long serialVersionUID = 1L;

    @ManyToOne()
    protected InternalNode parent;

    @ManyToOne()
    private TreeNode errorTransition;

    /* Quienes me tienen en sus transiciones de error */
    @OneToMany(mappedBy = "errorTransition", cascade = CascadeType.MERGE, fetch = FetchType.EAGER)
    private Collection<TreeNode> references = new ArrayList<TreeNode>();

    TreeNode() {
    }

    /**
     * Construye un <code>TreeNode</code> completando únicamente la información del <code>Node</code> del que hereda.
     * 
     * @param action Acción a realizar cuando el chatterbot está en el nodo.
     * @param keywords Palabras clave que hacen que el chatterbot llegue a este nodo.
     * @param description Descripción del nodo.
     * @param answer Respuesta que el chatterbot emite cuando estando el el nodo.
     * @param errorTransition Transición de error a la que se pasa si ocurre un error durante la ejecución de una
     *            acción.
     */
    public TreeNode(Action action, String keywords, String description, String answer, TreeNode errorTransition) {
        super(action, keywords, description, answer);
        setErrorTransition(errorTransition);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.model.GlobalNode#hasTransitions()
     */
    public abstract boolean hasTransitions();

    /**
     * @return Nodo padre del nodo.
     */
    public InternalNode getParent() {
        return parent;
    }

    /**
     * @return Transición de error.
     */
    public TreeNode getErrorTransition() {
        return errorTransition;
    }

    /**
     * @param errorTransition Transición de error.
     */
    public void setErrorTransition(TreeNode errorTransition) {
        if ((this.errorTransition == null && errorTransition == null)
                || (this.errorTransition != null && errorTransition != null && this.errorTransition
                        .equals(errorTransition))) {
            return;
        }
        if (this.errorTransition != null) {
            // System.out.println("setErrorTransition: sacandome de mi padre anterior");
            this.errorTransition.getReferences().remove(this);
        }

        this.errorTransition = errorTransition;
        if (this.errorTransition != null) {
            // System.out.println("setErrorTransition: poniendome en mi nuevo padre");
            this.errorTransition.getReferences().add(this);
        }
    }

    public Collection<TreeNode> getReferences() {
        return references;
    }

}
