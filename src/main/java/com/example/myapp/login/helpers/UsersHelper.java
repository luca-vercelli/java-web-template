package com.example.myapp.login.helpers;

import java.io.File;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;
import javax.servlet.Servlet;
import javax.servlet.ServletContext;

import org.apache.log4j.Logger;

import com.example.myapp.crud.EntityManagerUtil;
import com.example.myapp.factory.Factory;
import com.example.myapp.login.db.User;
import com.example.myapp.login.util.PasswordAuthentication;
import com.example.myapp.main.util.ApplicationProperties;

public class UsersHelper {

	private static UsersHelper instance;
	private static Logger LOG = Logger.getLogger(UsersHelper.class);

	private PasswordAuthentication PAinstance = Factory.getInstance().createObject(PasswordAuthentication.class);

	public static UsersHelper getInstance() {
		if (instance == null) {
			instance = Factory.getInstance().createObject(UsersHelper.class);
		}
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
		boolean ret = u.getEncryptedPassword().equals(encryptPassword(cleartextPassword));
		return ret;
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

	public User getUserByNameAndPassword(String name, String password) {

		String encryptedPassword = encryptPassword(password);
		password = null;

		EntityManager em = EntityManagerUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			TypedQuery<User> query;

			query = em
					.createQuery(
							"from User where name = :name and encryptedPassword = :encryptedPassword and active = :true",
							User.class)
					.setParameter("name", name).setParameter("true", true)
					.setParameter("encryptedPassword", encryptedPassword);

			List<User> users = query.getResultList();

			tx.commit();

			if (users.isEmpty()) {
				return null;
			} else if (users.size() > 1) {
				LOG.error("Found more user with same password and name: " + name);
				return null;
			}

			return users.get(0);

		} catch (Exception exc) {
			if (tx != null && tx.isActive())
				tx.rollback();
			throw exc;
		}
	}

	public User getUserByEmailAndPassword(String email, String password) {

		String encryptedPassword = encryptPassword(password);
		password = null;

		EntityManager em = EntityManagerUtil.getEntityManager();
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			TypedQuery<User> query;

			query = em
					.createQuery(
							"from User where email = :email and encryptedPassword = :encryptedPassword and active = :true",
							User.class)
					.setParameter("email", email).setParameter("true", true)
					.setParameter("encryptedPassword", encryptedPassword);

			List<User> users = query.getResultList();

			tx.commit();

			if (users.isEmpty()) {
				return null;
			} else if (users.size() > 1) {
				LOG.error("Found more user with same password and email: " + email);
				return null;
			}

			return users.get(0);

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
				ApplicationProperties.getInstance().getRoot().getAbsolutePath() + File.separator + "jaas.conf");

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
