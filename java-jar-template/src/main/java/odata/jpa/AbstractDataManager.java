/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package odata.jpa;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.NonUniqueResultException;
import javax.persistence.TypedQuery;
import javax.persistence.metamodel.Attribute;
import javax.persistence.metamodel.EntityType;
import javax.persistence.metamodel.IdentifiableType;
import javax.persistence.metamodel.Metamodel;
import javax.persistence.metamodel.SingularAttribute;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.beanutils.PropertyUtils;

/**
 * Utility methods for managing persisted entities.
 * 
 * Wraps a JPA EntityManager. Subclasses should be @Stateless.
 * 
 * @author Luca Vercelli 2017-2018
 *
 */
public abstract class AbstractDataManager {

	private Map<String, Class<?>> entityCache = new HashMap<String, Class<?>>();

	/**
	 * Subclasses must implement this with the EntityManager corresponding to the
	 * Persistence Unit they want to use.
	 * 
	 * @return
	 */
	public abstract EntityManager em();

	/**
	 * Return a managed entity Class, if any, or null if entity is not managed.
	 * 
	 * Complex Types are ignored.
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

		for (EntityType<?> entityType : em().getMetamodel().getEntities()) {
			if (entityType.getName().equals(entity)) {
				Class<?> clazz = entityType.getJavaType();
				entityCache.put(entity, clazz);
				return clazz;
			}
		}

		return null;
	}

	/**
	 * Retrieve (single) identity column.
	 * 
	 * @see https://stackoverflow.com/questions/16909236
	 * @param entity
	 * @return
	 */
	public <T> SingularAttribute<? super T, ?> getIdAttribute(Class<T> entity) {
		Metamodel m = em().getMetamodel();
		IdentifiableType<T> type = (IdentifiableType<T>) m.managedType(entity);
		return type.getId(type.getIdType().getJavaType());
	}

	/**
	 * Retrieve a column.
	 * 
	 * @param entity
	 * @param propertyName
	 * @return
	 */
	public <T> Attribute<? super T, ?> getAttribute(Class<T> entity, String propertyName) {
		Metamodel m = em().getMetamodel();
		return m.managedType(entity).getAttribute(propertyName);
	}

	/**
	 * Select a single attribute.
	 * 
	 * @param entity
	 * @param propertyName
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public <U> U getAttributeValue(Attribute<?, U> attribute, Object obj) {
		U value;
		if (attribute.getJavaMember() instanceof Field) {
			Field field = (Field) attribute.getJavaMember();
			try {
				value = (U) field.get(obj);
			} catch (IllegalArgumentException | IllegalAccessException e) {
				// TODO use LOG instead
				e.printStackTrace();
				throw new IllegalStateException();
			}
		} else if (attribute.getJavaMember() instanceof Method) {
			Method getter = (Method) attribute.getJavaMember();
			try {
				value = (U) getter.invoke(obj);
			} catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException e) {
				// TODO use LOG instead
				e.printStackTrace();
				throw new IllegalStateException();
			}
		} else {
			System.out.println(
					"Attribute " + attribute + " has unknown java getter: " + attribute.getJavaMember().getClass());
			throw new IllegalStateException();
		}
		return value;
	}

	/**
	 * Select a single attribute.
	 * 
	 * @param entity
	 * @param attributeName
	 * @param obj
	 * @return
	 */
	public Object getAttributeValue(Class<?> entity, String attributeName, Object obj) {
		return getAttributeValue(getAttribute(entity, attributeName), obj);
	}

	/**
	 * Return a new object with given attributes set. Class must have a no-argument
	 * constructor.
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
	 * Delete an object from database. Wrap EntityManager.remove().
	 */
	public void remove(Object persistedObject) {
		em().remove(persistedObject);
	}

	/**
	 * Delete an object from database, by primary key.
	 */
	public void removeById(Class<?> entity, Serializable id) {
		Object obj = em().find(entity, id);
		em().remove(obj);
	}

	/**
	 * Load all objects of given entity.
	 */
	public <T> List<T> findAll(Class<T> entity) {
		return em().createQuery("from " + entity.getName(), entity).getResultList();
	}

	/**
	 * Count number of rows in given table.
	 */
	public Long countEntities(Class<?> entity) {
		return countEntities(entity, null);
	}

	/**
	 * Count number of rows in given table.
	 * 
	 * @param where
	 *            JPQL WHERE clause, without "WHERE".
	 */
	public Long countEntities(Class<?> entity, String where) {
		String whereCondition = "";
		if (where != null && !where.trim().isEmpty()) {
			whereCondition = " WHERE " + where;
		}
		//FIXME jpa does not accept count(*) so we need a fixed attribute such as x.id
		return em().createQuery("select count(x.id) from " + entity.getName() + " x " + whereCondition, Long.class)
				.getSingleResult();
	}

