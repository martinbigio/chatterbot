package ar.edu.itba.tpf.chatterbot.service;

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpStatus;
import org.apache.commons.httpclient.methods.GetMethod;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.dao.ServerDAO;
import ar.edu.itba.tpf.chatterbot.exception.ServerServiceException;
import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.support.ChatCount;
import ar.edu.itba.tpf.chatterbot.support.ServerCriteria;

/**
 * Implementación del servicio de servidores.
 */
public class ServerServiceImpl implements ServerService {

    private static final Logger logger = Logger.getLogger(ServerServiceImpl.class);

    private ServerDAO serverDAO;
    private String dispatcherURL;

    @Required
    public void setServerDAO(ServerDAO serverDAO) {
        this.serverDAO = serverDAO;
    }

    @Required
    public void setDispatcherURL(String dispatcherURL) {
        this.dispatcherURL = dispatcherURL;
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ServerService#getServers()
     */
    public Collection<Server> getServers() {
        return serverDAO.findAll();
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ServerService#persistServer(Server)
     */
    public void persistServer(Server server) {
        serverDAO.save(server);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ServerService#getActiveChatterbots()
     */
    @SuppressWarnings("deprecation")
    public Map<Server, Integer> getActiveChatterbots() throws ServerServiceException {
        HttpClient client = new HttpClient();
        Map<Server, Integer> result = new HashMap<Server, Integer>();

        client.setTimeout(1000);
        
        GetMethod method = new GetMethod(dispatcherURL + "servers");
     
        method.addRequestHeader("User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
        method.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        try {
            int statusCode = client.executeMethod(method);

            logger.info("Solicitando al dispatcher cantidad de chatterbots.");
            if (statusCode == HttpStatus.SC_OK) {
                logger.info("Procesando respuesta del dispatcher.");

                String response = method.getResponseBodyAsString();

                Pattern pattern = Pattern.compile("\"([^\"]*)\": \\[(\\d+), (\\d+)\\]");
                Matcher matcher = pattern.matcher(response);

                while (matcher.find()) {
                    if (matcher.groupCount() != 3) {
                        logger.error("Respuesta inválida del dispatcher");
                        throw new ServerServiceException("Respuesta inválida del dispatcher.", null);
                    }
                    String serverName = matcher.group(1);
                    int activeChatterbots = Integer.valueOf(matcher.group(2));

                    Server s = serverDAO.findServerByName(serverName);
                    if (s == null) {
                        logger.error("Respuesta inválida del dispatcher");
                        throw new ServerServiceException("Respuesta inválida del dispatcher.", null);
                    }

                    result.put(s, activeChatterbots);
                }
                return result;
            }
        } catch (HttpException e) {
            logger.error("No se puede conectar con el dispatcher.");
            throw new ServerServiceException("No se puede conectar con el dispatcher.", null);
        } catch (IOException e) {
            logger.error("No se puede conectar con el dispatcher.");
            throw new ServerServiceException("No se puede conectar con el dispatcher.", null);
        } finally {
            method.releaseConnection();
        }
        logger.error("No se puede conectar con el dispatcher.");
        throw new ServerServiceException("No se puede conectar con el dispatcher.", null);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ServerService#synchronizeServers();
     */
    @SuppressWarnings("deprecation")
    public void synchronizeServers() throws ServerServiceException {
        HttpClient client = new HttpClient();
        GetMethod method = new GetMethod(dispatcherURL + "synchronize");

        client.setTimeout(1000);
        
        logger.info("Sincronizando servidores del dispatcher.");

        method.addRequestHeader("User-Agent",
                "Mozilla/5.0 (Windows; U; Windows NT 5.1; en-US; rv:1.9.0.3) Gecko/2008092417 Firefox/3.0.3");
        method.addRequestHeader("Accept", "text/html,application/xhtml+xml,application/xml;q=0.9,*/*;q=0.8");

        try {
            int statusCode = client.executeMethod(method);

            if (statusCode != HttpStatus.SC_OK) {
                logger.error("Error al sincronizar servidores. Respuesta del dispatcher: "
                        + HttpStatus.getStatusText(statusCode));
                throw new ServerServiceException("No te pude sincronizar los servidores.", null);
            }
        } catch (HttpException e) {
            logger.error("No se puede conectar con el dispatcher.");
            throw new ServerServiceException("No se puede conectar con el dispatcher.", null);
        } catch (IOException e) {
            logger.error("No se puede conectar con el dispatcher.");
            throw new ServerServiceException("No se puede conectar con el dispatcher.", null);
        } finally {
            method.releaseConnection();
        }
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ServerService#getServerLoad(ServerCriteria)
     */
    public Map<Date, Long> getServerLoad(ServerCriteria serverCriteria) {
        Collection<ChatCount<Date>> data = serverDAO.findServerLoad(serverCriteria);
        Map<Date, Long> result = new HashMap<Date, Long>();

        for (ChatCount<Date> cc : data) {
            result.put(cc.getCriteria(), cc.getCount());
        }
        return result;

    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ServerService#removeServer(Server)
     */
    public void removeServer(Server server) {
        serverDAO.delete(server);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ServerService#getServerByName(String)
     */
    public Server getServerByName(String serverName) {
        return serverDAO.findServerByName(serverName);
    }

}
