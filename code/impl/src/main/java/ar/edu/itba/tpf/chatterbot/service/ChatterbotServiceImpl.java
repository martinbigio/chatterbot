package ar.edu.itba.tpf.chatterbot.service;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;

import opennlp.tools.lang.spanish.PosTagger;
import opennlp.tools.lang.spanish.SentenceDetector;
import opennlp.tools.lang.spanish.TokenChunker;
import opennlp.tools.lang.spanish.Tokenizer;
import opennlp.tools.namefind.NameFinderME;

import org.apache.log4j.Logger;
import org.hibernate.exception.ConstraintViolationException;
import org.springframework.beans.factory.annotation.Required;

import ar.edu.itba.tpf.chatterbot.dao.BaseActionDAO;
import ar.edu.itba.tpf.chatterbot.dao.GlobalNodeDAO;
import ar.edu.itba.tpf.chatterbot.dao.TreeNodeDAO;
import ar.edu.itba.tpf.chatterbot.dao.WebServiceActionDAO;
import ar.edu.itba.tpf.chatterbot.exception.ChatterbotServiceException;
import ar.edu.itba.tpf.chatterbot.model.BaseAction;
import ar.edu.itba.tpf.chatterbot.model.GlobalNode;
import ar.edu.itba.tpf.chatterbot.model.InternalNode;
import ar.edu.itba.tpf.chatterbot.model.LeafNode;
import ar.edu.itba.tpf.chatterbot.model.TreeNode;
import ar.edu.itba.tpf.chatterbot.model.WebServiceAction;

/**
 * Implementación del servicio de chatterbot.
 */
public class ChatterbotServiceImpl implements ChatterbotService {

    private static final Logger logger = Logger.getLogger(ChatterbotServiceImpl.class);

    private GlobalNodeDAO globalNodeDAO;
    private WebServiceActionDAO webServiceActionDAO;
    private TreeNodeDAO treeNodeDAO;
    private BaseActionDAO baseActionDAO;

    private SentenceDetector sentenceDetector;
    private Tokenizer tokenizer;
    private TokenChunker tokenChunker;
    private PosTagger posTagger;

    private String sentenceFile;
    private String tokenizerFile;
    private String chunkerFile;
    private String taggerFile;

    @Required
    public void setGlobalNodeDAO(GlobalNodeDAO globalNodeDAO) {
        this.globalNodeDAO = globalNodeDAO;
    }

    @Required
    public void setWebServiceActionDAO(WebServiceActionDAO webServiceActionDAO) {
        this.webServiceActionDAO = webServiceActionDAO;
    }

    @Required
    public void setTreeNodeDAO(TreeNodeDAO treeNodeDAO) {
        this.treeNodeDAO = treeNodeDAO;
    }

    @Required
    public void setBaseActionDAO(BaseActionDAO baseActionDAO) {
        this.baseActionDAO = baseActionDAO;
    }

    @Required
    public void setChunkerFile(String chunkerFile) {
        this.chunkerFile = chunkerFile;
    }

    @Required
    public void setSentenceFile(String sentenceFile) {
        this.sentenceFile = sentenceFile;
    }

    @Required
    public void setTaggerFile(String taggerFile) {
        this.taggerFile = taggerFile;
    }

