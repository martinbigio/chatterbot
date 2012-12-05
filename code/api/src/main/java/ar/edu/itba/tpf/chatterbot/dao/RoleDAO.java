package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Collection;

import ar.edu.itba.tpf.chatterbot.model.Role;

/**
 * Objeto de acceso a datos para roles.
 */
public interface RoleDAO extends GenericDAO<Role, Long> {
    
    /**
     * Busca un rol con la descripción provista.
     * 
     * @param roleDescription Descripción del rol solicitado.
     * @return Rol solicitado.
     */
    public Role findRoleByRoleDescription(String roleDescription);
    
    public Collection<Role> findAvailableRoles();
}
