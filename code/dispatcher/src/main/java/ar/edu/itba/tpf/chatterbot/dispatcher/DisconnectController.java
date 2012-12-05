package ar.edu.itba.tpf.chatterbot.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

public class DisconnectController extends AbstractController {

    private DispatcherManager dispatcherManager;
    
    @Required
    public void setDispatcherManager(DispatcherManager dispatcherManager) {
        this.dispatcherManager = dispatcherManager;
    }
    
    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response) throws Exception {
        String serverName = request.getParameter("server");
        this.dispatcherManager.removeServer(serverName);
        return null;
    }
}
