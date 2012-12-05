package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.GregorianCalendar;

import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.Chat;
import ar.edu.itba.tpf.chatterbot.model.ChatEntry;
import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.support.ChatCount;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;
import ar.edu.itba.tpf.chatterbot.support.LoggingCriteria;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

public class ChatDAOTest extends GenericDAOTest<Chat> {

    @Autowired
    private ChatDAO chatDAO;

    private Chat[] chats;
    private Date[] dates;

    @Override
    protected GenericDAO<Chat, Long> getDao() {
        return chatDAO;
    }

    @Override
    protected Chat getSample() {
        return new Chat("testClient", new Server("testServer", "testHost", 1000, 1, 1f, true));
    }

    @Override
    protected void updateSample(Chat sample) {
        sample.setClient("testClient2");
    }

    @Before
    public void setup() {

        Server s = new Server("testServer", "testHost", 100, 100, 1f, true);
        chats = new Chat[4];

        /* Chat satisfactorio que empieza a las 4:00:00 y termina a las 4:01:04 */
        chats[0] = new Chat("testClient", s, true);
        chats[0].addChatEntry(new ChatEntry(chats[0], new GregorianCalendar(2008, 01, 01, 4, 0, 0).getTime(), "hola",
                true));
        chats[0].addChatEntry(new ChatEntry(chats[0], new GregorianCalendar(2008, 01, 01, 4, 0, 1).getTime(),
                "en que lo puedo ayudar?", false));
        chats[0].addChatEntry(new ChatEntry(chats[0], new GregorianCalendar(2008, 01, 01, 4, 0, 10).getTime(),
                "quiero consultar el horario de atencion", true));
        chats[0].addChatEntry(new ChatEntry(chats[0], new GregorianCalendar(2008, 01, 01, 4, 0, 11).getTime(),
                "de que sucursal?", false));
        chats[0].addChatEntry(new ChatEntry(chats[0], new GregorianCalendar(2008, 01, 01, 4, 1, 03).getTime(),
                "quiero consultar el horario de atencion", true));
        chats[0].addChatEntry(new ChatEntry(chats[0], new GregorianCalendar(2008, 01, 01, 4, 1, 04).getTime(),
                "el horario es de 10:00 a 15:00", false));
        chats[0].setFinalNode("nodo0");
        
        /* Chat no satisfactorio que empieza a las 4:00:30 y termina a las 4:00:40 */
        chats[1] = new Chat("testClient2", s, false);
        chats[1].addChatEntry(new ChatEntry(chats[1], new GregorianCalendar(2008, 01, 01, 4, 0, 30).getTime(),
                "buen dia", true));
        chats[1].addChatEntry(new ChatEntry(chats[1], new GregorianCalendar(2008, 01, 01, 4, 0, 31).getTime(),
                "que tiene de bueno?", false));
        chats[1].addChatEntry(new ChatEntry(chats[1], new GregorianCalendar(2008, 01, 01, 4, 0, 40).getTime(), "chau",
                true));
        chats[1].setFinalNode("nodo0");
        
        /* Chat no satisfactorio que empieza a las 5:00:00 y termina a las 5:00:23 */
        chats[2] = new Chat("testClient", s, false);
        chats[2].addChatEntry(new ChatEntry(chats[2], new GregorianCalendar(2008, 01, 01, 5, 0, 0).getTime(), "buenas",
                true));
        chats[2].addChatEntry(new ChatEntry(chats[2], new GregorianCalendar(2008, 01, 01, 5, 0, 1).getTime(),
                "hola, en que lo puedo ayudar?", false));
        chats[2].addChatEntry(new ChatEntry(chats[2], new GregorianCalendar(2008, 01, 01, 5, 0, 10).getTime(),
                "quiero sacar a pasear a mi perro", true));
        chats[2].addChatEntry(new ChatEntry(chats[2], new GregorianCalendar(2008, 01, 01, 5, 0, 12).getTime(),
                "no entiendo su pregunta", false));
        chats[2].addChatEntry(new ChatEntry(chats[2], new GregorianCalendar(2008, 01, 01, 5, 0, 20).getTime(),
                "es la hora del paseo", true));
        chats[2].addChatEntry(new ChatEntry(chats[2], new GregorianCalendar(2008, 01, 01, 5, 0, 23).getTime(),
                "ingrese la sucursal", false));
        chats[2].setFinalNode("nodo1");
        
        /* Chat satisfactorio que empieza a las 5:00:15 y termina a las 5:01:16 */
        chats[3] = new Chat("testClient3", s, true);
        chats[3].addChatEntry(new ChatEntry(chats[3], new GregorianCalendar(2008, 01, 01, 5, 0, 15).getTime(), "hola",
                true));
        chats[3].addChatEntry(new ChatEntry(chats[3], new GregorianCalendar(2008, 01, 01, 5, 0, 16).getTime(),
                "en que lo puedo ayudar?", false));
        chats[3].addChatEntry(new ChatEntry(chats[3], new GregorianCalendar(2008, 01, 01, 5, 1, 00).getTime(),
                "quiero hacer una transferencia", true));
        chats[3].addChatEntry(new ChatEntry(chats[3], new GregorianCalendar(2008, 01, 01, 5, 1, 03).getTime(),
                "disculpe, pero esa transaccion no esta soportada", false));
        chats[3].addChatEntry(new ChatEntry(chats[3], new GregorianCalendar(2008, 01, 01, 5, 1, 15).getTime(),
                "ok, no te hagas drama, me voy a la sucursal", true));
        chats[3].addChatEntry(new ChatEntry(chats[3], new GregorianCalendar(2008, 01, 01, 5, 1, 16).getTime(), "adios",
                false));
        chats[3].setFinalNode("nodo1");
        
        dates = new Date[] { new GregorianCalendar(2008, 01, 01, 0, 0, 0).getTime(),
                new GregorianCalendar(2008, 01, 01, 4, 0, 0).getTime(),
                new GregorianCalendar(2008, 01, 01, 4, 0, 10).getTime(),
                new GregorianCalendar(2008, 01, 01, 4, 1, 30).getTime(),
                new GregorianCalendar(2008, 01, 01, 5, 0, 10).getTime(),
                new GregorianCalendar(2008, 01, 01, 5, 1, 30).getTime(),
                new GregorianCalendar(2008, 01, 01, 5, 1, 40).getTime() };

        chatDAO.save(chats[0]);
        chatDAO.save(chats[1]);
        chatDAO.save(chats[2]);
        chatDAO.save(chats[3]);
    }

