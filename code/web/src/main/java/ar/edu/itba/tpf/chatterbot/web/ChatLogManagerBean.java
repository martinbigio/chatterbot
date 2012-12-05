package ar.edu.itba.tpf.chatterbot.web;


import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.DataModel;
import javax.faces.model.ListDataModel;

import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.model.Chat;
import ar.edu.itba.tpf.chatterbot.service.LoggingService;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;
import ar.edu.itba.tpf.chatterbot.support.LoggingCriteria;

public class ChatLogManagerBean {

	private static ResourceBundle rb;

	{
	    rb =  ResourceBundle.getBundle("ar.edu.itba.tpf.chatterbot.ErrorMessages");
	}
	
	private IntervalCriteriaManager intervalCriteriaManager;
	private LoggingService loggingService;
	private DataModel chatsDataModel;
	private Chat chat;
	private String keywords = "";
	

	public ChatLogManagerBean() {
		intervalCriteriaManager = new IntervalCriteriaManager();
		chatsDataModel = new ListDataModel();
	}

	@Required
	public void setLoggingService(LoggingService loggingService) {
		this.loggingService = loggingService;
		reloadDataModel();
	}

	public DataModel getChats() {
		return chatsDataModel;
	}
	
	public String viewChat() {
		chat = (Chat) chatsDataModel.getRowData();
		return "successful";
	}
	
	public Chat getChat() {
		return chat;
	}
	
	private void reloadDataModel() {
		IntervalCriteria intervalCriteria = intervalCriteriaManager.getIntervalCriteria();
		LoggingCriteria loggingCriteria = new LoggingCriteria(intervalCriteria.getFrom(), intervalCriteria.getTo(), 
				keywords);
		chatsDataModel.setWrappedData(loggingService.getChats(loggingCriteria));
	}
	
	public String getKeywords() {
		return keywords;
	}
	
	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}
	
	public String updateDate() {
		System.out.println(intervalCriteriaManager.getIntervalCriteria());
		
		if (intervalCriteriaManager.getFromDate().compareTo(intervalCriteriaManager.getToDate()) == 1) {
			FacesContext facesContext = FacesContext.getCurrentInstance();

			FacesMessage facesMessage = new FacesMessage();
			facesMessage.setSeverity(FacesMessage.SEVERITY_ERROR);
			facesMessage.setDetail(rb.getString("ar.edu.itba.tpf.chatterbot.logs.errorDate"));
			facesMessage.setSummary(rb.getString("ar.edu.itba.tpf.chatterbot.logs.errorDate"));
			
			facesContext.addMessage("", facesMessage);
			return "error";
		} else {
			reloadDataModel();
			return "successful";
		}
	}
	
	public IntervalCriteriaManager getIntervalCriteriaManager() {
		return intervalCriteriaManager;
	}
}
