package ar.edu.itba.tpf.chatterbot.model;

import java.util.Collection;

/**
 * Nodo del árbol de consultas o nodo global.
 */
public interface Node {

    /**
     * @return Porcentaje de coincidencias entre keywords del usuario y las de este nodo.
     */
    public float matchKeywords(Collection<String> keywords);
}
