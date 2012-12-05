package ar.edu.itba.tpf.chatterbot.listener;

import java.util.Iterator;
import java.util.List;

import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.component.UIViewRoot;
import javax.faces.context.FacesContext;
import javax.faces.event.PhaseEvent;
import javax.faces.event.PhaseId;
import javax.faces.event.PhaseListener;

public class MessageListener implements PhaseListener {
	
	private static final long serialVersionUID = 0L;
	
	
	
	private UIComponent findComponentById(UIComponent root, String id) {
		if (root == null) {
			return null;
		}

		if(root.getId().equals(id)) {
			return root;
		}
		
		List<UIComponent> childComponents = root.getChildren();
		
		for (UIComponent component : childComponents) {
			UIComponent uiComponent = findComponentById(component, id);
			
			if (uiComponent != null) {
				return uiComponent;
			}
			
		}
		
		return null;
	}
	
	public void beforePhase(PhaseEvent phaseEvent) {
		FacesContext facesContext = phaseEvent.getFacesContext();
		UIViewRoot root = facesContext.getViewRoot();
		
		Iterator<String> i = facesContext.getClientIdsWithMessages();

		while (i.hasNext()) {
			
			String id = (String) i.next();
			
			if (id.equals("")) {
				continue;
			}

			String resolvedId = id;
			
			if (id.contains(":")) {
				String[] parts = id.split(":");
				resolvedId = parts[parts.length - 1];
			}

			UIComponent component = findComponentById(root, resolvedId);

			if (component == null){
			    continue;
			}
			
			String fieldName = (String) component.getAttributes().get("fieldName");
			
			if (fieldName != null) {
			
				Iterator<FacesMessage> j = facesContext.getMessages(id);
				
				while (j.hasNext()) {
					FacesMessage facesMessage = (FacesMessage) j.next();
					facesMessage.setDetail(fieldName + ": " + facesMessage.getDetail());
					facesMessage.setSummary(fieldName + ": " + facesMessage.getSummary());
				}
			}
		}
	}
	
	public void afterPhase(PhaseEvent e) {}
	
	public PhaseId getPhaseId() {
		return PhaseId.RENDER_RESPONSE;
	}
	

}
