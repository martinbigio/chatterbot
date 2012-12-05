package ar.edu.itba.tpf.chatterbot.dao;

import java.util.Collection;
import java.util.Date;
import java.util.List;

import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.Server;
import ar.edu.itba.tpf.chatterbot.support.ChatCount;
import ar.edu.itba.tpf.chatterbot.support.ServerCriteria;

/**
 * Implementación para Hibernate del objeto de acceso a datos de servidores.
 */
public class ServerDAOImpl extends GenericDAOImpl<Server, Long> implements ServerDAO {

    @Autowired
    public ServerDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    public Server findServerByName(String name) {
        List<Server> servers = (List<Server>) findByCriteria(Restrictions.like("name", name));
        if (servers.size() == 0) {
            return null;
        } else {
            return servers.get(0);
        }
    }

    @SuppressWarnings("unchecked")
    public Collection<ChatCount<Date>> findServerLoad(ServerCriteria serverCriteria) {
        return getHibernateTemplate().find(
                "select new ar.edu.itba.tpf.chatterbot.support.ChatCount(min(e.date), count(distinct c)) "
                        + " from Chat c inner join c.chatEntries e where e.date >= ? and e.date <= ? "
                        + " and c.server.name = ? "
                        + " group by year(e.date) || month(e.date) || day(e.date)",
                new Object[] {  serverCriteria.getFrom(), serverCriteria.getTo(), serverCriteria.getServer().getName() });
    }
}
