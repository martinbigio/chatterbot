package ar.edu.itba.tpf.chatterbot.service;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;

import java.util.Arrays;
import java.util.Collection;

import org.jmock.Expectations;
import org.jmock.Mockery;
import org.jmock.integration.junit4.JMock;
import org.jmock.integration.junit4.JUnit4Mockery;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;

import ar.edu.itba.tpf.chatterbot.dao.GlobalNodeDAO;
import ar.edu.itba.tpf.chatterbot.dao.TreeNodeDAO;
import ar.edu.itba.tpf.chatterbot.model.GlobalNode;
import ar.edu.itba.tpf.chatterbot.model.InternalNode;
import ar.edu.itba.tpf.chatterbot.model.LeafNode;
import ar.edu.itba.tpf.chatterbot.model.TreeNode;

@RunWith(JMock.class)
public class ChatterbotServiceTest {

    private Mockery context;
    private ChatterbotServiceImpl chatterbotService;
    
    private GlobalNodeDAO globalNodeDAO;
    private TreeNodeDAO treeNodeDAO;
  
    private GlobalNode globalNode = new GlobalNode(null, "keywords", "descr", "answer");
    private TreeNode treeNode = new InternalNode(null, "keywords", "descr", "answer", null);
    
    @Before
    public void setup() {
        context = new JUnit4Mockery();

        chatterbotService = new ChatterbotServiceImpl();
        globalNodeDAO = context.mock(GlobalNodeDAO.class);
        treeNodeDAO = context.mock(TreeNodeDAO.class);
        
        chatterbotService.setGlobalNodeDAO(globalNodeDAO);
        chatterbotService.setTreeNodeDAO(treeNodeDAO);
        chatterbotService.setChunkerFile("../server/src/main/resources/models/tokenize/SpanishTokChunk.bin.gz");
        chatterbotService.setSentenceFile("../server/src/main/resources/models/sentdetect/SpanishSent.bin.gz");
        chatterbotService.setTaggerFile("../server/src/main/resources/models/postag/SpanishPOS.bin.gz");
        chatterbotService.setTokenizerFile("../server/src/main/resources/models/tokenize/SpanishTok.bin.gz");
    }

    @Test
    public void testGetGlobalNodes() {
        final Collection<GlobalNode> globalNodes = Arrays.asList(globalNode);
        
        context.checking(new Expectations() {
            {
                one(globalNodeDAO).findAll();
                will(returnValue(globalNodes));
            }
        });
        assertThat(chatterbotService.getGlobalNodes(), equalTo(globalNodes));
    }

    @Test
    public void testGetRootNode() {
        context.checking(new Expectations() {
            {
                one(treeNodeDAO).findRootNode();
                will(returnValue(treeNode));
            }
        });
        assertThat(chatterbotService.getRootNode(), equalTo(treeNode));
    }
 
    @Test
    public void testGetTransition() throws Exception {
        InternalNode startNode = new InternalNode(null, "keywords", "start node", "answer", null);
        TreeNode child1 = new LeafNode(null, "saldo cuenta", "description", "answer", null);
        TreeNode child2 = new LeafNode(null, "saldo", "description", "answer", null);
        TreeNode child3 = new LeafNode(null, "horario", "description", "answer", null);
        
        startNode.addChild(child1);
        startNode.addChild(child2);
        startNode.addChild(child3);
        
        assertThat(chatterbotService.getTransition(startNode, "quiero saber el saldo de mi cuenta"), equalTo(child1));
        assertThat(chatterbotService.getTransition(startNode, "quiero saber el horario de atencion"), equalTo(child3));
        assertThat(chatterbotService.getTransition(startNode, "lalalal"), equalTo(null));
        
    }
    
    @Test
    public void testGetGlobalTransition() throws Exception {
        
        GlobalNode node1 = new GlobalNode(null, "hola", "description", "que tal?");
        GlobalNode node2 = new GlobalNode(null, "gracias", "description", "de nada");
        GlobalNode node3 = new GlobalNode(null, "adios", "description", "hasta pronto");
        
        final Collection<GlobalNode> globalNodes = Arrays.asList(node1, node2, node3);
        
        context.checking(new Expectations() {
            {
                allowing(globalNodeDAO).findAll();
                will(returnValue(globalNodes));
            }
        });
        
        assertThat(chatterbotService.getGlobalTransition("hola que tal?"), equalTo(node1));
        assertThat(chatterbotService.getGlobalTransition("muchisimas gracias"), equalTo(node2));
        assertThat(chatterbotService.getGlobalTransition("adios, hasta luego"), equalTo(node3));
        assertThat(chatterbotService.getGlobalTransition("lalala"), equalTo(null));
        
    }
    
    @Test
    public void testPersistGlobalNode() {
        context.checking(new Expectations() {
            {
                one(globalNodeDAO).save(globalNode);
            }
        });
        chatterbotService.persistGlobalNode(globalNode);
    }

    @Test
    public void testPersistTreeNode() {
        context.checking(new Expectations() {
            {
                one(treeNodeDAO).save(treeNode);
            }
        });
        chatterbotService.persistTreeNode(treeNode);
    }

    @Test
    public void testRemoveGlobalNode() {
        context.checking(new Expectations() {
            {
                one(globalNodeDAO).delete(globalNode);
            }
        });
        chatterbotService.removeGlobalNode(globalNode);
    }

}
