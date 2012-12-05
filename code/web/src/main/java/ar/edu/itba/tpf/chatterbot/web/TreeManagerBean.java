package ar.edu.itba.tpf.chatterbot.web;

import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;

import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.model.SelectItem;

import org.hibernate.exception.ConstraintViolationException;
import org.richfaces.component.UITree;
import org.richfaces.component.html.HtmlTree;
import org.richfaces.component.state.TreeStateAdvisor;
import org.richfaces.event.NodeSelectedEvent;
import org.richfaces.model.TreeNode;
import org.richfaces.model.TreeNodeImpl;
import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.exception.ChatterbotServiceException;
import ar.edu.itba.tpf.chatterbot.model.BaseAction;
import ar.edu.itba.tpf.chatterbot.model.InternalNode;
import ar.edu.itba.tpf.chatterbot.model.LeafNode;
import ar.edu.itba.tpf.chatterbot.service.ChatterbotService;

public class TreeManagerBean implements TreeStateAdvisor {

    private static ResourceBundle rb;

    {
        rb = ResourceBundle.getBundle("ar.edu.itba.tpf.chatterbot.ErrorMessages");
    }

    private ChatterbotService chatterbotService;
    private TreeNode selectedNode = null;
    private TreeNode rootNode = null;
    private long counter = 1;

    @Required
    public void setChatterbotService(ChatterbotService chatterbotService) {
        this.chatterbotService = chatterbotService;
        loadTree();
    }

    public void loadTree() {
        rootNode = new TreeNodeImpl();
        loadTree(rootNode, this.chatterbotService.getRootNode());
    }

    private void loadTree(TreeNode root, ar.edu.itba.tpf.chatterbot.model.TreeNode treeNode) {

        if (root == null || treeNode == null) {
            return;
        }

        TreeNode newRootNode = attachTreeNode(root, treeNode);

        if (treeNode instanceof InternalNode) {
            InternalNode internalNode = (InternalNode) treeNode;

            Collection<ar.edu.itba.tpf.chatterbot.model.TreeNode> childs = internalNode.getChildren();

            for (ar.edu.itba.tpf.chatterbot.model.TreeNode childTreeNode : childs) {
                loadTree(newRootNode, childTreeNode);
            }
        }
    }

    public void processSelection(NodeSelectedEvent event) {
        HtmlTree tree = (HtmlTree) event.getComponent();
        this.selectedNode = tree.getTreeNode(tree.getRowKey());
    }

    public TreeNode attachTreeNode(TreeNode parent, ar.edu.itba.tpf.chatterbot.model.TreeNode treeNode) {
        if (parent == null || treeNode == null) {
            return null;
        }

        TreeNode newTreeNode = new TreeNodeImpl();
        newTreeNode.setData(treeNode);
        parent.addChild(counter++, newTreeNode);

        return newTreeNode;
    }