	/**
	 * Load at most maxResults objects of given entity, starting from firstResult,
	 * and with given ordering. Useful for pagination.
	 * 
	 * Notice that "maxResult" is in fact the size of a page, while "firstResult" =
	 * (pageNumber-1)*pageSize
	 * 
	 * @param bindVariable
	 *            The variable name to be used as object variable (compulsory). Usually "x".
	 *            This must be the same variable name used inside "where" and "orderby" conditions.
	 * @param maxResult
	 *            max number of elements to retrieve (optional)
	 * @param firstResult
	 *            positional order of first element to retrieve (0-based)
	 *            (optional).
	 * @param where
	 *            WHERE JPQL clause, without "WHERE" (optional).
	 * @param orderby
	 *            ORDER BY JPQL clause, without "ORDER BY" (optional).
	 */
	public <T> List<T> find(Class<T> entity, String bindVariable, Integer maxResults, Integer firstResult, String where, String orderby) {

		String whereCondition = "";
		String orderbyCondition = "";

		if (where != null && !where.trim().isEmpty()) {
			whereCondition = " WHERE " + where;
		}

		if (orderby != null && !orderby.trim().isEmpty()) {
			orderbyCondition = " ORDER BY " + orderby;
		}

		TypedQuery<T> query = em()
				.createQuery("SELECT " + bindVariable + " FROM " + entity.getName() + " " + bindVariable + " "
						+ whereCondition + orderbyCondition, entity);
		if (firstResult != null)
			query.setFirstResult(firstResult);
		if (maxResults != null)
			query.setMaxResults(maxResults);

		return query.getResultList();
	}

	/**
	 * Find by id. Wrap EntityManager.find().
	 */
	public <T> T find(Class<T> entity, Serializable primaryKey) {
		return em().find(entity, primaryKey);
	}

	/**
	 * Create a Query such as property=value (null supported).
	 */
	private <T> TypedQuery<T> createFindByPropertyQuery(Class<T> entity, String propertyName, Object value) {
		if (value != null) {
			return em()
					.createQuery("select x from " + entity.getName() + " x where x." + propertyName + " = :param", entity)
					.setParameter("param", value);
		} else {
			return em().createQuery("select x from " + entity.getName() + " x where x." + propertyName + " is null",
					entity);
		}
	}

	/**
	 * Load all objects of given entity, such that property=value (null supported).
	 */
	public <T> List<T> findByProperty(Class<T> entity, String propertyName, Object value) {
		return createFindByPropertyQuery(entity, propertyName, value).getResultList();
	}

	/**
	 * Load that object of given entity, such that property=value (null supported),
	 * or null if none.
	 * 
	 * @throws NonUniqueResultException
	 * @throws NoResultException
	 */
	public <T> T findByPropertySingleResult(Class<T> entity, String propertyName, Object value) {
		return createFindByPropertyQuery(entity, propertyName, value).getSingleResult();
	}

	/**
	 * Find (i.e. retrieve) an object from database, by primary key, then detache it
	 * and remove its primary key so that we have a "new" object.
	 * 
	 * The new object is not saved to database.
	 * 
	 * @throws IllegalArgumentException
	 *             if object does not exists
	 */
	public <T> T duplicate(Class<T> entity, Serializable id) {

		T obj = em().find(entity, id);

		if (obj == null)
			throw new IllegalArgumentException("No object with given id");

		em().detach(obj);

		SingularAttribute<? super T, ?> idAttr = getIdAttribute(entity);
		if (idAttr == null) {
			// TODO LOG instead of System.out.println
			System.out.println("getIdAttribute() returned null for entity:" + entity);
			throw new IllegalArgumentException("getIdAttribute() returned null for entity:" + entity);
		}

		try {
			PropertyUtils.setSimpleProperty(obj, idAttr.getName(), null);
		} catch (IllegalAccessException | InvocationTargetException | NoSuchMethodException exc) {
			// TODO LOG instead of System.out.println
			System.out.println("Misconfigured class: " + entity);
			exc.printStackTrace();
			throw new IllegalArgumentException("Misconfigured class: " + entity);
		}

		return obj;
	}

	/**
	 * Save an object to database, return the same object possibly modified. Wrap
	 * EntityManager.merge().
	 */
	public <T> T merge(T obj) {
		return em().merge(obj);
	}

	/**
	 * Save an object to database. You should usually use merge() instead. Wrap
	 * EntityManager.persist().
	 */
	public void persist(Object obj) {
		em().persist(obj);
	}

}
