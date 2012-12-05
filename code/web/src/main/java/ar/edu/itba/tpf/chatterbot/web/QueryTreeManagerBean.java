package ar.edu.itba.tpf.chatterbot.web;

import java.util.ArrayList;
import java.util.Collection;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.event.AbortProcessingException;
import javax.faces.model.SelectItem;

import org.hibernate.exception.ConstraintViolationException;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.event.NodeSelectedListener;
import org.richfaces.model.TreeNodeImpl;

import ar.edu.itba.tpf.chatterbot.exception.ChatterbotServiceException;
import ar.edu.itba.tpf.chatterbot.model.BaseAction;
import ar.edu.itba.tpf.chatterbot.model.InternalNode;
import ar.edu.itba.tpf.chatterbot.model.LeafNode;
import ar.edu.itba.tpf.chatterbot.model.TreeNode;
import ar.edu.itba.tpf.chatterbot.service.ChatterbotService;

public class QueryTreeManagerBean implements NodeSelectedListener {
    /* Servicio */
    private ChatterbotService chatterbotService;

    /* Nodo de richfaces para la raiz del árbol de decisión */
    private org.richfaces.model.TreeNode treeNode;

    /* Nodo que está siendo editado */
    private TreeNode currentNode;

    /**
     * Contruye el backingbean.
     * 
     * @param chatterbotService Servicio para acceder a funcionalidades del chatterbot.
     */
    public QueryTreeManagerBean(ChatterbotService chatterbotService) {
        this.chatterbotService = chatterbotService;

        /* LLenar el árbol */
        InternalNode rootNode = (InternalNode) chatterbotService.getRootNode();
        if (rootNode != null) {
            this.currentNode = rootNode;
            this.treeNode = fillNodeWithRoot(rootNode);
        }
    }

    /**
     * Callback que se ejecuta cuando se selecciona un nodo del árbol.
     * 
     * @param event Evento que acciona el callback.
     * @throws AbortProcessingException
     */
    public void processSelection(NodeSelectedEvent event) throws AbortProcessingException {
        HtmlTree tree = (HtmlTree) event.getComponent();
        if (tree.getRowData() != null) {
            currentNode = (TreeNode) tree.getRowData();
            System.out.println(currentNode);
        }
    }
    
