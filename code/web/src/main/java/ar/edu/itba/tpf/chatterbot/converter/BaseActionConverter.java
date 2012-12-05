package ar.edu.itba.tpf.chatterbot.converter;

import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.convert.Converter;

import ar.edu.itba.tpf.chatterbot.model.BaseAction;
import ar.edu.itba.tpf.chatterbot.service.ChatterbotService;

public class BaseActionConverter implements Converter {

    private ChatterbotService chatterbotService;

    public void setChatterbotService(ChatterbotService chatterbotService) {
        this.chatterbotService = chatterbotService;
    }

    public Object getAsObject(FacesContext arg0, UIComponent arg1, String value) {
        if (value.equals("Ninguna")) {
            return null;
        } else {
            return chatterbotService.getActionByDescription(value);
        }
    }

    public String getAsString(FacesContext arg0, UIComponent arg1, Object obj) {
        if (obj == null) {
            return "Ninguna";
        } else {
            if (obj instanceof BaseAction) {
                return ((BaseAction) obj).getDescription();
            } else {
                throw new RuntimeException("Converter BaseActionConverter cannot be applied to a " + obj.getClass()
                        + " instance");
            }
        }
    }

}
