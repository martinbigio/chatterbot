package ar.edu.itba.tpf.chatterbot.dao;

import ar.edu.itba.tpf.chatterbot.model.TreeNode;

/**
 * Objeto de acceso a datos para nodos del árbol de consultas.
 */
public interface TreeNodeDAO extends GenericDAO<TreeNode, Long> {

    /**
     * Busca el nodo raíz del árbol de decisión del chatterbot (busca el único <code>TreeNode</code> que no tiene
     * padre).
     * 
     * @return Nodo raíz del árbol de decisión del chatterbot.
     */
    public TreeNode findRootNode();

    /**
     * Busca el nodo interno que tiene la descripción indicada.
     * 
     * @param description Descripción del nodo que se desea buscar.
     * @return Nodo interno con la descripción solicitada.
     */
    public TreeNode findTreeNodeByDescription(String description);
    
    public int getErrorTransitionsReferencesCount(TreeNode t);

}
