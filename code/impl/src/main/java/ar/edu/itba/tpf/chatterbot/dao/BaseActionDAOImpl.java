package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.BaseAction;

public class BaseActionDAOImpl extends GenericDAOImpl<BaseAction, Long> implements BaseActionDAO {

    @Autowired
    public BaseActionDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    @SuppressWarnings("unchecked")
    @Override
    public Collection<BaseAction> findAll() {
        return getHibernateTemplate().find("select distinct ba from BaseAction ba");    
    }
    
    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.BaseActionDAO#findBaseActionByDescription(String)
     */
    public BaseAction findBaseActionByDescription(String description) {
        List<BaseAction> baseActions = findByCriteria(Restrictions.eq("description", description));
        if (baseActions.size() == 0) {
            return null;
        } else {
            return baseActions.get(0);
        }
    }

}
