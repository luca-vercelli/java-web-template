package com.example.myapp.factory;

import org.hibernate.Transaction;
import org.hibernate.criterion.DetachedCriteria;
import org.hibernate.criterion.Projections;
import org.hibernate.criterion.Restrictions;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;

import org.hibernate.Session;

import com.example.myapp.crud.HibernateUtil;
import com.example.myapp.factory.db.ClassReplacement;

/**
 * You can use Factory.getInstance().creteObject(), instead of "new", throughout
 * your application. If you need to change the implementation of some class or
 * interface, just add a record on ClassReplacement table.
 *
 */
public class Factory {

	private static Factory instance = new Factory();

	private Factory() {
	}

	public static Factory getInstance() {
		return instance;
	}

	/**
	 * Recursively search for replaced classes on DB, then instantiate object
	 * with its nullary constructor.
	 */
	public <T> T createObject(Class<T> clazz) {
		try {

			Class<? extends T> newClazz = replaceClass(clazz);

			return newClazz.newInstance();
		} catch (InstantiationException e) {
			throw new RuntimeException(e);
		} catch (IllegalAccessException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Recursively search for replaced classes on DB, then instantiate object
	 * with appropriate constructor.
	 */
	public <T> T createObject(Class<T> clazz, Object... args) {
		// FIXME current implementation is quite inefficient

		Class<? extends T> newClazz = replaceClass(clazz);

		T object = null;
		// FIXME why this warning? it looks ok to me
		Constructor<? extends T>[] constructors = (Constructor<? extends T>[]) newClazz.getConstructors();
		for (Constructor<? extends T> constructor : constructors) {
			try {
				object = constructor.newInstance(args);
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
			if (object != null)
				return object;
		}
		throw new RuntimeException("Cannot find such constructor: " + clazz.getName() + "," + Arrays.asList(args));
	}

	/**
	 * Recursively search for replaced classes on DB.
	 */
	public <T> Class<? extends T> replaceClass(Class<T> clazz) {
		String clazzName = clazz.getName();
		String newClazzName = replaceClass(clazzName);
		try {
			Class<?> newClazz = Class.forName(newClazzName);
			if (!clazz.isAssignableFrom(newClazz))
				throw new IllegalStateException("Class " + clazzName + " cannot be replaced by " + newClazzName);
			// FIXME this warning is ok, IMHO, how to avoid it?
			return (Class<? extends T>) newClazz;
		} catch (ClassNotFoundException e) {
			throw new RuntimeException(e);
		}
	}

	/**
	 * Recursively search for replaced classes on DB.
	 */
	public String replaceClass(String clazzName) {
		Transaction tx = null;
		try {

			Session session = HibernateUtil.getSession();
			tx = session.beginTransaction();
			boolean searching = true;

			while (searching) {
				DetachedCriteria criteria = DetachedCriteria.forClass(ClassReplacement.class);
				criteria.add(Restrictions.eq("origClassName", clazzName));
				criteria.setProjection(Projections.property("replaceClassName"));
				String newClazzName = (String) criteria.getExecutableCriteria(session).uniqueResult();
				if (newClazzName != null) {
					clazzName = newClazzName;
				} else {
					searching = false;
				}
			}

			tx.commit();

			return clazzName;

		} finally {
			if (tx != null && tx.isActive())
				tx.rollback();
		}
	}

}
