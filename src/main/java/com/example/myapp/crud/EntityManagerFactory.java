package com.example.myapp.crud;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class EntityManagerFactory {

	// Create an EntityManagerFactory when you start the application.
	// This should be used only in standalone (non-web) context.
	public static final javax.persistence.EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
			.createEntityManagerFactory("MyPersistenceUnit");

	public static EntityManager createEntityManager() {
		return ENTITY_MANAGER_FACTORY.createEntityManager();
	}

}
