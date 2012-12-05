package ar.edu.itba.tpf.chatterbot.dao;

import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.GlobalNode;

public class GlobalNodeDAOTest extends GenericDAOTest<GlobalNode> {

    @Autowired
    private GlobalNodeDAO globalNodeDAO;
    
    @Override
    protected GenericDAO<GlobalNode, Long> getDao() {
        return globalNodeDAO;
    }
    
    @Override
    protected GlobalNode getSample() {
        return new GlobalNode(null, "test", "test", "test");
    }
    
    @Override
    protected void updateSample(GlobalNode sample) {
        sample.setDescription("test2");
    }
}
