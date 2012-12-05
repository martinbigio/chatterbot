package ar.edu.itba.tpf.chatterbot.dao;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.WebServiceAction;

/**
 * Implementación para Hibernate del objeto de acceso a datos de nodos globales.
 */
public class WebServiceActionDAOImpl extends GenericDAOImpl<WebServiceAction, Long> implements WebServiceActionDAO {

    @Autowired
    public WebServiceActionDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

}
