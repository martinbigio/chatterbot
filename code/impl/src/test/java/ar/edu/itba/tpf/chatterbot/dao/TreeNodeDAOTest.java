package ar.edu.itba.tpf.chatterbot.dao;

import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import ar.edu.itba.tpf.chatterbot.model.InternalNode;
import ar.edu.itba.tpf.chatterbot.model.LeafNode;
import ar.edu.itba.tpf.chatterbot.model.TreeNode;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class TreeNodeDAOTest extends GenericDAOTest<TreeNode> {

    @Autowired
    private TreeNodeDAO treeNodeDAO;
    
    @Override
    protected GenericDAO<TreeNode, Long> getDao() {
        return treeNodeDAO;
    }
    @Override
    protected TreeNode getSample() {
        return new InternalNode(null, "test", "test", "test", null);
    }
    @Override
    protected void updateSample(TreeNode sample) {
        sample.setDescription("test2");
    }
    
    @Test
    public void testFindRootNode() {
        InternalNode rootNode = new InternalNode(null, "keywords", "root", "answer", null);
        LeafNode leftSon = new LeafNode(null, "leftkeywords", "left", "answer", null);
        LeafNode rightSon = new LeafNode(null, "rightkeywords", "right", "answer", null);
        rootNode.addChild(leftSon);
        rootNode.addChild(rightSon);
        treeNodeDAO.save(rootNode);
        assertThat(treeNodeDAO.findRootNode(), equalTo((TreeNode)rootNode));
    }
    
    @Test
    public void testFindTreeNodeByDescription() {
        InternalNode rootNode = new InternalNode(null, "keywords", "root", "answer", null);
        LeafNode leftSon = new LeafNode(null, "leftkeywords", "left", "answer", null);
        LeafNode rightSon = new LeafNode(null, "rightkeywords", "right", "answer", null);
        rootNode.addChild(leftSon);
        rootNode.addChild(rightSon);
        treeNodeDAO.save(rootNode);
        assertThat(treeNodeDAO.findTreeNodeByDescription("left"), equalTo((TreeNode)leftSon));
        assertThat(treeNodeDAO.findTreeNodeByDescription("right"), equalTo((TreeNode)rightSon));
        assertThat(treeNodeDAO.findTreeNodeByDescription("root"), equalTo((TreeNode)rootNode));
        assertThat(treeNodeDAO.findTreeNodeByDescription("foo"), equalTo(null));
    }
    
    @Test
    public void testGetErrorTransitionsReferenceCount() {
        InternalNode rootNode = new InternalNode(null, "keywords", "root", "answer", null);
        LeafNode leftSon = new LeafNode(null, "leftkeywords", "left", "answer", null);
        LeafNode rightSon = new LeafNode(null, "rightkeywords", "right", "answer", null);
        rootNode.addChild(leftSon);
        rootNode.addChild(rightSon);
        treeNodeDAO.save(rootNode);
        
        leftSon.setErrorTransition(rootNode);
        treeNodeDAO.save(leftSon);
        rightSon.setErrorTransition(rootNode);
        treeNodeDAO.save(rightSon);
        
        treeNodeDAO.save(rootNode);
        
        assertThat(treeNodeDAO.getErrorTransitionsReferencesCount(rootNode), equalTo(2));
        assertThat(treeNodeDAO.getErrorTransitionsReferencesCount(leftSon), equalTo(0));
        assertThat(treeNodeDAO.getErrorTransitionsReferencesCount(rightSon), equalTo(0));
        
    }
}
