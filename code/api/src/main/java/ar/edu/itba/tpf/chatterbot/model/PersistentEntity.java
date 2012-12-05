package ar.edu.itba.tpf.chatterbot.model;

import java.io.Serializable;

import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;

/**
 * Entidad que se persiste a través de Hibernate y contiene un id.
 * 
 * Todas las entidades que quieran ser persistidas deben extender de esta clase, y ser anotadas con <code>Entity</code>
 * a nivel clase.
 */
@MappedSuperclass
public abstract class PersistentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id = null;

    /**
     * @return Id de la entidad. Si no está persistida es null.
     */
    public Long getId() {
        return id;
    }

    /**
     * @param id Id de la entidad.
     */
    public void setId(Long id) {
        this.id = id;
    }

    @Override
    public int hashCode() {
        return 0;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        return true;
    }

}
