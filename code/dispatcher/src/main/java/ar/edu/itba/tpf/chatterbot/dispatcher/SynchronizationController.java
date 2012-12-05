package ar.edu.itba.tpf.chatterbot.dispatcher;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller que atiende el request HTTP de sincronización de servidores.
 * 
 * No retorna nada, simplemente invoca al synchronize del <code>DispatcherManager</code>.
 */
public class SynchronizationController extends AbstractController {

    private DispatcherManager dispatcherManager;

    @Required
    public void setDispatcherManager(DispatcherManager dispatcherManager) {
        this.dispatcherManager = dispatcherManager;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        dispatcherManager.synchronizeServerState();
        return null;
    }

}
