package ar.edu.itba.tpf.chatterbot.dao;

import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.User;

/**
 * Implementación para Hibernate del objeto de acceso a datos de usuarios.
 */
public class UserDAOImpl extends GenericDAOImpl<User, Long> implements UserDAO {

    @Autowired
    public UserDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.UserDAO#findUserByUsername(String)
     */
    public User findUserByUsername(String username) {
        List<User> users = (List<User>) findByCriteria(Restrictions.like("username", username));
        if (users.size() == 0) {
            return null;
        } else {
            return users.get(0);
        }
    }
}
