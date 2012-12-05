package ar.edu.itba.tpf.chatterbot.dao;

import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.WebServiceAction;

public class WebServiceActionDAOTest extends GenericDAOTest<WebServiceAction> {

    @Autowired
    private WebServiceActionDAO webServiceActionDAO;
    
    @Override
    protected GenericDAO<WebServiceAction, Long> getDao() {
        return webServiceActionDAO;
    }
    
    @Override
    protected WebServiceAction getSample() {
        return new WebServiceAction("description", "wsdl", "service", "method");
    }
    
    @Override
    protected void updateSample(WebServiceAction sample) {
        sample.setDescription("anotherdescription");
    }
    
}
