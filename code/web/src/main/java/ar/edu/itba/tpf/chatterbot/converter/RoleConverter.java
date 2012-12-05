package ar.edu.itba.tpf.chatterbot.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import ar.edu.itba.tpf.chatterbot.model.Role;
import ar.edu.itba.tpf.chatterbot.service.UserService;

public class RoleConverter implements Converter {
	
	private UserService userService = null;
	
	public void setUserService(UserService userService) {
		this.userService = userService;
	}

	public Object getAsObject(FacesContext context, UIComponent component, String value) {
		Role role = userService.getRoleByRoleDescription(value);
		return role;
	}
	
	public String getAsString(FacesContext context, UIComponent component, Object object) {
		if (object instanceof Role) {
			Role role = (Role) object;
			return role.getRoleDescription();
		}
		
		return "Rol no definido";
	}
	

}
