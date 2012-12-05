package ar.edu.itba.tpf.chatterbot.model;

import java.util.List;

/**
 * Una acción a realizar al pasar por un nodo en el árbol de consultas.
 */
public interface Action {

    /**
     * Ejecuta la acción y deja los resultados en el mapa suministrado.
     * 
     * @param keys
     * @param values
     */
    public void execute(List<String> keys, List<String> values);
}
