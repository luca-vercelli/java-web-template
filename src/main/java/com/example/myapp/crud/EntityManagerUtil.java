/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.crud;

import javax.persistence.EntityManager;
import javax.persistence.Persistence;

public class EntityManagerUtil {

	// Create an EntityManagerFactory when you start the application.
	// This should be used only in standalone (non-web) context.
	public static final javax.persistence.EntityManagerFactory ENTITY_MANAGER_FACTORY = Persistence
			.createEntityManagerFactory("MyPersistenceUnit");

	/**
	 * Get in some way an EntityManager. Current implementation simply call
	 * ENTITY_MANAGER_FACTORY.createEntityManager(). Not so good for JTA.
	 */
	public static EntityManager getEntityManager() {
		return ENTITY_MANAGER_FACTORY.createEntityManager();
	}

}
