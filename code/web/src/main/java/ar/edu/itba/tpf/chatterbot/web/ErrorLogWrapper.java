package ar.edu.itba.tpf.chatterbot.web;

import ar.edu.itba.tpf.chatterbot.model.ErrorLog;

public class ErrorLogWrapper {

	private final static int MESSAGE_SIZE = 45;
	private final static int STACKTRACE_SIZE = 30;
	
	ErrorLog errorLog;
	boolean selected;
	
	public ErrorLogWrapper(ErrorLog errorLog) {
		this.errorLog = errorLog;
		this.selected = false;
	}
	
	
	public ErrorLog getErrorLog() {
		return errorLog;
	}
	
	public boolean isSelected() {
		return selected;
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
	}
	
	
    public String getShrinkMessage() {
    	if (this.errorLog.getMessage().length() > MESSAGE_SIZE) {
        	return this.errorLog.getMessage().substring(0, MESSAGE_SIZE) + "...";
    	} else {
    		return this.errorLog.getMessage();
    	}
    }

    public String getShrinkStackTrace() {
    	if (this.errorLog.getStackTrace().length() > STACKTRACE_SIZE) {
        	return this.errorLog.getStackTrace().substring(0, STACKTRACE_SIZE) + "...";
    	} else {
    		return this.errorLog.getStackTrace();
    	}
    }

	
}
