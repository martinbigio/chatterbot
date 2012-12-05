package ar.edu.itba.tpf.chatterbot.web;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.model.SelectItem;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.model.BaseAction;
import ar.edu.itba.tpf.chatterbot.model.GlobalNode;
import ar.edu.itba.tpf.chatterbot.service.ChatterbotService;


public class GlobalNodeManagerBean extends GenericManagerBean<GlobalNode> {

    private static final long serialVersionUID = 1L;

    private static final Logger LOGGER = Logger.getLogger(GlobalNodeManagerBean.class);

    private ChatterbotService chatterbotService;
    
    @Required
    public void setChatterbotService(ChatterbotService chatterbotService) {
    	LOGGER.debug("Se está seteando el servicio.");
        this.chatterbotService = chatterbotService;
    }

    public String getLoadListData() {
    	LOGGER.debug("Se está cargando la información de la BD.");
        getListData().setWrappedData(chatterbotService.getGlobalNodes());
        return "";
    }

    /**
     * @return Colección con todas las acciones almacenadas.
     */
    public Collection<SelectItem> getActions() {
        Collection<SelectItem> actions = new ArrayList<SelectItem>();

        /* Agregar opción para no seleccionar ninguna opcion */
        actions.add(new SelectItem(null));
        for (BaseAction baseAction : chatterbotService.getAllActions()) {
            actions.add(new SelectItem(baseAction));
        }

        return actions;
    }
    
    @Override
    protected GlobalNode createData() {
    	return new GlobalNode(null, "", "", "");
    }
    
    @Override
    protected boolean removeData() {
    	chatterbotService.removeGlobalNode(this.getSelectedData());
    	return true;
    }
    
	@Override
	protected boolean persistData() {
		chatterbotService.persistGlobalNode(this.getSelectedData());
		return true;
	}
}
