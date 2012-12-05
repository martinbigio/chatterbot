package ar.edu.itba.tpf.chatterbot.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;

/**
 * Accion generica que se puede asignar a cualquier nodo.
 * 
 * Contiene una descripcion y un metodo abstracto <code>execute</code> que debe ser implementado por cada tipo
 * especifico de accion.
 */
@Entity
@Table(uniqueConstraints = { @UniqueConstraint(columnNames = { "description" }) })
public abstract class BaseAction extends VersionablePersistentEntity implements Action {

    private static final long serialVersionUID = 1L;

    private String description;
    
    @OneToMany(mappedBy="action", targetEntity=AbstractNode.class, cascade=CascadeType.MERGE, fetch=FetchType.EAGER)
    private Collection<AbstractNode> references;
    
    BaseAction() {
    }

    /**
     * Crea una nueva acción con una determinada descripción.
     * 
     * @param description Descripción de la acción.
     */
    public BaseAction(String description) {
        this.description = description;
        this.references = new ArrayList<AbstractNode>();
    }

    /**
     * @return Descripción de la acción.
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description Descripción de la acción.
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.model.Action#execute(String, java.util.List, java.util.List)
     */
    public abstract void execute(List<String> keys, List<String> values);

    @Override
    public String toString() {
        return description;
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
        BaseAction other = (BaseAction) obj;
        if (description == null) {
            if (other.description != null)
                return false;
        } else if (!description.equals(other.description))
            return false;
        return true;
    }
    
    public Collection<AbstractNode> getReferences() {
        return references;
    }
}
