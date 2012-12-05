package ar.edu.itba.tpf.chatterbot.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.GlobalNode;

/**
 * Implementación para Hibernate del objeto de acceso a datos de nodos globales.
 */
public class GlobalNodeDAOImpl extends GenericDAOImpl<GlobalNode, Long> implements GlobalNodeDAO {

    @Autowired
    public GlobalNodeDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

}
