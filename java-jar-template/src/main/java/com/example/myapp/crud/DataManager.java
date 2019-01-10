package com.example.myapp.crud;
import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import odata.jpa.AbstractDataManager;

@Stateless
public class DataManager extends AbstractDataManager {

	@PersistenceContext
	EntityManager em;

	@Override
	public EntityManager em() {
		return em;
	}

}
