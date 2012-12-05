package ar.edu.itba.tpf.chatterbot.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.Server;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;


public class ServerDAOTest extends GenericDAOTest<Server> {

    @Autowired
    private ServerDAO serverDAO;
    
    @Override
    protected GenericDAO<Server, Long> getDao() {
        return serverDAO;
    }

    @Override
    protected Server getSample() {
        return new Server("test", "test", 100, 10, 1f, true);
    }

    @Override
    protected void updateSample(Server sample) {
        sample.setHostname("test2");
    }

    @Test
    public void testFindServerByName() {
        Server server1 = new Server("server1", "host1", 10, 10, 1f, true);
        Server server2 = new Server("server2", "host2", 10, 10, 1f, true);
        Server server3 = new Server("server3", "host3", 10, 10, 1f, true);
        
        serverDAO.save(server1);
        serverDAO.save(server2);
        serverDAO.save(server3);
        
        assertThat(serverDAO.findServerByName("server1"), equalTo(server1));
        assertThat(serverDAO.findServerByName("server2"), equalTo(server2));
        assertThat(serverDAO.findServerByName("server3"), equalTo(server3));
        assertThat(serverDAO.findServerByName("server4"), equalTo(null));
    }
    
}
