package ar.edu.itba.tpf.chatterbot.model;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.Transient;
import javax.xml.ws.Holder;

import org.apache.log4j.Logger;
import org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean;

import ar.edu.itba.tpf.chatterbot.business.InfoProvider;

/**
 * Implementación de <code>BaseAction</code> para Webservices. un Webservice que desee ser utilizado como acción por
 * el chatterbot debe implementar la interfaz <code>InfoProvider</code>. Cuando se obtiene una instancia de esta case
 * se genera el port (proxy) para comunicarse con el Webservice.
 */
@Entity
public class WebServiceAction extends BaseAction {

    private static final long serialVersionUID = 1L;

    private static final Logger logger = Logger.getLogger(WebServiceAction.class);
    
    /* NamespaceUri que se va a colocar en el SoapEnvelope */
    private static final String NAMESPACE_URI = "http://business.chatterbot.tpf.itba.edu.ar/";

    /* URL del Webservice descriptor */
    private String wsdlUrl;

    /* Nombre del servicio que se queire acceder */
    private String serviceName;

    /* Método que se quiere consumir (requerido por <code>InfoProvider</code>) */
    private String methodNane;

    @Transient
    private InfoProvider provider;

    WebServiceAction() {
        this.provider = null;
    }

    public WebServiceAction(String description, String wsdlUrl, String serviceName, String methodName) {
        super(description);

        this.wsdlUrl = wsdlUrl;
        this.serviceName = serviceName;
        this.methodNane = methodName;
        this.provider = null;
    }

    @Override
    public void execute(List<String> keys, List<String> values) {
        /* Holders para los parámetros de entrada/salida */
        Holder<ArrayList<String>> k = new Holder<ArrayList<String>>((ArrayList<String>) keys);
        Holder<ArrayList<String>> v = new Holder<ArrayList<String>>((ArrayList<String>) values);

        /* Generar dinámicamente el port */
        if (provider == null) {
            JaxWsPortProxyFactoryBean port = new org.springframework.remoting.jaxws.JaxWsPortProxyFactoryBean();
            port.setServiceInterface(InfoProvider.class);
            try {
                port.setWsdlDocumentUrl(new URL(this.wsdlUrl));
            } catch (MalformedURLException e) {
                logger.error("La URL de la accion " + this.getDescription() + " es invalida.");
                return;
            }
            port.setNamespaceUri(NAMESPACE_URI);
            port.setServiceName(this.serviceName + "Service");
            port.setPortName(this.serviceName + "Port");

            javax.xml.ws.Service service = port.createJaxWsService();
            provider = (InfoProvider) service.getPort(service.getPorts().next(), InfoProvider.class);
        }

        /* Invocar */
        provider.provide(methodNane, k, v);

        keys.clear();
        keys.addAll(k.value);
        values.clear();
        values.addAll(v.value);

    }

    public String getWsdlUrl() {
        return wsdlUrl;
    }

    public void setWsdlUrl(String wsdlUrl) {
        this.wsdlUrl = wsdlUrl;
    }

    public String getServiceName() {
        return serviceName;
    }

    public void setServiceName(String serviceName) {
        this.serviceName = serviceName;
    }

    public String getMethodNane() {
        return methodNane;
    }

    public void setMethodNane(String methodNane) {
        this.methodNane = methodNane;
    }

//    public static void main(String[] args) {
  //      Action action = new WebServiceAction("mi accion", "http://localhost:8081/chatterbot-business/clients?wsdl",
    //            "Clients", "validarNumeroCuenta");
     //   ArrayList<String> keys = new ArrayList<String>();
     //   ArrayList<String> values = new ArrayList<String>();
     //   keys.add("lastUserMessage");
     //   values.add("  dasdasd       aaa1234567890aaa    dasda  ");

     //   action.execute(keys, values);
     //   System.out.println(keys);
     //   System.out.println(values);
    //}
}
