package ar.edu.itba.tpf.chatterbot.dao;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.util.Collection;
import java.util.List;

import org.hibernate.Criteria;
import org.hibernate.SessionFactory;
import org.hibernate.criterion.Criterion;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.orm.hibernate3.support.HibernateDaoSupport;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import ar.edu.itba.tpf.chatterbot.model.PersistentEntity;

/**
 * Implementación del GenericDAO para Hibernate.
 * 
 * @param <T> Clase del modelo sobre la que trabaja el DAO.
 * @param <PK> Tipo del cambo clave del objeto del modelo cuando se persiste.
 */
public abstract class GenericDAOImpl<T extends PersistentEntity, PK extends Serializable> extends HibernateDaoSupport
        implements GenericDAO<T, PK> {
    private Class<T> entityClass;

    @SuppressWarnings("unchecked")
    @Autowired
    public GenericDAOImpl(SessionFactory sessionFactory) {
        super.setSessionFactory(sessionFactory);

        /* Obtener el Class<T> de la implementacion de la clase del modelo */
        this.entityClass = (Class<T>) ((ParameterizedType) getClass().getGenericSuperclass()).getActualTypeArguments()[0];
    }

    @Transactional(readOnly = false, propagation=Propagation.SUPPORTS)
    public void delete(T object) {
        getHibernateTemplate().delete(object);
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS)
    public T find(PK id) {
        return (T) getHibernateTemplate().get(entityClass, id);
    }

    @Transactional(propagation=Propagation.SUPPORTS)
    public Collection<T> findAll() {
        return findByCriteria();
    }

    @Transactional(readOnly = false, propagation = Propagation.SUPPORTS)
    public void save(T object) {
        if (object.getId() == null) {
            getHibernateTemplate().save(object);
        } else {
            getHibernateTemplate().merge(object);
        }
    }

    @SuppressWarnings("unchecked")
    @Transactional(propagation=Propagation.SUPPORTS)
    protected List<T> findByCriteria(Criterion... criterion) {
        Criteria detachedCriteria = getSession().createCriteria(entityClass);
        for (Criterion c : criterion) {
            detachedCriteria.add(c);
        }
        return detachedCriteria.list();
    }

}
