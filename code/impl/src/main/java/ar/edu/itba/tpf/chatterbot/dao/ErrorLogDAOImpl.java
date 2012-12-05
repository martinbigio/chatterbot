package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Collection;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.ErrorLog;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;

/**
 * Implementación para Hibernate del objeto de acceso a datos de errores.
 */
public class ErrorLogDAOImpl extends GenericDAOImpl<ErrorLog, Long> implements ErrorLogDAO {

    @Autowired
    public ErrorLogDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }
    
    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.ErrorLogDAO#findErrorLogs(IntervalCriteria)
     */
    @SuppressWarnings("unchecked")
	public Collection<ErrorLog> findErrorLogs(IntervalCriteria intervalCriteria) {
        return getHibernateTemplate().find(
                "select e from ErrorLog e where e.timestamp >= ? and e.timestamp <= ? ",
                new Object[] { intervalCriteria.getFrom(), intervalCriteria.getTo() });

    }

}
