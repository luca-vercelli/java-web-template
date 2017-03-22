/*
* WebTemplate 1.0
* Luca Vercelli 2016
* Released under GPLv3 
*/
package com.example.myapp.crud.actions;

import java.util.List;

import org.hibernate.HibernateException;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.myapp.crud.HibernateUtil;
import com.opensymphony.xwork2.ActionSupport;

public class Crud<T> extends ActionSupport {

	//FIXME..bisognava usare rest...
	
	private static final long serialVersionUID = -7641749620096312407L;

	private Long objectId;
	private String objectClass;
	private T object;
	private List<T> objects;

	/**
	 * Questa pagina lista tutte le entità esistenti
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String list() {
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			setObjects(session.createQuery(objectClass).list());
			tx.commit();
		} catch (HibernateException exc) {
			LOG.error("Error saving object", exc);
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * Questa pagina crea / modifica la singola entità
	 * 
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public String edit() {
		if (objectId != null) {

			Session session = HibernateUtil.getSession();
			Transaction tx = null;
			try {
				tx = session.beginTransaction();
				if (object == null)
					object = (T) session.get(objectClass, objectId);
				tx.commit();
			} catch (HibernateException exc) {
				LOG.error("Error loading object", exc);
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
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			session.saveOrUpdate(object);
			tx.commit();
		} catch (HibernateException exc) {
			LOG.error("Error saving object", exc);
			return ERROR;
		}
		return SUCCESS;
	}

	/**
	 * Delete object from db.
	 */
	@SuppressWarnings("unchecked")
	public String delete() {
		if (object == null && objectId == null) {
			addActionError("crud.errNoObject");
			return ERROR;
		}
		Session session = HibernateUtil.getSession();
		Transaction tx = null;
		try {
			tx = session.beginTransaction();
			if (object == null)
				object = (T) session.get(objectClass, objectId);
			session.delete(object);
			tx.commit();
		} catch (HibernateException exc) {
			LOG.error("Error deleting object", exc);
			return ERROR;
		}
		return SUCCESS;
	}

	public T getObject() {
		return object;
	}

	public void setObject(T object) {
		this.object = object;
	}

	public Long getObjectId() {
		return objectId;
	}

	public void setObjectId(Long objectId) {
		this.objectId = objectId;
	}

	public String getObjectClass() {
		return objectClass;
	}

	public void setObjectClass(String objectClass) {
		this.objectClass = objectClass;
	}

	public List<T> getObjects() {
		return objects;
	}

	public void setObjects(List<T> objects) {
		this.objects = objects;
	}

}
