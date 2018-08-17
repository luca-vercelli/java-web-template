/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.login.helpers;

import java.nio.ByteBuffer;
import java.nio.CharBuffer;
import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.xml.bind.DatatypeConverter;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.slf4j.Logger;

import com.example.myapp.main.entity.Menu;
import com.example.myapp.main.entity.Page;
import com.example.myapp.main.entity.Role;
import com.example.myapp.main.entity.User;
import com.example.myapp.main.enums.BooleanYN;
import com.example.myapp.main.util.ApplicationProperties;

import odata.jpa.AbstractDataManager;

//TODO may split encrypt-related stuff
@Stateless
public class UsersManager {

	@PersistenceContext
	EntityManager em;
	@Inject
	Logger LOG;
	@Inject
	ApplicationProperties appProps;
	@Inject
	AbstractDataManager genericManager;

	public final String ALGORITHM = "SHA-256";

	public UsersManager() {
	}

	public User getUserByNameAndPassword(String name, String cleartextPassword) {
		return getUserByNameAndPassword(name, cleartextPassword.toCharArray());
	}

	/**
	 * Authenticates user. This is useless using EE security. This method should get
	 * the same result of jdbcRealm authentication.
	 * 
	 * @param email
	 * @param cleartextPassword
	 * @return
	 */
	// FIXME jdbcRealm does not check if active = true
	public User getUserByNameAndPassword(String name, char[] cleartextPassword) {

		// FIXME namedquery?
		TypedQuery<User> query = em.createQuery("from User where name = :name and active = :true", User.class)
				.setParameter("name", name).setParameter("true", BooleanYN.Y);

		List<User> users = query.getResultList();

		for (User u : users) {
			if (testPassword(u, cleartextPassword))
				return u;
			// FIXME what if more than 1?
		}

		return null;

	}

