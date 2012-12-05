package ar.edu.itba.tpf.chatterbot.dao;

import java.util.List;

import org.apache.log4j.Logger;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Restrictions;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.TreeNode;

/**
 * Implementación para Hibernate del objeto de acceso a datos de nodos del árbol de consultas.
 */
public class TreeNodeDAOImpl extends GenericDAOImpl<TreeNode, Long> implements TreeNodeDAO {

    private static final Logger logger = Logger.getLogger(TreeNodeDAO.class);
    
    @Autowired
    public TreeNodeDAOImpl(SessionFactory sessionFactory) {
        super(sessionFactory);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.TreeNodeDAO#findRootNode()
     */
    public TreeNode findRootNode() {
        List<TreeNode> nodes = findByCriteria(Restrictions.isNull("parent"));
        if (nodes.size() == 0) {
            return null;
        } else {
//            logger.warn("Hay mas de un nodo raiz en la base de datos.");
            return (TreeNode) nodes.get(0);
        }
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.dao.TreeNodeDAO#findTreeNodeByDescription(String)
     */
    public TreeNode findTreeNodeByDescription(String description) {
        List<TreeNode> nodes = findByCriteria(Restrictions.eq("description", description));
        if (nodes.size() == 0) {
            return null;
        } else {
            return nodes.get(0);
        }
    }

    public int getErrorTransitionsReferencesCount(TreeNode t) {
        List<TreeNode> nodes = findByCriteria(Restrictions.eq("errorTransition", t));
        return nodes.size();
    }


}
