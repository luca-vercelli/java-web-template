/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.crud.actions;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.myapp.crud.EntityManagerFactory;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.ModelDriven;

public class Crud<T> extends ActionSupport implements ModelDriven<T> {

	// FIXME..bisognava usare rest...

	private static final long serialVersionUID = -7641749620096312407L;

	private Long objectId;
	private Class<T> objectClass;
	private T object; // FIXME need some kind of interceptor
	private List<T> objects;

	/**
	 * Questa pagina lista tutte le entità esistenti
	 * 
	 * @return
	 */
	public String list() {
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = EntityManagerFactory.createEntityManager();
			tx = em.getTransaction();
			setModels(em.createQuery(objectClass.getName(), objectClass).getResultList());
			tx.commit();

		} catch (HibernateException exc) {
			LOG.error("Error getting object", exc);
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * Questa pagina crea / modifica la singola entità
	 * 
	 * @return
	 */
	public String edit() {
		if (objectId != null) {

			EntityManager em = null;
			EntityTransaction tx = null;
			try {
				em = EntityManagerFactory.createEntityManager();
				tx = em.getTransaction();
				if (object == null)
					object = (T) em.find(objectClass, objectId);
				tx.commit();
			} catch (HibernateException exc) {
				LOG.error("Error loading object", exc);
				if (tx != null && tx.isActive())
					tx.rollback();
				return ERROR;
			}
		}
		return SUCCESS;
	}

	/**
	 * Insert or update object on db.
	 * 
	 * @return
	 */
	public String save() {
		if (object == null) {
			addActionError("crud.errNoObject");
			return ERROR;
		}
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = EntityManagerFactory.createEntityManager();
			tx = em.getTransaction();
			em.persist(object);
			tx.commit();
		} catch (HibernateException exc) {
			LOG.error("Error saving object", exc);
			if (tx != null && tx.isActive())
				tx.rollback();
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * Delete object from db.
	 */
	public String delete() {
		if (object == null && objectId == null) {
			addActionError("crud.errNoObject");
			return ERROR;
		}
		EntityManager em = null;
		EntityTransaction tx = null;
		try {
			em = EntityManagerFactory.createEntityManager();
			tx = em.getTransaction();
			if (object == null)
				object = (T) em.find(objectClass, objectId);
			em.remove(object);
			tx.commit();
		} catch (HibernateException exc) {
			LOG.error("Error deleting object", exc);
			if (tx != null && tx.isActive())
				tx.rollback();
			return ERROR;
		}
		return SUCCESS;
	}

	public Long getModelId() {
		return objectId;
	}

	public void setModelId(Long objectId) {
		this.objectId = objectId;
	}

	public String getModelClass() {
		return objectClass.getName();
	}

	public void setModelClass(String objectClass) throws ClassNotFoundException {
		this.objectClass = (Class<T>) Class.forName(objectClass);
	}

	public List<T> getModels() {
		return objects;
	}

	public void setModels(List<T> objects) {
		this.objects = objects;
	}

	@Override
	public T getModel() {
		return object;
	}

	public void setModel(T object) {
		this.object = object;
	}

}
