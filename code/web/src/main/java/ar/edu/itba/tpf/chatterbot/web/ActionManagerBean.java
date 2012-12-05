package ar.edu.itba.tpf.chatterbot.web;

import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.model.Action;
import ar.edu.itba.tpf.chatterbot.model.BaseAction;
import ar.edu.itba.tpf.chatterbot.model.TreeNode;
import ar.edu.itba.tpf.chatterbot.model.WebServiceAction;
import ar.edu.itba.tpf.chatterbot.service.ChatterbotService;

public class ActionManagerBean extends GenericManagerBean<Action> {

    private static final long serialVersionUID = 1L;
    
    private ChatterbotService chatterbotService;

    @Required
    public void setChatterbotService(ChatterbotService chatterbotService) {
        this.chatterbotService = chatterbotService;
    }

    private TreeManagerBean treeManagerBean;

    @Required
    public void setTreeManagerBean(TreeManagerBean treeMAnagerBean) {
        this.treeManagerBean = treeMAnagerBean;
    }
    
    @Override
    protected Action createData() {
    	return new WebServiceAction("", "", "", "");
    }

    @Override
    protected boolean persistData() {
    	if (getSelectedData() instanceof WebServiceAction) {
            this.chatterbotService.persistWebserviceAction((WebServiceAction) getSelectedData());
    	}
    	
//        //TODO: Recargar el arbol del manager bean (podriamos hacer esto cambiando el scope?)
//        treeManagerBean.loadTree();
//        if (treeManagerBean.getSelectedNode() != null && treeManagerBean.getSelectedNode().getData() != null) {
//            treeManagerBean.getSelectedNode().setData(chatterbotService
//                    .getTreeNodeByDescription(((TreeNode) treeManagerBean.getSelectedNode().getData()).getDescription()));
//        }
    	
    	return true;
    }

    @Override
    protected boolean removeData() {
    	BaseAction action = (BaseAction) getSelectedData();
    	
        if (action.getReferences().size() == 0) {
            chatterbotService.removeAction(action);
        } else {
        	addMesssageError(rb.getString("ar.edu.itba.tpf.chatterbot.action.actionIsInUse"));
            return false;
        }

        return true;
    }
    
    @Override
    public String getLoadListData() {
        getListData().setWrappedData(chatterbotService.getAllActions());
    	return "";
    }
   
}
