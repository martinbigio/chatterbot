package ar.edu.itba.tpf.chatterbot.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToMany;

/**
 * Un rol asignado a uno o más usuarios.
 * 
 * Contiene un nombre, descripción y una <code>Collection<User></code> con los usuarios que tienen este role asignado.
 */
@Entity
public class Role extends VersionablePersistentEntity implements Serializable {

    private static final long serialVersionUID = 1L;

    private String roleName = null;
    private String roleDescription = null;

    @ManyToMany(fetch = FetchType.EAGER, mappedBy = "roles")
    private Collection<User> users = null;

    /**
     * Crea un nuevo <Role> asignando valores default a todos sus campos.
     */
    @SuppressWarnings("unused")
    private Role() {
    }

    /**
     * Crea un nuevo <code>Role</code> asignando únicamente el nombre y la descripción del mismo.
     * 
     * @param roleName Nombre de rol.
     * @param roleDescription Descripción del rol.
     */
    public Role(String roleName, String roleDescription) {
        this.roleName = roleName;
        this.roleDescription = roleDescription;
        this.users = new ArrayList<User>();
    }

    /**
     * @return Nombre del rol.
     */
    public String getRoleName() {
        return roleName;
    }

    /**
     * @param roleName Nombre del rol.
     * @return this.
     */
    public Role setRoleName(String roleName) {
        this.roleName = roleName;
        return this;
    }

    /**
     * @return Descripción del rol.
     */
    public String getRoleDescription() {
        return roleDescription;
    }

    /**
     * @param roleDescription Descripción del rol.
     * @return this.
     */
    public Role setRoleDescription(String roleDescription) {
        this.roleDescription = roleDescription;
        return this;
    }

    /**
     * @return <code>Collection<User></code> que tienen este rol asignado.
     */
    public Collection<User> getUsers() {
        return users;
    }

//    /**
//     * @param users <code>Collection<User></code> que tienen este rol asignado.
//     * @return this.
//     */
//    @SuppressWarnings("unchecked")
//    public Role setUsers(Collection<? extends User> users) {
//        this.users = (Collection<User>) users;
//        return this;
//    }

    
    public Role addUser(User user){
        this.users.add(user);
        return this;
    }
    
    @Override
    public String toString() {
        return this.roleDescription;
    }

    @Override
    public boolean equals(Object obj) {

        if (obj instanceof Role) {
            Role otherRole = (Role) obj;
            return otherRole.getRoleName().equals(this.roleName);
        }

        return super.equals(obj);
    }

    @Override
    public int hashCode() {
        return this.roleName.hashCode();
    }
}
