package ar.edu.itba.tpf.chatterbot.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

import javax.faces.model.SelectItem;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.security.context.SecurityContextHolder;
import org.springframework.security.providers.encoding.ShaPasswordEncoder;

import ar.edu.itba.tpf.chatterbot.model.Role;
import ar.edu.itba.tpf.chatterbot.model.User;
import ar.edu.itba.tpf.chatterbot.service.UserService;

public class UserManagerBean extends GenericManagerBean<User> {

	private static final long serialVersionUID = 0L;
	
    private ShaPasswordEncoder shaPasswordEncoder;
    
    @Required
    public void setShaPasswordEncoder(ShaPasswordEncoder shaPasswordEncoder) {
		this.shaPasswordEncoder = shaPasswordEncoder;
	}
    
    private UserService userService;

    @Required
    public void setUserService(UserService userService) {
        this.userService = userService;
    }
    
    private String repassword;
    private String loggedUser;
    
    public UserManagerBean() {
    	super();
    	User user = (User) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    	this.loggedUser = user.getUsername();
    }
    
    
    public void setRepassword(String repassword) {
        this.repassword = repassword;
    }

    public String getRepassword() {
        return repassword;
    }

    
    public String getLoggedUser() {
    	return this.loggedUser;
    }
    
    public List<SelectItem> getAvailableRoles() {

        Collection<Role> roles = userService.getAvailableRoles();
        List<SelectItem> rolesItems = new ArrayList<SelectItem>();

        for (Role role : roles) {
            rolesItems.add(new SelectItem(role, role.getRoleDescription()));
        }

        return rolesItems;
    }
    
    @Override
    public String getLoadListData() {
        getListData().setWrappedData(userService.getUsers());
    	return "";
    }

    @SuppressWarnings("unchecked")
    public Role getUserMainRole() {

        if (this.getSelectedData().getRoles() != null) {
            Iterator iter = this.getSelectedData().getRoles().iterator();

            if (iter.hasNext()) {
                return (Role) iter.next();
            }
        }

        return null;
    }

    public void setUserMainRole(Role role) {
        Collection<Role> roles = (Collection<Role>) this.getSelectedData().getRoles();
        if (roles != null) {
            roles.clear();
            roles.add(role);
        } else {
            roles = new ArrayList<Role>();
            roles.add(role);
            this.getSelectedData().setRoles(roles);
        }
    }

    @Override
    protected User createData() {
    	return new User("", "", "", "", "");
    }

    
    @Override
    protected boolean persistData() {
        String password = this.getSelectedData().getPassword();

        if (!isEditMode()) {
            if (password == null || password.equals("") || repassword == null || repassword.equals("")) {
            	addMesssageError(rb.getString("ar.edu.itba.tpf.chatterbot.user.emptyPassword"));
                return false;
            }

            User user = this.userService.getUserByUsername(this.getSelectedData().getUsername());

            if (user != null) {
            	addMesssageError(rb.getString("ar.edu.itba.tpf.chatterbot.validator.userExists"));
                return false;
            }
        }

        if (password.equals("") && repassword.equals("") && isEditMode()) {
        	// Recupera el password en caso de que no se lo modifique.
        	User oldUser = userService.getUserByUsername(this.getSelectedData().getUsername());
        	this.getSelectedData().setPassword(oldUser.getPassword());
        } else if (password == null || repassword == null || password.equals("") || repassword.equals("") 
        		|| !password.equals(repassword)) {
        	addMesssageError(rb.getString("ar.edu.itba.tpf.chatterbot.user.passwordMismatch"));
            return false;
        } else {
        	this.getSelectedData().setPassword(shaPasswordEncoder.encodePassword(
        			this.getSelectedData().getPassword(), null));
        }

        userService.persistUser(this.getSelectedData());
        return true;
    }

    
    @Override
    protected boolean removeData() {
        userService.removeUser(this.getSelectedData());
    	return true;
    }
    
}
