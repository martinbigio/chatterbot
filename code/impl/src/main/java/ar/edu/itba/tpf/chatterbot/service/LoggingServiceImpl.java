package ar.edu.itba.tpf.chatterbot.service;

import java.util.Collection;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.dao.ChatDAO;
import ar.edu.itba.tpf.chatterbot.dao.ErrorLogDAO;
import ar.edu.itba.tpf.chatterbot.model.Chat;
import ar.edu.itba.tpf.chatterbot.model.ErrorLog;
import ar.edu.itba.tpf.chatterbot.support.ChatCount;
import ar.edu.itba.tpf.chatterbot.support.IntervalCriteria;
import ar.edu.itba.tpf.chatterbot.support.LoggingCriteria;

/**
 * Implementación del servicio de logging.
 */
public class LoggingServiceImpl implements LoggingService {

    private ChatDAO chatDAO;
    private ErrorLogDAO errorLogDAO;

    @Required
    public void setChatDAO(ChatDAO chatDAO) {
        this.chatDAO = chatDAO;
    }

    @Required
    public void setErrorLogDAO(ErrorLogDAO errorLogDAO) {
        this.errorLogDAO = errorLogDAO;
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.LoggingService#getAverageChatTime(IntervalCriteria)
     */
    public Map<String, Long> getAverageChatTime(IntervalCriteria intervalCriteria) {
        Collection<ChatCount<Integer>> result = chatDAO.findAverageChatTime(intervalCriteria);
        Map<String, Long> ret = new HashMap<String, Long>();
        
        for (ChatCount<Integer> c : result) {
            ret.put(c.getCriteria() + " minuto" + (c.getCriteria() == 1 ? "" : "s"), c.getCount());
        }
        return ret;
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.LoggingService#getChats(LoggingCriteria)
     */
    public Collection<Chat> getChats(LoggingCriteria loggingCriteria) {
        return chatDAO.findChats(loggingCriteria);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.LoggingService#getChatsPerDay(IntervalCriteria)
     */
    public Map<Date, Long> getChatsPerDay(IntervalCriteria intervalCriteria) {
        Collection<ChatCount<Date>> data = chatDAO.findChatsPerDay(intervalCriteria);
        Map<Date, Long> result = new HashMap<Date, Long>();
        
        for (ChatCount<Date> cc : data) {
            result.put(cc.getCriteria(), cc.getCount());
        }
        return result;
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.LoggingService#getSuccessfulChatsRatio(IntervalCriteria)
     */
    public Long[] getSuccessfulChatsRatio(IntervalCriteria intervalCriteria) {
        return chatDAO.findSuccessfulChatsRatio(intervalCriteria);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.LoggingService#persistChat(Chat)
     */
    public void persistChat(Chat chat) {
        chatDAO.save(chat);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.LoggingService#getErrorLogs(IntervalCriteria)
     */
    public Collection<ErrorLog> getErrorLogs(IntervalCriteria intervalCriteria) {
        return errorLogDAO.findErrorLogs(intervalCriteria);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.LoggingService#getMostVisitedNodes(IntervalCriteria)
     */
    public Map<String, Long> getMostVisitedNodes(IntervalCriteria intervalCriteria) {
        Collection<ChatCount<String>> result = chatDAO.findMostVisitedNodes(intervalCriteria);
        Map<String, Long> ret = new HashMap<String, Long>();
        
        for (ChatCount<String> c : result) {
            ret.put(c.getCriteria(), c.getCount());
        }
        return ret;
    }
    
    
    /**
     * @see ar.edu.itba.tpf.chatterbot.service.LoggingService#persistErrorLog(ErrorLog)
     */
    public void persistErrorLog(ErrorLog errorLog) {
        this.errorLogDAO.save(errorLog);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.LoggingService#removeErrorLog(ErrorLog)
     */
    public void removeErrorLog(ErrorLog errorLog) {
        this.errorLogDAO.delete(errorLog);
    }

}
