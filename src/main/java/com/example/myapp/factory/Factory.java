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
	public Object createObject(Class clazz) {
		try {

			clazz = replaceClass(clazz);

			return clazz.newInstance();
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
	public Object createObject(Class clazz, Object... args) {
		// FIXME current implementation is quite inefficient
		
		clazz = replaceClass(clazz);
		
		Object ret = null;
		Constructor[] constructors = clazz.getConstructors();
		for (Constructor constructor : constructors) {
			try {
				ret = constructor.newInstance(args);
			} catch (InstantiationException e) {
			} catch (IllegalAccessException e) {
			} catch (IllegalArgumentException e) {
			} catch (InvocationTargetException e) {
			}
			if (ret != null)
				return ret;
		}
		throw new RuntimeException("Cannot find such constructor: " + clazz.getName() + "," + Arrays.asList(args));
	}

	/**
	 * Recursively search for replaced classes on DB.
	 */
	public Class replaceClass(Class clazz) {
		String clazzName = clazz.getName();
		clazzName = replaceClass(clazzName);
		try {
			return Class.forName(clazzName);
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
