package ar.edu.itba.tpf.chatterbot.service;

import java.util.Collection;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataAccessException;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.userdetails.UserDetailsService;
import org.springframework.security.userdetails.UsernameNotFoundException;

import ar.edu.itba.tpf.chatterbot.dao.RoleDAO;
import ar.edu.itba.tpf.chatterbot.dao.UserDAO;
import ar.edu.itba.tpf.chatterbot.model.Role;
import ar.edu.itba.tpf.chatterbot.model.User;

/**
 * Implementación del servicio de usuarios.
 */
public class UserServiceImpl implements UserService, UserDetailsService {

    private UserDAO userDAO;
    private RoleDAO roleDAO;

    @Required
    public void setUserDAO(UserDAO userDAO) {
        this.userDAO = userDAO;
    }

    @Required
    public void setRoleDAO(RoleDAO roleDAO) {
        this.roleDAO = roleDAO;
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.UserService#getUserByUsername(String)
     */
    public User getUserByUsername(String username) {
        return userDAO.findUserByUsername(username);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.UserService#persistUser(User)
     */
    public void persistUser(User user) {
        userDAO.save(user);
    }
    
    /**
     * @see ar.edu.itba.tpf.chatterbot.service.UserService#persistRole(Role)
     */
    public void persistRole(Role role) {
        roleDAO.save(role);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.UserService#getAvailableRoles()
     */
    public Collection<Role> getAvailableRoles() {
        return roleDAO.findAvailableRoles()/*findAll()*/;
    }

    /**
     * @see org.springframework.security.userdetails.UserDetailsService#loadUserByUsername(String)
     */
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException, DataAccessException {
        return (User) getUserByUsername(username);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.UserService#getRoleByRoleDescription(String)
     */
    public Role getRoleByRoleDescription(String roleDescription) {
        return roleDAO.findRoleByRoleDescription(roleDescription);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.UserService#getUsers()
     */
    public Collection<User> getUsers() {
        return userDAO.findAll();
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.UserService#removeUser(User)
     */
    public void removeUser(User user) {
        userDAO.delete(user);
    }


}