    @Test
    public void testFindSuccessfulChatsRatio() {
        assertThat(chatDAO.findSuccessfulChatsRatio(new IntervalCriteria(dates[0], dates[1])), equalTo(new Long[]{1L, 
        		1L}));
        assertThat(chatDAO.findSuccessfulChatsRatio(new IntervalCriteria(dates[1], dates[3])), equalTo(new Long[]{1L, 
        		2L}));
        assertThat(chatDAO.findSuccessfulChatsRatio(new IntervalCriteria(dates[2], dates[3])), equalTo(new Long[]{1L, 
        		2L}));
        assertThat(chatDAO.findSuccessfulChatsRatio(new IntervalCriteria(dates[4], dates[4])), equalTo(new Long[]{0L, 
        		1L}));
        assertThat(chatDAO.findSuccessfulChatsRatio(new IntervalCriteria(dates[3], dates[4])), equalTo(new Long[]{0L, 
        		1L}));
        assertThat(chatDAO.findSuccessfulChatsRatio(new IntervalCriteria(dates[3], dates[5])), equalTo(new Long[]{1L, 
        		2L}));
        assertThat(chatDAO.findSuccessfulChatsRatio(new IntervalCriteria(dates[5], dates[6])), equalTo(new Long[]{0L, 
        		0L}));
    }

    @Test
    public void testFindChats() {
        Collection<Chat> actualResult, expectedResult;

        actualResult = chatDAO.findChats(new LoggingCriteria(dates[0], dates[1], ""));
        expectedResult = Arrays.asList(chats[0]);
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));

        actualResult = chatDAO.findChats(new LoggingCriteria(dates[0], dates[1], "rojo"));
        expectedResult = Arrays.asList();
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));

        actualResult = chatDAO.findChats(new LoggingCriteria(dates[1], dates[4], "hola"));
        expectedResult = Arrays.asList(chats[0], chats[2]);
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));

        actualResult = chatDAO.findChats(new LoggingCriteria(dates[1], dates[4], "roberto"));
        expectedResult = Arrays.asList();
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));

        actualResult = chatDAO.findChats(new LoggingCriteria(dates[5], dates[6], ""));
        expectedResult = Arrays.asList();
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));
    }

    @Test
    @SuppressWarnings("unchecked")
    public void testFindChatsPerDay() {
        Collection<ChatCount<Date>> actualResult, expectedResult;
        
        actualResult = chatDAO.findChatsPerDay(new IntervalCriteria(dates[0], dates[1]));
        expectedResult = Arrays.asList(new ChatCount<Date>(dates[1], 1));
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));
        
        
    }
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFindAvgChatTime() {
        Collection<ChatCount<Integer>> actualResult, expectedResult;

        actualResult = chatDAO.findAverageChatTime(new IntervalCriteria(dates[0], dates[1]));
        expectedResult = Arrays.asList(new ChatCount<Integer>(1, 1));
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));
        
        actualResult = chatDAO.findAverageChatTime(new IntervalCriteria(dates[0], dates[6]));
        expectedResult = Arrays.asList(new ChatCount<Integer>(0, 2), new ChatCount<Integer>(1, 2));
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));
        
    }
    
    
    @SuppressWarnings("unchecked")
    @Test
    public void testFindMostVisitedNodes() {
        Collection<ChatCount<String>> actualResult, expectedResult;

        actualResult = chatDAO.findMostVisitedNodes(new IntervalCriteria(dates[0], dates[1]));
        expectedResult = Arrays.asList(new ChatCount<String>("nodo0", 1));
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));

        actualResult = chatDAO.findMostVisitedNodes(new IntervalCriteria(dates[0], dates[6]));
        expectedResult = Arrays.asList(new ChatCount<String>("nodo0", 1), new ChatCount<String>("nodo1", 1));
        assertThat(actualResult.size(), equalTo(expectedResult.size()));
        assertTrue(actualResult.containsAll(expectedResult));

        actualResult = chatDAO.findMostVisitedNodes(new IntervalCriteria(dates[5], dates[6]));
        assertThat(actualResult.size(), equalTo(0));
    }
}
