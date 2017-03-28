package com.example.myapp.login.helpers;

import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.apache.log4j.Logger;

import com.example.myapp.crud.EntityManagerFactory;
import com.example.myapp.factory.Factory;
import com.example.myapp.login.db.User;
import com.example.myapp.login.util.PasswordAuthentication;

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
	public String getEncryptedPassword(String pwd) {
		return PAinstance.hash(pwd); // FIXME how to get char[] from Struts2
										// action?
	}

	/**
	 * Set user password
	 * 
	 * @param u
	 * @param unencryptedPassword
	 */
	public void setPassword(User u, String unencryptedPassword) {
		u.setEncryptedPassword(getEncryptedPassword(unencryptedPassword));
	}

	/**
	 * Test user password
	 */
	public boolean testPassword(User u, String unencryptedPassword) {
		boolean ret = u.getEncryptedPassword().equals(getEncryptedPassword(unencryptedPassword));
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

	public User getUserByUsernameAndPassword(String username, String password) {

		EntityManager em = EntityManagerFactory.createEntityManager(); // FIXME
																		// ...
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			TypedQuery<User> query;

			query = em
					.createQuery("from User where userId = :userId and encryptedPassword = :pwd and active = :true",
							User.class)
					.setParameter("userId", username).setParameter("true", true)
					.setParameter("encryptedPassword", getEncryptedPassword(password));

			List<User> users = query.getResultList();

			tx.commit();

			if (users.isEmpty()) {
				return null;
			} else if (users.size() > 1) {
				LOG.error("Found more user with same password and username: " + username);
			}

			return users.get(0);

		} catch (Exception exc) {
			if (tx != null && tx.isActive())
				tx.rollback();
			throw exc;
		}
	}

	public User getUserByEmailAndPassword(String email, String password) {

		EntityManager em = EntityManagerFactory.createEntityManager(); // FIXME
																		// ...
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			TypedQuery<User> query;

			query = em
					.createQuery("from User where email = :email and encryptedPassword = :pwd and active = :true",
							User.class)
					.setParameter("email", email).setParameter("true", true)
					.setParameter("encryptedPassword", getEncryptedPassword(password));

			List<User> users = query.getResultList();

			tx.commit();

			if (users.isEmpty()) {
				return null;
			} else if (users.size() > 1) {
				LOG.error("Found more user with same password and email: " + email);
			}

			return users.get(0);

		} catch (Exception exc) {
			if (tx != null && tx.isActive())
				tx.rollback();
			throw exc;
		}
	}
}
