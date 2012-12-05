package ar.edu.itba.tpf.chatterbot.validator;

import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.model.User;
import ar.edu.itba.tpf.chatterbot.service.UserService;

public class UserValidator implements Validator {

	private static  ResourceBundle rb;

	{
	    rb =  ResourceBundle.getBundle("ar.edu.itba.tpf.chatterbot.ErrorMessages");
	}
	
	private UserService userService;
	
	@Required
	public void setUserService(UserService userService) {
		this.userService = userService;
	}
	
	
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object object)
			throws ValidatorException {
		String username = (String) object;
		
		User user = this.userService.getUserByUsername(username);
		
		if (user != null) {
	    	 FacesMessage message = new FacesMessage();
	    	 message.setDetail(rb.getString("ar.edu.itba.tpf.chatterbot.validator.userExists"));
	    	 message.setSummary(rb.getString("ar.edu.itba.tpf.chatterbot.validator.userExists"));
	    	 message.setSeverity(FacesMessage.SEVERITY_ERROR);
	    	 throw new ValidatorException(message);
		}
		
	}
	
}
