package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Collection;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.tpf.chatterbot.model.Role;

/**
 * Implementación para Hibernate del objeto de acceso a datos de roles.
 */
@Transactional(readOnly = true)
public class RoleDAOImpl extends GenericDAOImpl<Role, Long> implements RoleDAO {
    @Autowired
    public RoleDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.RoleDAO#findRoleByRoleDescription(String)
     */
    public Role findRoleByRoleDescription(String roleDescription) {
        List<Role> roles = findByCriteria(Restrictions.eq("roleDescription", roleDescription));
        if (roles.size() == 0) {
            return null;
        } else {
            return roles.get(0);
        }
    }
    
    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.RoleDAO#findAvailableRoles()
     */
    @SuppressWarnings("unchecked")
	public Collection<Role> findAvailableRoles() {
    	return getHibernateTemplate().find("select distinct r from Role r");	
    }
}
