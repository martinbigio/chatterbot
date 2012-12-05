package ar.edu.itba.tpf.chatterbot.web;

import javax.faces.component.UIComponent;
import javax.faces.event.AbortProcessingException;
import javax.faces.event.ActionEvent;
import javax.faces.event.ActionListener;

import org.richfaces.component.html.HtmlPanelMenuGroup;
import org.richfaces.component.html.HtmlPanelMenuItem;

public class MenuManagerBean implements ActionListener {
	
	private String selectedMenu = "";

	
	public String getSelectedChild() {
		return selectedMenu;
	}

//	public String changeMenu() {
//		FacesContext context = FacesContext.getCurrentInstance();
//		this.selectedMenu = (String) context.getExternalContext().getRequestParameterMap().get("menu_group");
//		return (String) context.getExternalContext().getRequestParameterMap().get("menu_to");
//	}
	

	
	public void processAction(ActionEvent actionEvent) throws AbortProcessingException {
		UIComponent component = actionEvent.getComponent();
		
		if (component instanceof HtmlPanelMenuItem) {
			HtmlPanelMenuItem panelMenuItem = (HtmlPanelMenuItem) component;
			
			if (panelMenuItem.getParent() instanceof HtmlPanelMenuGroup) {
				HtmlPanelMenuGroup panelMenuGroup = (HtmlPanelMenuGroup) panelMenuItem.getParent();
				this.selectedMenu = (String) panelMenuGroup.getName(); 
			}
		}
	}

}
