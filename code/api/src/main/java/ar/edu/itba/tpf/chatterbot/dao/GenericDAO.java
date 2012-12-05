package ar.edu.itba.tpf.chatterbot.dao;

import java.io.Serializable;
import java.util.Collection;

import ar.edu.itba.tpf.chatterbot.model.PersistentEntity;

/**
 * Interfaz genérica para DAOs.
 * 
 * @param <T> Clase del modelo sobre la que trabaja el DAO.
 * @param <PK> Tipo del cambo clave del objeto del modelo cuando se persiste.
 */
public interface GenericDAO<T extends PersistentEntity, PK extends Serializable> {

    /**
     * Persiste la entidad.
     * 
     * @param entity Entidad que se quiere persistir.
     */
    public void save(T entity);

    /**
     * Busca todas las instancias de <code>T</code>.
     * 
     * @return <code>Collection<T></code> con todas las instancias de tipo <code>T</code>.
     */
    public Collection<T> findAll();

    /**
     * Dado un un identificador de entidad, busca el objeto de modelo asociado.
     * 
     * @param id identificador de la entidad que se desea buscar.
     * @return Objeto de modelo correspondiente al identificador buscado.
     */
    public T find(PK id);

    /**
     * Elimina la instancia recibida.
     * 
     * @param entity Instancia que se desea eliminar.
     */
    public void delete(T entity);
}
