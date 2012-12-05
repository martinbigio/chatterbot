package ar.edu.itba.tpf.chatterbot.model;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Superclase de todos los nodos (globales y del árbol de consulta).
 * 
 * Agrupa todo el comportamiento y la información que tienen en común todos los nodos.
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "description" }) })
public abstract class AbstractNode extends PersistentEntity implements Node {

    private static final long serialVersionUID = 1L;

    @ManyToOne(targetEntity = ar.edu.itba.tpf.chatterbot.model.BaseAction.class)
    private Action action;
    private String keywords;
    private String description;
    private String answer;

    AbstractNode() {
    }

    /**
     * Crea un nuevo nodo con una determinada acción, palabras clave, descripción y template de respuesta.
     * 
     * @param action Acción a ejecutar al ingresar al nodo (puede ser null).
     * @param keywords Palabras clave asociadas al nodo.
     * @param description Descripción del nodo.
     * @param answer Template de respuesta.
     */
    public AbstractNode(Action action, String keywords, String description, String answer) {
        super();
        this.keywords = keywords;
        this.description = description;
        this.answer = answer;
        this.action = action;
    }

    /**
     * @return Palabras clave del nodo.
     */
    public String getKeywords() {
        return keywords;
    }

    /**
     * @param keywords Palabras clave del nodo.
     */
    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * @return Descripción del nodo.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description Decripción del nodo.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return Respuesta que el chatterbot emite estando en el nodo.
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @param answer Respuesta que el chatterbot emite estando en el nodo.
     * @return this
     */
    public void setAnswer(String answer) {
        this.answer = answer;
    }

    /**
     * @return Acción asociada al nodo.
     */
    public Action getAction() {
        return action;
    }

    /**
     * @param action Acción asociada al nodo.
     */
    public void setAction(Action action) {
        if ((this.action == null && action == null)
                || (this.action != null && action != null && this.action.equals(action))) {
            return;
        }
        if (this.action != null) {
            ((BaseAction) this.action).getReferences().remove(this);
        }

        this.action = action;
        if (this.action != null) {
            ((BaseAction) this.action).getReferences().add(this);
        }

    }

    /**
     * @return Porcentaje de coincidencias entre keywords del usuario y las de este nodo.
     */
    public float matchKeywords(Collection<String> keywords) {

        if (this.keywords == null) {
            return 0;
        }

        String[] k = this.keywords.split("[ \t\n\r]");

        float matchAcum = 0;
        float matchTotal = 0;

        for (String userKeyword : keywords) {
            int singleMatches = 0;
            int singleTotal = 0;

            for (String nodeKeyword : k) {

                if (userKeyword.trim().equals(nodeKeyword.trim())) {
                    singleMatches++;
                }
                singleTotal++;

            }

            matchAcum += (float) singleMatches / singleTotal;
            matchTotal++;
        }
        return matchAcum / matchTotal;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((description == null) ? 0 : description.hashCode());
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
        final AbstractNode other = (AbstractNode) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        return true;
    }

    @Override
    public String toString() {
        return description;
    }

}
