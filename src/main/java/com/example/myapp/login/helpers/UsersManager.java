/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.helpers;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;

import com.example.myapp.crud.GenericManager;
import com.example.myapp.login.util.PasswordAuthentication;
import com.example.myapp.main.entity.Menu;
import com.example.myapp.main.entity.Page;
import com.example.myapp.main.entity.Role;
import com.example.myapp.main.entity.User;
import com.example.myapp.main.enums.BooleanYN;
import com.example.myapp.main.util.ApplicationProperties;
import com.sun.messaging.jmq.util.MD5;

@Stateless
public class UsersManager {

	@PersistenceContext
	EntityManager em;
	@Inject
	Logger LOG;
	@Inject
	PasswordAuthentication PAinstance;
	@Inject
	ApplicationProperties appProps;
	@Inject
	GenericManager genericManager;

	public UsersManager() {
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

		// FIXME namedquery?
		TypedQuery<User> query = em.createQuery("from User where name = :name and active = :true", User.class)
				.setParameter("name", name).setParameter("true", BooleanYN.Y);

		List<User> users = query.getResultList();

		for (User u : users) {
			if (testPassword(u, password))
				return u;
			// FIXME what if more than 1?
		}

		return null;

	}

	public User getUserByEmailAndPassword(String email, char[] cleartextPassword) {

		// FIXME namedquery?
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

	public User getUserByEmail(String email) {

		List<User> users = genericManager.findByProperty(User.class, "email", email);

		for (User u : users) {
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

		// consider modules: Krb5LoginModule, LdapLoginModule, NTLoginModule,
		// JndiLoginModule
		// ...sun...

		PassiveCallbackHandler cbh = new PassiveCallbackHandler(user, password);

		LoginContext lc;
		try {
			lc = new LoginContext("MainApp", cbh); // referenced in jaas.conf

		} catch (LoginException e) {
			LOG.error("Exception during new LoginContext()", e);
			return null;
		}

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

	/**
	 * Return a <b>detached</b> instance of given Menu whose submenus and
	 * subpages are all and only the ones that the user is allowed to. Return
	 * null if user is not allowed at all. Please do not save this instance!!!
	 * 
	 * @param user
	 * @return
	 */
	public Menu filterMenuForUser(Menu menu, User user) {

		// FIXME this is slow
		// should use some kind of View

		if (menu == null)
			return null;

		Menu menu2 = new Menu();
		menu2.setOrdering(menu.getOrdering());
		menu2.setDescription(menu.getDescription());
		menu2.setIcon(menu.getIcon());
		// parentMenu null here

		for (Page p : menu.getPages()) {
			if (isAuthorized(p, user)) {
				menu2.getPages().add(p);
			}
		}

		for (Menu m : menu.getSubmenus()) {
			if (isAuthorized(m, user)) {
				Menu filteredMenu2 = filterMenuForUser(m, user);
				if (filteredMenu2 != null) {
					menu2.getSubmenus().add(filteredMenu2);
				}
			}
		}

		if (menu2.getPages().isEmpty() && menu2.getSubmenus().isEmpty())
			return null;
		else
			return menu2;
	}

	/**
	 * False if there is recorded an authorization for the user to the given
	 * menu.
	 */
	private boolean isAuthorized(Menu m, User user) {
		if (m.getAuthorizedRoles().isEmpty())
			return true; // no auth needed
		for (Role role : m.getAuthorizedRoles()) {
			if (user.getRoles().contains(role))
				return true;
		}
		return false;
	}

	/**
	 * False if there is recorded an authorization for the user to the given
	 * page.
	 */
	private boolean isAuthorized(Page p, User user) {
		if (p.getAuthorizedRoles().isEmpty())
			return true; // no auth needed
		for (Role role : p.getAuthorizedRoles()) {
			if (user.getRoles().contains(role))
				return true;
		}
		return false;
	}

	/**
	 * Return a tree of menus, filtered according to filterMenuForUser(Menu
	 * menu, User user). Please do not save such menus!!!
	 * 
	 * @param user
	 * @return
	 */
	public List<Menu> getMenusForUser(User user) {

		List<Menu> topMenus = genericManager.findByProperty(Menu.class, "parentMenu", null);
		List<Menu> filteredMenus = new ArrayList<Menu>();

		for (Menu m : topMenus) {
			Menu m2 = filterMenuForUser(m, user);
			if (m2 != null)
				filteredMenus.add(m2);
		}

		return filteredMenus;
	}

	/**
	 * Return the (ordered) list of pages the user shall see in his menu. Not
	 * all pages are required to lie inside a Menu.
	 * 
	 * @param user
	 * @return
	 */
	public List<Page> getPagesForUser(User user) {

		TypedQuery<Page> query = em.createQuery("from Page where authorizedRoles in :roles", Page.class)
				.setParameter("roles", user.getRoles());

		List<Page> pages = query.getResultList();

		return pages;
	}

	/**
	 * Create and return a password recovery code. The code is stored encrypted.
	 * 
	 * @param user
	 * @return
	 */
	public String createPasswordRecoveryCode(User user) {
		String code = MD5.getHashString("" + Math.random() + new Date());
		user.setPasswordRecoveryCode(MD5.getHashString(code));
		return code;
	}

	public User getUserByPasswordRecoveryCode(String pwdRecCode) {
		pwdRecCode = MD5.getHashString(pwdRecCode);
		List<User> users = genericManager.findByProperty(User.class, "passwordRecoveryCode", pwdRecCode);

		if (users.isEmpty())
			return null;
		else if (users.size() > 1)
			throw new IllegalStateException("2 users with same authentication code!");
		else
			return users.get(0);
	}

	/**
	 * Create, set and return a new 10-characters-long random password,
	 * containing only letters and digits.
	 * 
	 * @param user
	 */
	public String newPassword(User user) {
		String pwd = randomString().substring(10);
		setPassword(user, pwd.toCharArray());
		return pwd;
	}

	/**
	 * Return a random 32-characters-long string, containing only letters and
	 * digits.
	 * 
	 * @param len
	 * @return
	 */
	private String randomString() {
		return MD5.getHashString("" + Math.random() + new Date());
	}
}
