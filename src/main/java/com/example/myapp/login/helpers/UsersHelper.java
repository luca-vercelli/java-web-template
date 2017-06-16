/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.helpers;

import java.io.File;
import java.util.List;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;

import com.example.myapp.login.entity.User;
import com.example.myapp.login.util.PasswordAuthentication;
import com.example.myapp.main.enums.Boolean;
import com.example.myapp.main.util.ApplicationProperties;

@Stateless
public class UsersHelper {

	@PersistenceContext
	EntityManager em;
	@Inject
	Logger LOG;
	@Inject
	PasswordAuthentication PAinstance;
	@Inject
	ApplicationProperties appProps;

	public UsersHelper() {
	}

	/**
	 * Return a hash for given password.
	 * 
	 * @param pwd
	 * @return
	 */
	public String encryptPassword(char[] cleartextPassword) {
		return PAinstance.hash(cleartextPassword); // FIXME how to get char[] ?
	}

	/**
	 * Set user password
	 * 
	 * @param u
	 * @param unencryptedPassword
	 */
	public void setPassword(User u, char[] cleartextPassword) {
		u.setEncryptedPassword(encryptPassword(cleartextPassword));
	}

	/**
	 * Test user password
	 */
	public boolean testPassword(User u, char[] cleartextPassword) {
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

	public User getUserByNameAndPassword(String name, char[] password) {

		TypedQuery<User> query = em.createQuery("from User where name = :name and active = :true", User.class)
				.setParameter("name", name).setParameter("true", Boolean.Y);

		List<User> users = query.getResultList();

		for (User u : users) {
			if (testPassword(u, password))
				return u;
			// FIXME what if more than 1?
		}

		return null;

	}

	public User getUserByEmailAndPassword(String email, char[] cleartextPassword) {

		TypedQuery<User> query = em.createQuery("from User where email = :email and active = :true", User.class)
				.setParameter("email", email).setParameter("true", true);

		List<User> users = query.getResultList();

		for (User u : users) {
			if (testPassword(u, cleartextPassword))
				return u;
			// FIXME what if more than 1?
		}

		return null;

	}

	/**
	 * Authenticate given username and password through JAAS.
	 * 
	 * @param user,
	 *            or null
	 * @param password
	 *            cleartext password. After authentication will be cleared.
	 * @return
	 */
	public LoginContext authenticate(String user, char[] password) {

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
			// FIXME. LoginException may happen either because authentication
			// failed, or for many other reasons.
			// We should ignore the first, and LOG the latter.
			LOG.error("Exception while loggin-in", e);
			return null;
		} finally {
			PAinstance.clearPassword(password);
		}

		// This should be not null...
		return lc;
	}
}
