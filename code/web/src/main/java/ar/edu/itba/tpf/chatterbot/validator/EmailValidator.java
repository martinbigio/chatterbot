package ar.edu.itba.tpf.chatterbot.validator;

import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class EmailValidator implements Validator {

	private static  ResourceBundle rb;

	{
	    rb =  ResourceBundle.getBundle("ar.edu.itba.tpf.chatterbot.ErrorMessages");
	}
	
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object object)
			throws ValidatorException {
		
		 String enteredEmail = (String)object;

		 Pattern p = Pattern.compile(".+@.+\\.[a-z]+");
	        
	     Matcher m = p.matcher(enteredEmail);
	        
	     boolean matchFound = m.matches();
	        
	     if (!matchFound) {
	    	 FacesMessage message = new FacesMessage();
	    	 message.setDetail(rb.getString("ar.edu.itba.tpf.chatterbot.validator.invalidEmail"));
	    	 message.setSummary(rb.getString("ar.edu.itba.tpf.chatterbot.validator.invalidEmail"));
	    	 message.setSeverity(FacesMessage.SEVERITY_ERROR);
	    	 throw new ValidatorException(message);
	     }

	}
	
}
