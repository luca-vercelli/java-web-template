/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.helpers;

import java.io.File;
import java.util.List;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import org.apache.log4j.Logger;

import com.example.myapp.login.db.User;
import com.example.myapp.login.util.PasswordAuthentication;
import com.example.myapp.main.util.ApplicationProperties;

//TODO convert in Stateless EJB
public class UsersHelper {

	private static Logger LOG = Logger.getLogger(UsersHelper.class);

	@Inject
	private static UsersHelper instance; // Singleton

	@PersistenceContext
	private EntityManager em; 

	@Inject
	private static PasswordAuthentication PAinstance; // Singleton

	@Inject
	ApplicationProperties appProps;
	
	public static UsersHelper getInstance() {
		return instance;
	}

	public UsersHelper() {
	}

	/**
	 * Return a hash for given password.
	 * 
	 * @param pwd
	 * @return
	 */
	public String encryptPassword(String cleartextPassword) {
		return PAinstance.hash(cleartextPassword); // FIXME how to get char[]
													// from Struts2
		// action?
	}

	/**
	 * Set user password
	 * 
	 * @param u
	 * @param unencryptedPassword
	 */
	public void setPassword(User u, String cleartextPassword) {
		u.setEncryptedPassword(encryptPassword(cleartextPassword));
	}

	/**
	 * Test user password
	 */
	public boolean testPassword(User u, String cleartextPassword) {
		return PAinstance.authenticate(cleartextPassword, u.getEncryptedPassword());
	}

	/**
	 * Fill array with zeroes
	 * 
	 * @param array
	 */
	public void clearArray(char[] array) {
		for (int i = 0; i < array.length; ++i)
			array[i] = 0;
	}

	public User getUserByNameAndPassword(String name, String cleartextPassword) {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			TypedQuery<User> query;

			query = em.createQuery("from User where name = :name and active = :true", User.class)
					.setParameter("name", name).setParameter("true", true);

			List<User> users = query.getResultList();

			tx.commit();

			for (User u : users) {
				if (testPassword(u, cleartextPassword))
					return u;
				// FIXME what if more than 1?
			}

			return null;

		} catch (Exception exc) {
			if (tx != null && tx.isActive())
				tx.rollback();
			throw exc;
		}
	}

	public User getUserByEmailAndPassword(String email, String cleartextPassword) {

		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			TypedQuery<User> query;

			query = em.createQuery("from User where email = :email and active = :true", User.class)
					.setParameter("email", email).setParameter("true", true);

			List<User> users = query.getResultList();

			tx.commit();

			for (User u : users) {
				if (testPassword(u, cleartextPassword))
					return u;
				// FIXME what if more than 1?
			}

			return null;

		} catch (Exception exc) {
			if (tx != null && tx.isActive())
				tx.rollback();
			throw exc;
		}
	}

	/**
	 * Authenticate given username and password through JAAS.
	 * 
	 * @param user,
	 *            or null
	 * @param password
	 * @return
	 */
	public LoginContext authenticate(String user, String password) {

		// FIXME could be put outside app?
		System.setProperty("java.security.auth.login.config",
				appProps.getAppRoot().getAbsolutePath() + File.separator + "jaas.conf");

		PassiveCallbackHandler cbh = new PassiveCallbackHandler(user, password);

		LoginContext lc;
		try {
			lc = new LoginContext("MainApp", cbh); // referenced in jaas.conf
		} catch (LoginException e) {
			LOG.error("Exception during new LoginContext()", e);
			return null;
		}

		// see Krb5LoginModule, LdapLoginModule, NTLoginModule, JndiLoginModule
		// ...sun...

		try {
			// call callback to retrieve credentials, and checks that
			// Authentication fails
			lc.login();
		} catch (LoginException e) {

			return null;
		}

		// This should be not null...
		return lc;
	}
}