	/**
	 * Authenticates user using email.
	 * 
	 * @param email
	 * @param cleartextPassword
	 * @return
	 */
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
		try {
			return genericManager.findByPropertySingleResult(User.class, "email", email);
		} catch (NoResultException e) {
			return null;
		}
	}

	public User getUserByUsername(String name) {
		try {
			return genericManager.findByPropertySingleResult(User.class, "name", name);
		} catch (NoResultException e) {
			return null;
		}
	}

	public User getUserByPasswordRecoveryCode(String pwdRecCode) {
		pwdRecCode = encrypt(pwdRecCode, "MD5");
		List<User> users = genericManager.findByProperty(User.class, "passwordRecoveryCode", pwdRecCode);

		if (users.isEmpty())
			return null;
		else if (users.size() > 1)
			throw new IllegalStateException("2 users with same authentication code!");
		else
			return users.get(0);
	}

	/**
	 * Generic multi-purpose hashing routine. Clear array.
	 * 
	 * @param cleartextMessage
	 * @param algorithm
	 * @return
	 */
	public String encrypt(byte[] cleartextMessageBytes, String algorithm) {

		MessageDigest md;
		try {
			md = MessageDigest.getInstance(algorithm);
		} catch (NoSuchAlgorithmException e) {
			throw new RuntimeException(e);
		}
		md.update(cleartextMessageBytes);
		byte[] digestBytes = md.digest();
		Arrays.fill(cleartextMessageBytes, (byte) 0);
		return DatatypeConverter.printHexBinary(digestBytes);
	}

	/**
	 * Generic multi-purpose hashing routine. Clear array.
	 * 
	 * @param cleartextMessage
	 * @param algorithm
	 * @return
	 */
	public String encrypt(char[] cleartextMessage, String algorithm) {

		byte[] cleartextMessageBytes = char2byte(cleartextMessage);
		Arrays.fill(cleartextMessage, '\u0000');
		return encrypt(cleartextMessageBytes, algorithm);
	}

	/**
	 * Generic multi-purpose hashing routine. Cannot clear String's array.
	 * 
	 * @param cleartextMessage
	 * @param algorithm
	 * @return null if cleartextMessage is null
	 */
	public String encrypt(String cleartextMessage, String algorithm) {
		if (cleartextMessage == null)
			return null;
		return encrypt(cleartextMessage.getBytes(), algorithm);
	}

	/**
	 * Return a hash for given password, then clear array.
	 * 
	 * @param pwd
	 * @return
	 */
	public String encryptPassword(char[] cleartextPassword) {

		return encrypt(cleartextPassword, ALGORITHM);
	}

	/**
	 * Return a hash for given password.
	 * 
	 * @param pwd
	 * @return
	 */
	public String encryptPassword(String cleartextPassword) {

		return encrypt(cleartextPassword, ALGORITHM);
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
	 * Set user password. We consider null equal to "".
	 * 
	 * @param u
	 * @param unencryptedPassword
	 */
	public void setPassword(User u, String cleartextPassword) {
		setPassword(u, encryptPassword(cleartextPassword));
	}

	/**
	 * Test user password, then clear array
	 */
	public boolean testPassword(User u, char[] cleartextPassword) {

		// FIXME currently naive
		return cleartextPassword != null && encryptPassword(cleartextPassword).equals(u.getEncryptedPassword());
	}

	/**
	 * Authenticate given username and password through JAAS (instead of Java EE
	 * Security).
	 * 
	 * @param user,
	 *            or null
	 * @param password
	 *            cleartext password. After authentication will be cleared.
	 * @param realm
	 *            JAAS realm name
	 * @return
	 */
	public LoginContext authenticate(String user, char[] password, String realm) {

		// consider modules: Krb5LoginModule, LdapLoginModule, NTLoginModule,
		// JndiLoginModule
		// ...sun...

		PassiveCallbackHandler cbh = new PassiveCallbackHandler(user, password);

		LoginContext lc;
		try {
			lc = new LoginContext(realm, cbh); // referenced in jaas.conf

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
			Arrays.fill(password, '\u0000');
		}

		return lc;
	}

	/**
	 * Return a <b>detached</b> instance of given Menu whose submenus and subpages
	 * are all and only the ones that the user is allowed to. Return null if user is
	 * not allowed at all. Please do not save this instance!!!
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
	 * False if there is recorded an authorization for the user to the given menu.
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
	 * False if there is recorded an authorization for the user to the given page.
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
	 * Return a tree of menus, filtered according to filterMenuForUser(Menu menu,
	 * User user). Please do not save such menus!!!
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
	 * Return the (ordered) list of pages the user shall see in his menu. Not all
	 * pages are required to lie inside a Menu.
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
		String code = randomString();
		user.setPasswordRecoveryCode(encrypt(code, "MD5"));
		return code;
	}

	/**
	 * Create, set and return a new 10-characters-long random password, containing
	 * only letters and digits.
	 * 
	 * @param user
	 */
	public String newPassword(User user) {
		String pwd = randomString().substring(10);
		setPassword(user, pwd);
		return pwd;
	}

	/**
	 * Return a random 32-characters-long string, containing only letters and
	 * digits.
	 * 
	 * @param len
	 * @return
	 */
	public String randomString() {

		return encrypt("" + Math.random() + new Date(), "MD5");
	}

	/**
	 * Convert char[] to byte[] without creating any String.
	 * 
	 * @see https://stackoverflow.com/questions/5513144
	 * @param x
	 * @return
	 */
	public byte[] char2byte(char[] chars) {
		CharBuffer charBuffer = CharBuffer.wrap(chars);
		ByteBuffer byteBuffer = Charset.forName("UTF-8").encode(charBuffer);
		byte[] bytes = Arrays.copyOfRange(byteBuffer.array(), byteBuffer.position(), byteBuffer.limit());
		Arrays.fill(charBuffer.array(), '\u0000'); // clear sensitive data
		Arrays.fill(byteBuffer.array(), (byte) 0); // clear sensitive data
		return bytes;
	}

}
