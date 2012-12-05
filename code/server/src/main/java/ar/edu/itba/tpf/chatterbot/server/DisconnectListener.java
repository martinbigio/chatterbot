package ar.edu.itba.tpf.chatterbot.server;

import java.io.IOException;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

public class DisconnectListener {
    
    private String dispatcherURL;
    private String serverName;
    
    private static final Logger logger = Logger.getLogger(DisconnectListener.class);

    @Required
    public void setDispatcherURL(String dispatcherURL) {
        this.dispatcherURL = dispatcherURL;
    }
    
    @Required
    public void setServerName(String serverName) {
        this.serverName = serverName;
    }
    
    @SuppressWarnings("deprecation")
    public void destroy() {

        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(dispatcherURL + "disconnect?server=" + serverName);

        logger.info("Sincronizando servidores del dispatcher.");

        method.addRequestHeader("User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
        method.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");
        client.setTimeout(1000);
        
        try {
            int statusCode = client.executeMethod(method);
            if (statusCode != HttpStatus.SC_OK) {
                logger.error("Error al dar de baja el servidor. Respuesta del dispatcher: "
                        + HttpStatus.getStatusText(statusCode));
            }
        } catch (HttpException e) {
            logger.error("No se puede conectar con el dispatcher.");
        } catch (IOException e) {
            logger.error("No se puede conectar con el dispatcher.");
        } finally {
            method.releaseConnection();
        }
    }
}
