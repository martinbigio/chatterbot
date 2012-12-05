package ar.edu.itba.tpf.chatterbot.service;

import java.util.Collection;

import org.hibernate.exception.ConstraintViolationException;

import ar.edu.itba.tpf.chatterbot.exception.ChatterbotServiceException;
import ar.edu.itba.tpf.chatterbot.model.BaseAction;
import ar.edu.itba.tpf.chatterbot.model.GlobalNode;
import ar.edu.itba.tpf.chatterbot.model.InternalNode;
import ar.edu.itba.tpf.chatterbot.model.LeafNode;
import ar.edu.itba.tpf.chatterbot.model.TreeNode;
import ar.edu.itba.tpf.chatterbot.model.WebServiceAction;

/**
 * Servicios para el chatterbot.
 */
public interface ChatterbotService {

    /**
     * Almacena un nodo global.
     * 
     * @param node Nodo que se desea almacenar.
     */
    public void persistGlobalNode(GlobalNode globalNode);

    /**
     * Almacena un nodo del árbol de consultas.
     * 
     * @param node Nodo que se desea almacenar.
     */
    public void persistTreeNode(TreeNode treeNode);

    /**
     * Almacena una acción.
     * 
     * @param baseAction Acción que se desea almacenar.
     */
    public void persistBaseAction(BaseAction baseAction);

    /**
     * Elimina un nodo global.
     * 
     * @param node Nodo que se desea almacenar.
     */
    public void removeGlobalNode(GlobalNode node);

    /**
     * Elimina una acción
     * 
     * @param action Acción que se desea almacenar.
     */
    public void removeAction(BaseAction action);

    /**
     * Obtiene una colección con todos los nodos globales.
     * 
     * @return Colección con todos los nodos globales.
     */
    public Collection<GlobalNode> getGlobalNodes();

    /**
     * Obtiene el nodo raíz del árbol de decisión del chatterbot. Dicho nodo es el único que no tiene padre.
     * 
     * @return Nodo raíz del árbol de decisión del chatterbot.
     */
    public TreeNode getRootNode();

    /**
     * Obtiene el proximo nodo (del árbol de decisión o bien global), al que debe pasar el chatterbot.
     * 
     * @param currentNode Nodo en el que se encuentra el chatterbot.
     * @param message Mensaje del cliente.
     * @return Nodo al que debe pasar el chatterbot.
     */
    public TreeNode getTransition(InternalNode currentNode, String message) throws ChatterbotServiceException;

    /**
     * Busca si el mensaje matchea con un nodo global, y de ser así lo retorna.
     * 
     * @param message Mensaje del cliente.
     * @return Nodo global que matchea, o null si no hay ninguno.
     */
    public GlobalNode getGlobalTransition(String message) throws ChatterbotServiceException;

    /**
     * Obtiene el nodo que tiene la descripción indicada.
     * 
     * @param description Descripción que tiene el nodo buscado.
     * @return Nodo con la descripción indicada.
     */
    public TreeNode getTreeNodeByDescription(String description);

    /**
     * Obtiene la acción que tiene la descripción indicada.
     * 
     * @param description Descripción que tiene la acción buscada.
     * @return Nodo Acción con la descripción indicada.
     */
    public BaseAction getActionByDescription(String description);

    /**
     * @return Conjunto con todas las acciones.
     */
    public Collection<BaseAction> getAllActions();

    public void persistWebserviceAction(WebServiceAction action);

    public void removeWebServiceAction(WebServiceAction action);

    public int getErrorTransitionReferencesCount(TreeNode t);

    /**
     * Le agrega al nodo padre el nodo hoja hijo recibido
     * 
     * @param father
     * @param child
     * @throws ConstraintViolationException En caso de que ya exista un nodo con el mismo nombre que el que se desea
     *                 agregar.
     */
    public void addLeafNode(TreeNode father, LeafNode child) throws ConstraintViolationException;
    
    public void removeTreeNode(TreeNode node) throws ChatterbotServiceException;
}
