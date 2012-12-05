package ar.edu.itba.tpf.chatterbot.model;

import javax.persistence.MappedSuperclass;
import javax.persistence.Version;

/**
 * Entidad que se persiste a través de Hibernate y contiene un id.
 * 
 * Todas las entidades que quieran ser persistidas deben extender de esta clase, y ser anotadas con <code>Entity</code>
 * a nivel clase.
 */
@MappedSuperclass
public abstract class VersionablePersistentEntity extends PersistentEntity {

    private static final long serialVersionUID = 1L;

    @Version
    private Long version = null;

    /**
     * @return Versión de la entidad. Si no está persistida es null.
     */
    public Long getVersion() {
        return version;
    }

    /**
     * @param version Versión de la entidad.
     */
    public void setVersion(Long version) {
        this.version = version;
    }
}
