package ar.edu.itba.tpf.chatterbot.web;

import java.io.Serializable;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.apache.log4j.Logger;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;

@SuppressWarnings("serial")
public abstract class GenericManagerBean<T> implements Serializable {
	
    protected static ResourceBundle rb;

    {
        rb = ResourceBundle.getBundle("ar.edu.itba.tpf.chatterbot.ErrorMessages");
    }

    private static final Logger LOGGER = Logger.getLogger(GenericManagerBean.class);
    
	private DataModel dataModel = new ListDataModel();
	private boolean editMode;
	private T selectedData;
	
	public abstract String getLoadListData();
	
	public DataModel getListData() {
		return dataModel;
	}

	
	public void setSelectedData(T selectedData) {
		this.selectedData = selectedData;
	}
	
	public T getSelectedData() {
		return selectedData;
	}
	
	@SuppressWarnings("unchecked")
	public String edit() {
		this.editMode = true;
		updateSelectedData();
		return "edit";
	}
	
	public String newData() {
		this.editMode = false;
		this.selectedData = createData();
		return "edit";
	}

	protected abstract boolean persistData();
	protected abstract boolean removeData();
	protected abstract T createData();
	
	protected void addMesssageError(String message) {
		FacesMessage facesMessage = new FacesMessage();
		facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
		facesMessage.setDetail(message);
		facesMessage.setSummary(message);
		FacesContext.getCurrentInstance().addMessage("", facesMessage);
	}
	
	@SuppressWarnings("unchecked")
	protected void updateSelectedData() {
		this.selectedData = (T) dataModel.getRowData();
	}
	
	public String save() {
    	try {
    		if (!persistData()) {
    			return "error";
    		}
    	} catch(DataIntegrityViolationException e) {
    		addMesssageError(rb.getString("ar.edu.itba.tpf.chatterbot.duplicatename"));
    		return "error";
    	} catch(HibernateOptimisticLockingFailureException e2) {
    		addMesssageError(rb.getString("ar.edu.itba.tpf.chatterbot.optimisticLocking"));
    		return "error";
    	}
  
    	return "success";
	}
	
	
	@SuppressWarnings("unchecked")
	public String remove() {
		if (!dataModel.isRowAvailable()) {
			LOGGER.debug("El data model es incorrecto.");
			return "";
		}
		
    	try {
    		updateSelectedData();
    		
    		if (!removeData()) {
    			return "error";
    		}
    	} catch(DataIntegrityViolationException e) {
    		return "success";
    	} catch(HibernateOptimisticLockingFailureException e2) {
    		return "success";
    	} catch(Exception e) {
    		LOGGER.error(e);
    	}
  
    	return "success";
	}
	
	protected void resetSelected() {
		this.selectedData = null;
	}
	
	public boolean isEditMode() {
		return editMode;
	}
}