    @Required
    public void setTokenizerFile(String tokenizerFile) {
        this.tokenizerFile = tokenizerFile;
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ChatterbotService#getGlobalNodes()
     */
    public Collection<GlobalNode> getGlobalNodes() {
        return globalNodeDAO.findAll();
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ChatterbotService#getRootNode()
     */
    public TreeNode getRootNode() {
        return treeNodeDAO.findRootNode();
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ChatterbotService#getTransition(InternalNode, String)
     */
    public TreeNode getTransition(InternalNode currentNode, String message) throws ChatterbotServiceException {

        if (!currentNode.hasTransitions()) {
            return getRootNode();
        } else if (currentNode.getChildren().size() == 1) {
            return currentNode.getChildren().iterator().next();
        }

        Collection<String> keywords = getKeywords(message);
        TreeNode bestNode = null;
        float bestMatch = 0;

        for (TreeNode t : currentNode.getChildren()) {
            float currentMatch = t.matchKeywords(keywords);

            if (currentMatch > bestMatch) {
                bestNode = t;
                bestMatch = currentMatch;
            }
        }

        return bestNode;
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ChatterbotService#getGlobalTransition(String)
     */
    public GlobalNode getGlobalTransition(String message) throws ChatterbotServiceException {
        Collection<String> keywords = getAllKeywords(message.toLowerCase());
        GlobalNode bestNode = null;
        float bestMatch = 0;

        for (GlobalNode node : getGlobalNodes()) {
            float currentMatch = node.matchKeywords(keywords);
            
            if (currentMatch > bestMatch) {
                bestNode = node;
                bestMatch = currentMatch;
            }
        }

        return bestNode;

    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ChatterbotService#persistGlobalNode(GlobalNode)
     */
    public void persistGlobalNode(GlobalNode globalNode) {
        globalNodeDAO.save(globalNode);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ChatterbotService#persistTreeNode(TreeNode)
     */
    public void persistTreeNode(TreeNode treeNode) {
        treeNodeDAO.save(treeNode);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ChatterbotService#removeGlobalNode(GlobalNode)
     */
    public void removeGlobalNode(GlobalNode globalNode) {
        globalNodeDAO.delete(globalNode);
    }

    /**
     * Obtiene las keywords de un mensaje del usuario, utilizando el framework de procesamiento de lenguaje natural.
     * 
     * @param message Mensaje a procesar.
     * @return El conjunto de verbos y sustantivos del mensaje.
     */
    private Collection<String> getKeywords(String message) throws ChatterbotServiceException {

        message = message.toLowerCase();
        
        try {
            if (this.tokenChunker == null) {
                this.tokenChunker = new TokenChunker(chunkerFile);
                this.sentenceDetector = new SentenceDetector(sentenceFile);
                this.tokenizer = new Tokenizer(tokenizerFile);
                this.posTagger = new PosTagger(taggerFile);
            }
        } catch (IOException e) {
            logger.error("Error al cargar los diccionarios.");
            throw new ChatterbotServiceException("Error al cargar los diccionarios.", e);
        }

        Collection<String> keywords = new ArrayList<String>();
        String testInput = message;
        try {
            String[] sentences = sentenceDetector.sentDetect(testInput);

            for (String s : sentences) {
                String[] tokens = tokenizer.tokenize(s);
                String[] chunks = tokenChunker.find(tokens, Collections.EMPTY_MAP);
                StringBuilder parsedSentence = new StringBuilder();
                for (int i = 0; i < chunks.length; i++) {
                    if (i == 0) {
                        parsedSentence.append(tokens[i]);
                    } else if (chunks[i].equals(NameFinderME.CONTINUE)) {
                        parsedSentence.append("_" + tokens[i]);
                    } else {
                        parsedSentence.append(" " + tokens[i]);
                    }
                }

                String tags = posTagger.tag(parsedSentence.toString());
                String[] words = tags.split(" ");

                for (String w : words) {
                    String[] wp = w.split("/");

                    if (wp[1].charAt(0) == 'V' || wp[1].charAt(0) == 'N') {
                        keywords.add(wp[0]);
                    }
                }
            }
        } catch (Exception e) {
            logger.error("Error al procesar el mensaje.");
            throw new ChatterbotServiceException("Error al procesar el mensaje.", e);
        }
        return keywords;
    }

    
    /**
     * Obtiene las keywords de un mensaje del usuario, utilizando el framework de procesamiento de lenguaje natural.
     * 
     * @param message Mensaje a procesar.
     * @return El conjunto de verbos y sustantivos del mensaje.
     */
    private Collection<String> getAllKeywords(String message) throws ChatterbotServiceException {

        message = message.toLowerCase();
        
        try {
            if (this.tokenChunker == null) {
                this.tokenChunker = new TokenChunker(chunkerFile);
                this.sentenceDetector = new SentenceDetector(sentenceFile);
                this.tokenizer = new Tokenizer(tokenizerFile);
                this.posTagger = new PosTagger(taggerFile);
            }
        } catch (IOException e) {
            logger.error("Error al cargar los diccionarios.");
            throw new ChatterbotServiceException("Error al cargar los diccionarios.", e);
        }

        Collection<String> keywords = new ArrayList<String>();
        String testInput = message;
        try {
            String[] sentences = sentenceDetector.sentDetect(testInput);

            for (String s : sentences) {
                String[] tokens = tokenizer.tokenize(s);
                String[] chunks = tokenChunker.find(tokens, Collections.EMPTY_MAP);
                StringBuilder parsedSentence = new StringBuilder();
                for (int i = 0; i < chunks.length; i++) {
                    if (i == 0) {
                        parsedSentence.append(tokens[i]);
                    } else if (chunks[i].equals(NameFinderME.CONTINUE)) {
                        parsedSentence.append("_" + tokens[i]);
                    } else {
                        parsedSentence.append(" " + tokens[i]);
                    }
                }

                String tags = posTagger.tag(parsedSentence.toString());
                String[] words = tags.split(" ");

                for (String w : words) {
                    String[] wp = w.split("/");
                    keywords.add(wp[0]);
                }
            }
        } catch (Exception e) {
            logger.error("Error al procesar el mensaje.");
            throw new ChatterbotServiceException("Error al procesar el mensaje.", e);
        }
        return keywords;
    }
    
    
    public TreeNode getTreeNodeByDescription(String description) {
        return treeNodeDAO.findTreeNodeByDescription(description);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ChatterbotService#getActionByDescription(String)
     */
    public BaseAction getActionByDescription(String description) {
        return baseActionDAO.findBaseActionByDescription(description);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ChatterbotService#persistBaseAction(BaseAction)
     */
    public void persistBaseAction(BaseAction baseAction) {
        baseActionDAO.save(baseAction);
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ChatterbotService#findAllActions()
     */
    public Collection<BaseAction> getAllActions() {
        return baseActionDAO.findAll();
    }

    /**
     * @see ar.edu.itba.tpf.chatterbot.service.ChatterbotService#removeAction(BaseAction)
     */
    public void removeAction(BaseAction action) {
        baseActionDAO.delete(action);
    }

    public void persistWebserviceAction(WebServiceAction action) {
        webServiceActionDAO.save(action);
    }

    public void removeWebServiceAction(WebServiceAction action) {
        webServiceActionDAO.delete(action);

    }

    public int getErrorTransitionReferencesCount(TreeNode t) {
        int count = treeNodeDAO.getErrorTransitionsReferencesCount(t);
        if (t instanceof InternalNode && ((InternalNode) t).hasTransitions()) {
            for (TreeNode c : ((InternalNode) t).getChildren()) {
                count += getErrorTransitionReferencesCount(c);
            }
        }
        return count;
    }

    public void addLeafNode(TreeNode father, LeafNode child) throws ConstraintViolationException {
        TreeNode modifiedNode = null;

        /* Si el nodo era una hoja transformalo uno interno */
        if (father instanceof LeafNode) {
            InternalNode parentNode = father.getParent();
            parentNode.removeChild(father);
            father.setDescription("#" + Math.random() + "$");
            persistTreeNode(parentNode);

            /* Forzar delete en la base para que despues no falle el unique constraint */
            treeNodeDAO.delete(father);

            father = new InternalNode(father.getAction(), father.getKeywords(), father.getDescription(), father
                    .getAnswer(), father.getErrorTransition());
            parentNode.addChild(father);
            modifiedNode = parentNode;
        } else {
            modifiedNode = father;
        }

        ((InternalNode) father).addChild(child);

        /* Persistir el nodo más cercano a la raiz que fue modificado */
        persistTreeNode(modifiedNode);
    }

    // public void removeTreeNode(TreeNode node) throws ChatterbotServiceException {
    // if (node.equals(getRootNode())) {
    // throw new ChatterbotServiceException("No se puede eliminar el nodo raiz.", null);
    // } else {
    // if (getErrorTransitionReferencesCount(node) > 0) {
    // throw new ChatterbotServiceException(
    // "No se puede eliminar el nodo porque existen otros que lo referencian como transición de error.",
    // null);
    // } else {
    // InternalNode parent = node.getParent();
    // parent.removeChild(node);
    // persistTreeNode(parent);
    //
    // /* Si se quedo sin hijos debe pasar a ser un nodo hoja! (a menos que sea la raiz) */
    // if (!parent.hasTransitions()) {
    // InternalNode ancestor = parent.getParent();
    // if (ancestor != null) {
    // /* parent no es la raiz */
    // LeafNode newNode = new LeafNode(parent.getAction(), parent.getKeywords(), parent
    // .getDescription(), parent.getAnswer(), parent.getErrorTransition());
    //
    // ancestor.removeChild(parent);
    // persistTreeNode(ancestor);
    // /* Obligar que se haga el delete! */
    // getTreeNodeByDescription(parent.getDescription());
    //
    // ancestor.addChild(newNode);
    // persistTreeNode(ancestor);
    // }
    // }
    // }
    // }
    // }
    public void removeTreeNode(TreeNode node) throws ChatterbotServiceException {
        if (node.equals(getRootNode())) {
            throw new ChatterbotServiceException("No se puede eliminar el nodo raiz.", null);
        } else {
            if (getErrorTransitionReferencesCount(node) > 0) {
                throw new ChatterbotServiceException(
                        "No se puede eliminar el nodo porque existen otros que lo referencian como transición de error.",
                        null);
            } else {
                InternalNode parent = node.getParent();
                parent.removeChild(node);
                persistTreeNode(parent);

                // /* Si se quedo sin hijos debe pasar a ser un nodo hoja! (a menos que sea la raiz) */
                // if (!parent.hasTransitions()) {
                // InternalNode ancestor = parent.getParent();
                // if (ancestor != null) {
                // /* parent no es la raiz */
                // LeafNode newNode = new LeafNode(parent.getAction(), parent.getKeywords(), parent
                // .getDescription(), parent.getAnswer(), parent.getErrorTransition());
                //
                // ancestor.removeChild(parent);
                // persistTreeNode(ancestor);
                // /* Obligar que se haga el delete! */
                // getTreeNodeByDescription(parent.getDescription());
                //
                // ancestor.addChild(newNode);
                // persistTreeNode(ancestor);
                // }
                // }
            }
        }
    }

}
