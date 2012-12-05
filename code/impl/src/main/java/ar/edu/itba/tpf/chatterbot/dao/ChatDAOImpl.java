package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Collection;
import java.util.Date;

import org.hibernate.SessionFactory;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.Chat;
import ar.edu.itba.tpf.chatterbot.support.ChatCount;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;
import ar.edu.itba.tpf.chatterbot.support.LoggingCriteria;

/**
 * Implementación para Hibernate del objeto de acceso a datos de chats.
 */
public class ChatDAOImpl extends GenericDAOImpl<Chat, Long> implements ChatDAO {

    @Autowired
    public ChatDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.ChatDAO#findAverageChatTime(IntervalCriteria)
     */
    @SuppressWarnings("unchecked")
    public Collection<ChatCount<Integer>> findAverageChatTime(IntervalCriteria intervalCriteria) {
        return getHibernateTemplate().find(
                "select new ar.edu.itba.tpf.chatterbot.support.ChatCount(c.length, count(distinct c)) from "
                        + " Chat c inner join c.chatEntries e "
                        + "where e.date >= ? and e.date <= ? group by c.length ", 
                        new Object[] { intervalCriteria.getFrom(), intervalCriteria.getTo() });
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.ChatDAO#findChats(LoggingCriteria)
     */
    @SuppressWarnings("unchecked")
    public Collection<Chat> findChats(LoggingCriteria loggingCriteria) {
        return getHibernateTemplate().find(
                "select distinct c from Chat c inner join c.chatEntries e "
                        + "where e.date >= ? and e.date <= ? and e.message like ? ",
                new Object[] { loggingCriteria.getFrom(), loggingCriteria.getTo(), "%" + loggingCriteria.getKeywords() + "%"});
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.ChatDAO#findChatsPerDay(IntervalCriteria)
     */
    @SuppressWarnings("unchecked")
    public Collection<ChatCount<Date>> findChatsPerDay(IntervalCriteria intervalCriteria) {
        return getHibernateTemplate().find(
                "select new ar.edu.itba.tpf.chatterbot.support.ChatCount(min(e.date), count(distinct c)) "
                        + " from Chat c inner join c.chatEntries e where e.date >= ? and e.date <= ? "
                        + " group by year(e.date) || month(e.date) || day(e.date)", new Object[] { intervalCriteria.getFrom(), intervalCriteria.getTo() });
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.ChatDAO#findSuccessfulChatsRatio(IntervalCriteria)
     */
    public Long[] findSuccessfulChatsRatio(IntervalCriteria intervalCriteria) {
        long successfulChats = (Long) getHibernateTemplate().find(
                "select count(distinct c) from Chat c inner join c.chatEntries e "
                        + "where is_successful = true and e.date >= ? and e.date <= ?",
                new Object[] { intervalCriteria.getFrom(), intervalCriteria.getTo() }).get(0);
        long totalChats = (Long) getHibernateTemplate().find(
                "select count(distinct c) from Chat c inner join c.chatEntries e "
                        + "where e.date >= ? and e.date <= ?",
                new Object[] { intervalCriteria.getFrom(), intervalCriteria.getTo() }).get(0);
        /*if (totalChats == 0) {
            return -1.0f;
        } */
        return new Long[]{successfulChats, totalChats};//successfulChats / (float) totalChats;
    }
    
    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.TreeNodeDAO#findMostVisitedNodes(IntervalCriteria)
     */
    @SuppressWarnings("unchecked")
    public Collection<ChatCount<String>> findMostVisitedNodes(IntervalCriteria intervalCriteria) {
        return getHibernateTemplate().find(
                "select new ar.edu.itba.tpf.chatterbot.support.ChatCount(c.finalNode, count(distinct c)) from "
                        + " Chat c inner join c.chatEntries e "
                        + "where c.successful = true and e.date >= ? and e.date <= ? group by c.finalNode",
                new Object[] { intervalCriteria.getFrom(), intervalCriteria.getTo() });
    }
}
