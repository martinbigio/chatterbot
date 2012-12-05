package ar.edu.itba.tpf.chatterbot.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;

import ar.edu.itba.tpf.chatterbot.model.ErrorLog;
import ar.edu.itba.tpf.chatterbot.service.LoggingService;

public class LogManagerBean extends GenericManagerBean<ErrorLogWrapper> {

	private static final long serialVersionUID = 0L;

	private static final Logger LOGGER = Logger.getLogger(LoginManagerBean.class);
	
	private LoggingService loggingService;

	@Required
	public void setLoggingService(LoggingService loggingService) {
		this.loggingService = loggingService;
	}

	private IntervalCriteriaManager intervalCriteriaManager = new IntervalCriteriaManager();


	@Override
	protected ErrorLogWrapper createData() {
		return null;
	}

	
	public String selectErrorLog() {
		updateSelectedData();
		return "success";
	}
	
	
	public String updateDate() {
		if (intervalCriteriaManager.getFromDate().compareTo(intervalCriteriaManager.getToDate()) == 1) {
			addMesssageError(rb.getString("ar.edu.itba.tpf.chatterbot.logs.errorDate"));
			return "error";
		} else {
			return "successful";
		}
	}
	
	@Override
	public String getLoadListData() {
		Collection<ErrorLog> errorLogs = loggingService.getErrorLogs(intervalCriteriaManager.getIntervalCriteria());
		List<ErrorLogWrapper> errorLogWrappers = new ArrayList<ErrorLogWrapper>();
		boolean isPresent = false;
		
		for (ErrorLog errorLog : errorLogs) {
			if (getSelectedData() != null) {
				if (errorLog.equals(getSelectedData().errorLog)) {
					isPresent = true;
				}
			}
			
			errorLogWrappers.add(new ErrorLogWrapper(errorLog));
		}
		
		getListData().setWrappedData(errorLogWrappers);
		
		if (!isPresent) {
			resetSelected();
		}
		
		return "";
	}
	
	@Override
	protected boolean persistData() {
		return false;
	}

	
	@Override
	protected boolean removeData() {
		return false;
	}
	
	@SuppressWarnings("unchecked")
	public String deleteAppLogs() {
		if (getListData() != null) {
			Collection<ErrorLogWrapper> errorLogWrappers = (Collection<ErrorLogWrapper>) getListData().getWrappedData();

			for (ErrorLogWrapper errorLogWrapper : errorLogWrappers) {
				if (errorLogWrapper.selected) {
			    	try {
						loggingService.removeErrorLog(errorLogWrapper.getErrorLog());
			    	} catch(DataIntegrityViolationException e) {
			    		// Ignore
			    	} catch(HibernateOptimisticLockingFailureException e2) {
			    		// Ignore
			    	}
					
			    	if (getSelectedData() != null) {
						if (errorLogWrapper.errorLog.equals(getSelectedData().getErrorLog())) {
							resetSelected();
						}
			    	}
				}
			}
		}
		return "success";
	}

	public IntervalCriteriaManager getIntervalCriteriaManager() {
		return intervalCriteriaManager;
	}
	
	@SuppressWarnings("unchecked")
	public String exampleMethod() {
		LOGGER.debug("Se va eliminar los nodos.");

		if (getListData() != null) {
			Collection<ErrorLogWrapper> errorLogWrappers = (Collection<ErrorLogWrapper>) getListData().getWrappedData();

			if (errorLogWrappers == null) {
				LOGGER.error("No hay información guardada!!");
			} else {
				for (ErrorLogWrapper errorLogWrapper : errorLogWrappers) {
					if (errorLogWrapper.selected) {
						loggingService.removeErrorLog(errorLogWrapper.getErrorLog());
					}
				}
			}
			
		}
		
    	return "success";
	}
	

}