    /**
     * Callback que se ejecuta cuando se guarda un nodo.
     */
    public void saveNode() {
        /* Persistir el nodo y actualizar el árbol de decisión */
        try {
            chatterbotService.persistTreeNode(currentNode);
        } catch (ConstraintViolationException e) {
            String message = "La descripción del nuevo nodo no puede ser la misma que la de otro ya existente.";
            FacesContext.getCurrentInstance().addMessage("tree",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        } catch (Exception e) {
            String message = "La descripción del nuevo nodo no puede ser la misma que la de otro ya existente.";
            FacesContext.getCurrentInstance().addMessage("tree",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        } finally {
            treeNode = fillNodeWithRoot(chatterbotService.getRootNode());
        }
    }

    /**
     * Callback que se ejecuta cuando se crea un nuevo nodo.
     */
    public void newNode() {
        /* Si el nodo era una hoja transformalo uno interno */
        LeafNode newNode = new LeafNode(null, "", "Nuevo Nodo", "", null);
        try {
            chatterbotService.addLeafNode(currentNode, newNode);
        } catch (ConstraintViolationException e) {
            String message = "La descripción del nuevo nodo no puede ser la misma que la de otro ya existente.";
            FacesContext.getCurrentInstance().addMessage("tree",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        } catch (Exception e) {
            String message = "La descripción del nuevo nodo no puede ser la misma que la de otro ya existente.";
            FacesContext.getCurrentInstance().addMessage("tree",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        } finally {
            treeNode = fillNodeWithRoot(chatterbotService.getRootNode());
        }

        currentNode = newNode;

        // chatterbotService.getTreeNodeByDescription(newNode.getDescription());
    }

    /**
     * Callback que se ejecuta cuando se quiere eliminar un nodo.
     */
    public void removeNode() {
        try {
            chatterbotService.removeTreeNode(currentNode);
        } catch (ChatterbotServiceException e) {
            FacesContext.getCurrentInstance().addMessage("tree",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
        }
        
        currentNode = chatterbotService.getRootNode();
        treeNode = fillNodeWithRoot(currentNode);
    }

    /**
     * Callback que se ejecuta para derminar si un nodo se debe expandir.
     * 
     * @param tree Árbol de decisón.
     * @return Siempre verdadero para expandir todos los nodos.
     */
    public boolean adviseNodeOpened(UITree tree) {
        return true;
    }

    /**
     * Retorna una colección con todos los nodos hijos del aŕbol de decisión.
     * 
     * @param currentNode Nodo del que se van a retornar todos los hijos.
     * @return Colección con todos los nodos hijos de cierto nodo.
     */
    private Collection<SelectItem> getAllNodes(TreeNode currentNode) {
        Collection<SelectItem> nodes = new ArrayList<SelectItem>();
        nodes.add(new SelectItem(currentNode));
        if (currentNode instanceof InternalNode) {
            for (TreeNode n : ((InternalNode) currentNode).getChildren()) {
                if (!(n instanceof LeafNode)) {
                    nodes.addAll(getAllNodes((InternalNode) n));
                } else {
                    nodes.add(new SelectItem(n));
                }
            }
        }
        return nodes;
    }

    /**
     * Wrapper de <code>fillNode</code> que agrega un nodo por sobre la raiz para que la raiz sea visible desde la
     * vista.
     * 
     * @param node Nodo a partir del cual se genera el árbol.
     * @return Árbol que se debe pasar a la vista.
     */
    private org.richfaces.model.TreeNode fillNodeWithRoot(TreeNode node) {
        org.richfaces.model.TreeNode tree = new TreeNodeImpl();
        tree.addChild(node.getDescription(), fillNode(node));
        return tree;

    }

    /**
     * Genera el objeto de richfaces con el árbol de decisión.
     * 
     * @param node Nodo a partir del cual se genera el árbol.
     * @return Árbol que se debe pasar a la vista.
     */
    private org.richfaces.model.TreeNode fillNode(TreeNode node) {
        org.richfaces.model.TreeNode ret = new TreeNodeImpl();
        ret.setData(node);
        if (node instanceof InternalNode && node.hasTransitions()) {
            for (TreeNode child : ((InternalNode) node).getChildren()) {
//                ret.addChild("Node" + nodeCount++, fillNode(child));
                ret.addChild(child.getDescription(), fillNode(child));
            }
        }
        return ret;
    }

    /**
     * @return Árbol de decisón para la vista.
     */
    public org.richfaces.model.TreeNode getTreeNode() {
        return treeNode;
    }

    public TreeNode getErrorTransition() {
        return currentNode.getErrorTransition();
    }

    /**
     * @return Nodo que está siendo editado.
     */
    public TreeNode getNode() {
        return currentNode;
    }

    /**
     * @return Transiciones de error (todos los nodos internos).
     */
    public Collection<SelectItem> getErrorTransitions() {
        Collection<SelectItem> errorTransitions = new ArrayList<SelectItem>();
        errorTransitions.add(new SelectItem(null));
        errorTransitions.addAll(getAllNodes(chatterbotService.getRootNode()));

        return errorTransitions;
    }

    /**
     * @return Colección con todas las acciones almacenadas.
     */
    public Collection<SelectItem> getActions() {
        Collection<SelectItem> actions = new ArrayList<SelectItem>();
        actions.add(new SelectItem(null));
        for (BaseAction baseAction : chatterbotService.getAllActions()) {
            actions.add(new SelectItem(baseAction));
        }

        return actions;
    }

    public boolean adviseNodeOpened() {
        return true;
    }
}
