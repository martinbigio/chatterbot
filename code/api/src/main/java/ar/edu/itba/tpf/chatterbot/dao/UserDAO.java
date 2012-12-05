package ar.edu.itba.tpf.chatterbot.dao;

import ar.edu.itba.tpf.chatterbot.model.User;

/**
 * Objeto de acceso a datos para usuarios.
 */
public interface UserDAO extends GenericDAO<User, Long> {

    /**
     * Busca el usuario solicitado.
     * 
     * @param username Nombre de usuario que se desea buscar.
     * @return Usuario con el nombre solicitado.
     */
    public User findUserByUsername(String username);
}
