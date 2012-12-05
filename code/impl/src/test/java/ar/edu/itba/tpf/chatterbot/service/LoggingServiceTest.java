package ar.edu.itba.tpf.chatterbot.service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import static org.junit.Assert.assertThat;
import static org.hamcrest.Matchers.equalTo;

import ar.edu.itba.tpf.chatterbot.dao.ChatDAO;
import ar.edu.itba.tpf.chatterbot.dao.ErrorLogDAO;
import ar.edu.itba.tpf.chatterbot.model.Chat;
import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.support.ChatCount;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;
import ar.edu.itba.tpf.chatterbot.support.LoggingCriteria;

@RunWith(JMock.class)
public class LoggingServiceTest {

    private Mockery context;
    private LoggingServiceImpl loggingService;
    private ChatDAO chatDAO;
    private ErrorLogDAO errorLogDAO;

    private Chat chat = new Chat("testClient", new Server("test", "test", 100, 100, 1f, true));
    
    @Before
    public void setup() {
        context = new JUnit4Mockery();

        loggingService = new LoggingServiceImpl();
        chatDAO = context.mock(ChatDAO.class);
        errorLogDAO = context.mock(ErrorLogDAO.class);

        loggingService.setChatDAO(chatDAO);
        loggingService.setErrorLogDAO(errorLogDAO);
    }

    @Test
    public void getChatsTest() {
        final Collection<Chat> chats = Arrays.asList(chat);
        final LoggingCriteria criteria = new LoggingCriteria(new Date(), new Date(), "test");
        
        context.checking(new Expectations() {
            {
                one(chatDAO).findChats(criteria);
                will(returnValue(chats));
            }
        });
        assertThat(loggingService.getChats(criteria), equalTo(chats));
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFindMostVisitedNodes() {
        final Collection<ChatCount<String>> resultDAO = Arrays.asList(new ChatCount<String>("node0", 1), new ChatCount<String>("node1", 2));
        final IntervalCriteria criteria1 = new IntervalCriteria(new Date(), new Date());
        final IntervalCriteria criteria2 = new IntervalCriteria(new Date(), new Date());
        
        context.checking(new Expectations() {
            {
                one(chatDAO).findMostVisitedNodes(criteria1);
                will(returnValue(resultDAO));
                one(chatDAO).findMostVisitedNodes(criteria2);
                will(returnValue(new HashSet<ChatCount<String>>()));
            }
        });
        
        Map<String, Long> expected = new HashMap<String, Long>();
        expected.put("node0", 1L);
        expected.put("node1", 2L);
        assertThat(loggingService.getMostVisitedNodes(criteria1), equalTo(expected));
        assertThat((HashMap<String, Long>)loggingService.getMostVisitedNodes(criteria2), equalTo(new HashMap<String, Long>()));
    }
    
    
}
