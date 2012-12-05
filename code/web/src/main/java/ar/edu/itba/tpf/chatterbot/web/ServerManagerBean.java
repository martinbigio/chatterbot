package ar.edu.itba.tpf.chatterbot.web;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;
import org.springframework.orm.hibernate3.HibernateOptimisticLockingFailureException;

import ar.edu.itba.tpf.chatterbot.exception.ServerServiceException;
import ar.edu.itba.tpf.chatterbot.model.ErrorLog;
import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.service.LoggingService;
import ar.edu.itba.tpf.chatterbot.service.ServerService;

public class ServerManagerBean extends GenericManagerBean<Server> {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(ServerManagerBean.class);
    
    private ServerService serverService;

    private LoggingService loggingService;
    
    @Required
    public void setLoggingService(LoggingService loggingService) {
		this.loggingService = loggingService;
	}
    
    @Required
    public void setServerService(ServerService serverService) {
        this.serverService = serverService;
    }
   
    @Override
    protected Server createData() {
    	return null;
    }

    @Override
    protected boolean persistData() {
    	return false;
    }
    
    @Override
    protected boolean removeData() {
    	serverService.removeServer((Server) this.getSelectedData());
    	return true;
    }
    
    @Override
    public String getLoadListData() {
        getListData().setWrappedData(serverService.getServers());
    	return "";
    }
    
    public String changeStatusServer() {
        Server server = (Server) getListData().getRowData();
        server.setEnabled(!server.isEnabled());

        try {
            serverService.persistServer(server);
            serverService.synchronizeServers();
        } catch (ServerServiceException e) {
            logger.error("Error al sincronizar los servidores.");
        	try {
            	loggingService.persistErrorLog(new ErrorLog(e.getMessage(), e.getMessage()));
        	} catch(Exception e2) {
        		logger.error(e2);
        	}
        } catch (HibernateOptimisticLockingFailureException e2) {
        	addMesssageError(rb.getString("ar.edu.itba.tpf.chatterbot.optimisticLocking"));
        	return "error";
        }
        return "successfull";
    }
}
