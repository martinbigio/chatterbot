package ar.edu.itba.tpf.chatterbot.web;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;

import org.springframework.security.AuthenticationServiceException;
import org.springframework.security.BadCredentialsException;
import org.springframework.security.ui.AbstractProcessingFilter;

public class LoginManagerBean {

//   private String j_password = "admin";
//   private String j_username = "admin";

	private String j_password = "";
	private String j_username = "";

	public LoginManagerBean() {
        Exception ex = (Exception) FacesContext.getCurrentInstance().getExternalContext().getSessionMap().get(
                AbstractProcessingFilter.SPRING_SECURITY_LAST_EXCEPTION_KEY);

        if (ex != null) {
            if (ex instanceof BadCredentialsException) {
                FacesContext.getCurrentInstance().addMessage(
                        "messages",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "El usuario o contraseña es inválido.",
                                "El usuario o contraseña es inválido."));
            } else if (ex instanceof AuthenticationServiceException) {
                FacesContext.getCurrentInstance().addMessage(
                        "messages",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, "El usuario o contraseña es inválido.",
                                "El usuario o contraseña es inválido."));
            } else {
                FacesContext.getCurrentInstance().addMessage("messages",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, ex.getMessage(), ex.getMessage()));
            }
        }
    }

    public void setJ_password(String j_password) {
        this.j_password = j_password;
    }

    public void setJ_username(String j_username) {
        this.j_username = j_username;
    }

    public String getJ_password() {
        return j_password;
    }

    public String getJ_username() {
        return j_username;
    }

}
