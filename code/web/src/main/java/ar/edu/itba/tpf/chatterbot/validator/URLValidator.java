package ar.edu.itba.tpf.chatterbot.validator;

import java.util.ResourceBundle;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.Validator;
import javax.faces.validator.ValidatorException;

public class URLValidator implements Validator {
	
	private static  ResourceBundle rb;

	private static final  String URL_REGEX = "^(https?://)"
        + "?(([0-9a-z_!~*'().&=+$%-]+: )?[0-9a-z_!~*'().&=+$%-]+@)?" //user@
        + "(([0-9]{1,3}\\.){3}[0-9]{1,3}" // IP- 199.194.52.184
        + "|" // allows either IP or domain
        + "localhost" // tertiary domain(s)- www.
        + "|" // allows either IP or domain
        + "([0-9a-z_!~*'()-]+\\.)*" // tertiary domain(s)- www.
        + "([0-9a-z][0-9a-z-]{0,61})?[0-9a-z]\\." // second level domain
        + "[a-z]{2,6})" // first level domain- .com or .museum
        + "(:[0-9]{1,4})?" // port number- :80
        + "((/?)|" // a slash isn't required if there is no file name
        + "(/[0-9a-z_!~*'().;?:@&=+$,%#-]+)+/?)$"; 
	
	{
	    rb =  ResourceBundle.getBundle("ar.edu.itba.tpf.chatterbot.ErrorMessages");
	}
	
	public void validate(FacesContext facesContext, UIComponent uiComponent, Object object)
			throws ValidatorException {
		
		 String enteredEmail = (String)object;
		 
		 Pattern p = Pattern.compile(URL_REGEX);
	        
	     Matcher m = p.matcher(enteredEmail);
	        
	     boolean matchFound = m.matches();
	        
	     if (!matchFound) {
	    	 FacesMessage message = new FacesMessage();
	    	 message.setDetail(rb.getString("ar.edu.itba.tpf.chatterbot.validator.invalidURL"));
	    	 message.setSummary(rb.getString("ar.edu.itba.tpf.chatterbot.validator.invalidURL"));
	    	 message.setSeverity(FacesMessage.SEVERITY_ERROR);
	    	 throw new ValidatorException(message);
	     }

	}

}