    public void newNode() {

        if (selectedNode == null || getSelectedNodeData() == null) {
            String message = rb.getString("ar.edu.itba.tpf.chatterbot.queryTree.emptySelection");
            FacesContext.getCurrentInstance().addMessage("tree",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
            return;
        }

        // LeafNode newNodeLeaf = new LeafNode(null, "", "Nuevo Nodo", "", null);
        // TreeNode newNode = this.attachTreeNode(selectedNode, newNodeLeaf);
        //
        // try {
        // chatterbotService.addLeafNode(chatterbotService.getTreeNodeByDescription(getSelectedNodeData().getDescription(
        // )), newNodeLeaf);

        InternalNode newInternalNode = new InternalNode(null, "", "Nuevo Nodo", "Nueva respuesta", null);
        TreeNode newNode = this.attachTreeNode(selectedNode, newInternalNode);
        ((InternalNode) getSelectedNodeData()).addChild(newInternalNode);
        try {
            chatterbotService.persistTreeNode(getSelectedNodeData());
        } catch (ConstraintViolationException e) {
            String message = rb.getString("ar.edu.itba.tpf.chatterbot.queryTree.duplicateNode");
            FacesContext.getCurrentInstance().addMessage("tree",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        } catch (Exception e) {
            String message = rb.getString("ar.edu.itba.tpf.chatterbot.queryTree.duplicateNode");
            FacesContext.getCurrentInstance().addMessage("tree",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        }

        newNode = this.attachTreeNode(selectedNode, chatterbotService.getTreeNodeByDescription(newInternalNode
                .getDescription()));
        this.selectedNode = newNode;
        loadTree();
    }

    public ar.edu.itba.tpf.chatterbot.model.TreeNode getSelectedNodeData() {
        if (selectedNode == null) {
            return null;
        }

        return (ar.edu.itba.tpf.chatterbot.model.TreeNode) selectedNode.getData();
    }

    public TreeNode getSelectedNode() {
        return selectedNode;
    }

    public void saveNode() {
        if (selectedNode == null || selectedNode.getData() == null) {
            String message = rb.getString("ar.edu.itba.tpf.chatterbot.queryTree.emptySelection");
            FacesContext.getCurrentInstance().addMessage("tree",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        } else {
            ar.edu.itba.tpf.chatterbot.model.TreeNode selectedData = (ar.edu.itba.tpf.chatterbot.model.TreeNode) selectedNode
                    .getData();
            if (selectedData.getErrorTransition() != null && selectedData.getErrorTransition().equals(selectedData)) {
                String message = rb.getString("ar.edu.itba.tpf.chatterbot.queryTree.invalidErrorTransition");
                FacesContext.getCurrentInstance().addMessage("tree",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
                selectedData.setErrorTransition(null);
                return;
            }
            try {
                chatterbotService.persistTreeNode((ar.edu.itba.tpf.chatterbot.model.TreeNode) selectedNode.getData());
            } catch (ConstraintViolationException e) {
                String message = rb.getString("ar.edu.itba.tpf.chatterbot.queryTree.duplicateNode");
                FacesContext.getCurrentInstance().addMessage("tree",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
            } catch (Exception e) {
                String message = rb.getString("ar.edu.itba.tpf.chatterbot.queryTree.duplicateNode");
                FacesContext.getCurrentInstance().addMessage("tree",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
            }
            loadTree();
        }
    }

    public void removeNode() {
        if (selectedNode != null) {
            try {
                chatterbotService.removeTreeNode((ar.edu.itba.tpf.chatterbot.model.TreeNode) selectedNode.getData());
            } catch (ChatterbotServiceException e) {
                FacesContext.getCurrentInstance().addMessage("tree",
                        new FacesMessage(FacesMessage.SEVERITY_ERROR, e.getMessage(), e.getMessage()));
            }
            selectedNode = null;
            loadTree();
        } else {
            String message = rb.getString("ar.edu.itba.tpf.chatterbot.queryTree.emptySelection");
            FacesContext.getCurrentInstance().addMessage("tree",
                    new FacesMessage(FacesMessage.SEVERITY_ERROR, message, message));
        }
    }

    public Boolean adviseNodeOpened(UITree tree) {
        return true;
    }

    public Boolean adviseNodeSelected(UITree tree) {

        if (tree != null && selectedNode != null) {
            TreeNode treeNode = tree.getTreeNode();

            if (treeNode.getData().equals(selectedNode.getData())) {
                return true;
            }
        }

        return null;
    }

    /**
     * Retorna una colección con todos los nodos hijos del aŕbol de decisión.
     * 
     * @param currentNode Nodo del que se van a retornar todos los hijos.
     * @return Colección con todos los nodos hijos de cierto nodo.
     */
    private Collection<SelectItem> getAllNodes(ar.edu.itba.tpf.chatterbot.model.TreeNode currentNode) {
        Collection<SelectItem> nodes = new ArrayList<SelectItem>();
        nodes.add(new SelectItem(currentNode));
        if (currentNode instanceof InternalNode) {
            for (ar.edu.itba.tpf.chatterbot.model.TreeNode n : ((InternalNode) currentNode).getChildren()) {
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

    public TreeNode getData() {
        return rootNode;
    }

    public String getLoadTree() {
        loadTree();
        return "";
    }
}
