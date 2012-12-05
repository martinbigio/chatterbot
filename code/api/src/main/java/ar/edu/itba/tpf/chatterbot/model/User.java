package ar.edu.itba.tpf.chatterbot.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import javax.persistence.Transient;

import org.springframework.security.GrantedAuthority;
import org.springframework.security.userdetails.UserDetails;
import org.springframework.security.util.AuthorityUtils;

/**
 * Un usuario de la aplicación web.
 * 
 * Contiene el nombre de usuario, contraseña, nombre, apellido, email del usuario y una lista de <code>Role</code>. Esta
 * clase implementa <code>UserDetails</code> para que Spring-Security maneja la autenticación de usuarios.
 */
@Entity
@Table(name="TPF_USER")
public class User extends VersionablePersistentEntity implements UserDetails {

    static final long serialVersionUID = 1L;

    private String username = null;
    private String password = null;
    private String firstName = null;
    private String lastName = null;
    private String email = null;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLE", joinColumns = { @JoinColumn(name = "USER_ID") },
    		inverseJoinColumns = { @JoinColumn(name = "ROLE_ID") })
    private Collection<Role> roles = null;

    /**
     * Crea un nuevo usuario asignando valores default a todos los campos.
     */
    @SuppressWarnings("unused")
    private User() {
    }

    /**
     * Crea un nuevo usuario completando toda su información.
     * 
     * @param username Nombre de usuario.
     * @param password Contraseña.
     * @param firstName Primer nombre.
     * @param lastName Apellido.
     * @param email Email.
     */
    public User(String username, String password, String firstName, String lastName, String email) {
        this.username = username;
        this.password = password;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.roles = new ArrayList<Role>();
    }

    public GrantedAuthority[] getAuthorities() {

        String[] authorities = null;
        int i = 0;
        if (roles != null) {
            authorities = new String[roles.size()];

            for (Role r : roles) {
                authorities[i++] = r.getRoleName();
            }
        } else {
            authorities = new String[] { "ROLE_ANONYMOUS" };
        }

        return AuthorityUtils.stringArrayToAuthorityArray(authorities);
    }

    /**
     * @return Valor booleano que indica si la cuenta de usuario ha exbirado.
     */
    @Transient
    public boolean isAccountNonExpired() {
        return true;
    }

    /**
     * 
     * return Valor booleano que indica si la cuenta de usuario no está deshabilitada.
     */
    @Transient
    public boolean isAccountNonLocked() {
        return true;
    }

    /**
     * @return Valor booleano que indica si las credenciales no han expirado.
     */
    @Transient
    public boolean isCredentialsNonExpired() {
        return true;
    }

    /**
     * @return Valor booleano que indica si la cuenta de usuario esta habilitada.
     */
    @Transient
    public boolean isEnabled() {
        return true;
    }

    /**
     * 
     * @return Email del usuario.
     */
    public String getEmail() {
        return email;
    }

    /**
     * @param email Email del usuario.
     */
    public void setEmail(String email) {
        this.email = email;
    }

    /**
     * @return Primer nombre de usuario.
     */
    public String getFirstName() {
        return firstName;
    }

    /**
     * @param firstName Primer nombre del usuario.
     */
    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    /**
     * @return Apellido del usuario.
     */
    public String getLastName() {
        return lastName;
    }

    /**
     * @param lastName Apellido del usuario.
     */
    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    /**
     * @return Contraseña del usuario.
     */
    public String getPassword() {
        return password;
    }

    /**
     * @param password Contraseña del usuario.
     */
    public void setPassword(String password) {
        this.password = password;
    }

    /**
     * @return <code>Collection<Role></code> con los roles del usuario.
     */
    public Collection<Role> getRoles() {
        return roles;
    }

    /**
     * @param roles <code>Collection<Role></code> con los roles del usuario.
     * @return this.
     */
    @SuppressWarnings("unchecked")
    public User setRoles(Collection<? extends Role> roles) {
        this.roles = (List<Role>) roles;
        return this;
    }

    /**
     * @return Nombre de usuario.
     */
    public String getUsername() {
        return username;
    }

    /**
     * @param username Nombre de usuario.
     */
    public void setUsername(String username) {
        this.username = username;
    }

    @Override
    public String toString() {
        StringBuffer sb = new StringBuffer();

        sb.append("[USER]{Username: " + username);
        sb.append(", password: " + password);
        sb.append(", first name: " + firstName);
        sb.append(", last name: " + lastName);
        sb.append(", email: " + email);

        if (roles != null && roles.size() > 0) {
            sb.append(", roles: ");
            for (Role role : roles) {
                sb.append(role.toString());
            }
        } else {
            sb.append(", roles empty");
        }

        sb.append("}");

        return sb.toString();
    }
    
    /**
     * Le agrega al usuario un rol.
     * @param role 
     * @return
     */
    public User addRole(Role role){
        this.roles.add(role);
        return this;
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = super.hashCode();
        result = prime * result + ((email == null) ? 0 : email.hashCode());
        result = prime * result + ((firstName == null) ? 0 : firstName.hashCode());
        result = prime * result + ((lastName == null) ? 0 : lastName.hashCode());
        result = prime * result + ((password == null) ? 0 : password.hashCode());
        result = prime * result + ((username == null) ? 0 : username.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (!super.equals(obj))
            return false;
        if (getClass() != obj.getClass())
            return false;
        final User other = (User) obj;
        if (email == null) {
            if (other.email != null)
                return false;
        } else if (!email.equals(other.email))
            return false;
        if (firstName == null) {
            if (other.firstName != null)
                return false;
        } else if (!firstName.equals(other.firstName))
            return false;
        if (lastName == null) {
            if (other.lastName != null)
                return false;
        } else if (!lastName.equals(other.lastName))
            return false;
        if (password == null) {
            if (other.password != null)
                return false;
        } else if (!password.equals(other.password))
            return false;
        if (username == null) {
            if (other.username != null)
                return false;
        } else if (!username.equals(other.username))
            return false;
        return true;
    }
}
