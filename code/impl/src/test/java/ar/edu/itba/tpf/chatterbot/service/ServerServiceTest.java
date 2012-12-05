package ar.edu.itba.tpf.chatterbot.service;

import java.util.ArrayList;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import ar.edu.itba.tpf.chatterbot.dao.ServerDAO;
import ar.edu.itba.tpf.chatterbot.model.Server;

@RunWith(JMock.class)
public class ServerServiceTest {

    private Mockery context;
    private ServerServiceImpl serverService;
    private ServerDAO serverDAO;

    private Server server;
    private Collection<Server> servers;

    @Before
    public void setup() {
        context = new JUnit4Mockery();

        serverService = new ServerServiceImpl();
        serverDAO = context.mock(ServerDAO.class);

        serverService.setServerDAO(serverDAO);
        serverService.setDispatcherURL("http://localhost/");
        server = new Server("Server 1", "Host 1", 3000, 30, 1.0f, true);
        servers = new ArrayList<Server>();
        servers.add(server);

    }

    @Test
    public void testGetServers() {
        context.checking(new Expectations() {
            {
                one(serverDAO).findAll();
                will(returnValue(servers));
            }
        });
        Collection<Server> s = serverService.getServers();
        assertThat(s, equalTo(servers));
    }

    @Test
    public void testPersistServer() {
        context.checking(new Expectations() {
            {
                one(serverDAO).save(server);
            }
        });
        serverService.persistServer(server);
    }

    @Test
    public void testRemoveServer() {
        context.checking(new Expectations() {
            {
                one(serverDAO).delete(server);
            }
        });
        serverService.removeServer(server);
    }
    
    
    @Test
    public void testGetServerByName() {
        context.checking(new Expectations() {
            {
                one(serverDAO).findServerByName("myserver");
                will(returnValue(server));
            }
        });
        assertThat(serverService.getServerByName("myserver"), equalTo(server));
    }

}
