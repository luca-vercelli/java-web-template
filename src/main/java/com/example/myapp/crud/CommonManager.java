package com.example.myapp.crud;

import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.metamodel.EntityType;

import org.apache.commons.beanutils.BeanUtils;

/**
 * Not very different from EntityManager. Just a bit more.
 * 
 * @author Luca Vercelli 2017
 *
 */
@Stateless
public class CommonManager {

	@PersistenceContext(unitName = "persistenceUnit")
	private EntityManager em;

	private Map<String, Class<?>> entityCache = new HashMap<String, Class<?>>();

	/**
	 * Return a managed entity Class, if any, or null if entity is not managed.
	 */
	public Class<?> getEntityClass(String entity) {

		if (entity == null)
			return null;
		entity = entity.trim();
		if (entity.equals(""))
			return null;

		if (entityCache.containsKey(entity)) {
			return entityCache.get(entity);
		}

		for (EntityType<?> entityType : em.getMetamodel().getEntities()) {
			if (entityType.getName().equals(entity)) {
				Class<?> clazz = entityType.getJavaType();
				entityCache.put(entity, clazz);
				return clazz;
			}
		}

		return null;
	}

	/**
	 * Return a new object with given attributes set. Class must have a
	 * no-argument constructor.
	 * 
	 * @param entity
	 * @param attributes
	 * @return
	 */
	public <T> T bean2object(Class<T> entity, Map<String, ? extends Object> attributes) {
		T obj;
		try {
			obj = entity.newInstance();
			BeanUtils.populate(obj, attributes);

		} catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new RuntimeException(e);
		}
		return obj;
	}

	/**
	 * Return object attributes.
	 * 
	 * @param obj
	 * @return
	 */
	public Map<String, String> object2bean(Object obj) {
		try {
			return BeanUtils.describe(obj);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Find (i.e. retrieve) an object from database, by primary key.
	 */
	public <T> T findById(Class<T> entity, Serializable id) {
		return em.find(entity, id);
	}

	/**
	 * Save an object to database (using 'merge' instead of 'persist').
	 */
	public <T> T save(T tosave) {
		return em.merge(tosave);
	}

	/**
	 * Delete an object from database.
	 */
	public void remove(Object toremove) {
		em.remove(toremove);
	}

	/**
	 * Delete an object from database, by primary key.
	 */
	public void remove(Class<?> entity, Serializable id) {
		Object obj = findById(entity, id);
		em.remove(obj);
	}

	/**
	 * Load all objects of given entity.
	 */
	public <T> List<T> findAll(Class<T> entity) {
		return em.createQuery("from " + entity.getName(), entity).getResultList();
	}

	/**
	 * Count number of rows in given table.
	 */
	public Long countEntities(Class<?> entity) {
		return em.createQuery("count(*) from " + entity.getName(), Long.class).getSingleResult();
	}

	/**
	 * Load at most maxResults objects of given entity, starting from
	 * firstResult, and with given ordering. Useful for pagination.
	 * 
	 * Notice that "maxResult" is in fact the size of a page, while
	 * "firstResult" = (pageNumber-1)*pageSize + 1
	 * 
	 * @param maxResult
	 *            max number of elements to retrieve
	 * @param firstResult
	 *            positional order of first element to retrieve (1-based).
	 * @param sort
	 *            attribute for ordering (optional)
	 * @param order
	 *            ASC or DESC. Default ASC.
	 */
	public <T> List<T> find(Class<T> entity, int maxResults, int firstResult, String sort, String order) {

		String sortCondition = "";

		if (sort != null) {
			if (order == null)
				order = "asc";
			else {
				order = order.toLowerCase();
				if (!order.equals("asc") && !order.equals("desc"))
					throw new IllegalArgumentException("order must be either 'asc' or 'desc'");
			}
			sortCondition = " order by " + sort + " " + order;
		}

		return em.createQuery("from " + entity.getName() + sortCondition, entity).setFirstResult(firstResult)
				.setMaxResults(maxResults).getResultList();
	}

	/**
	 * Load all objects of given entity, such that property=value.
	 */
	public <T> List<T> findByProperty(Class<T> entity, String property, Object value) {
		return em.createQuery("from " + entity.getName() + " where " + property + " = :param", entity)
				.setParameter("param", value).getResultList();
	}

}
