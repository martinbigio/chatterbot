package ar.edu.itba.tpf.chatterbot.dao;

import ar.edu.itba.tpf.chatterbot.model.BaseAction;

public interface BaseActionDAO extends GenericDAO<BaseAction, Long> {

    /**
     * Dada la descripción de una acción la busca.
     * 
     * @param description Descrición de la acción que se quiere buscar.
     * @return Acción con la descripción indicada.
     */
    public BaseAction findBaseActionByDescription(String description);

}
