package ar.edu.itba.tpf.chatterbot.service;

import java.util.Collection;

import ar.edu.itba.tpf.chatterbot.model.Role;
import ar.edu.itba.tpf.chatterbot.model.User;

/**
 * Servicio para administración de usuarios.
 */
public interface UserService {

    /**
     * Almacena un usuario.
     * 
     * @param user Usuario que se desea almacenar.
     */
    public void persistUser(User user);
    
    /**
     * Almacena un rol.
     * 
     * @param role Rol que se desea almacenar.
     */
    public void persistRole(Role role);

    /**
     * Elimina un usuario.
     * 
     * @param user Usuario que se desea eliminar.
     */
    public void removeUser(User user);

    /**
     * Busca un usuario dado su nombre de usuario.
     * 
     * @param username Nombre de usuario del usuario que se desea buscar.
     * @return Usuario con el nombre de usuario especificado o null en caso de no encontrarlo.
     */
    public User getUserByUsername(String username);

    /**
     * Obtiene una colección con todos los roles que pueden tener los usuarios.
     * 
     * @return Colección de roles que pueden terner asignados los usuarios.
     */
    public Collection<Role> getAvailableRoles();

    /**
     * Obtiene una colección con todos los usuarios registrados.
     * 
     * @return Colección con todos los usuarios registrados.
     */
    public Collection<User> getUsers();

    /**
     * Obtiene todos los roles que tienen como descripción la indicada.
     * 
     * @param roleDescription Descripción de los roles que se quieren obtener.
     * @return Colección con roles cuya descripción es la especificada.
     */
    public Role getRoleByRoleDescription(String roleDescription);
}
