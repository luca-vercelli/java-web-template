package com.example.myapp.crud;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import odata.jpa.AbstractDataManager;

public class GenericManager extends AbstractDataManager {

	@PersistenceContext
	EntityManager em;

	@Override
	public EntityManager em() {
		return em;
	}

}
