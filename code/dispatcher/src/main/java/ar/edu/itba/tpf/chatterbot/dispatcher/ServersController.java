package ar.edu.itba.tpf.chatterbot.dispatcher;

import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Required;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.AbstractController;

/**
 * Controller que responde al request HTTP de estado de los servidores.
 * 
 * Responde por Json el nombre de cada servidor, junto con la cantidad de chatterbots activos y la cantidad total.
 */
public class ServersController extends AbstractController {

    private DispatcherManager dispatcherManager;

    @Required
    public void setDispatcherManager(DispatcherManager dispatcherManager) {
        this.dispatcherManager = dispatcherManager;
    }

    @Override
    protected ModelAndView handleRequestInternal(HttpServletRequest request, HttpServletResponse response)
            throws Exception {
        Map<String, Integer[]> stats = dispatcherManager.getServerStats();
        StringBuffer sb = new StringBuffer();
        sb.append("{");
        for (String host : stats.keySet()) {
            Integer[] values = stats.get(host);
            if (sb.length() > 1) {
                sb.append(", ");
            }
            sb.append(String.format("\"%s\": [%d, %d]", host.replace("\"", "\\\"").replace("\\", "\\\\"), values[0],
                    values[1]));
        }
        sb.append("}");
        response.getOutputStream().print(sb.toString());
        return null;
    }

}
