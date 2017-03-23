/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.actions;

import java.util.List;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import org.apache.struts2.interceptor.SessionAware;

import com.example.myapp.crud.EntityManagerFactory;
import com.example.myapp.login.db.User;
import com.opensymphony.xwork2.ActionSupport;

public class Login extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 7397484529732988537L;

	public final static String SESSION_ATTRIBUTE = "authUser";

	private String userId;
	private String email;
	private String pwd;

	private Map<String, Object> sessionMap;

	@Override
	public String execute() {

		sessionMap.remove(SESSION_ATTRIBUTE);

		if ((userId == null || userId.equals("")) && (email == null || email.equals(""))) {

			// This can also happen when user go to "Login" address for the
			// first time

			addActionError(getText("login.missing.parameters")); // FIXME can be
																	// improved
			return LOGIN;
		}

		User user = null;

		EntityManager em = EntityManagerFactory.createEntityManager(); // FIXME
																		// ...
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			TypedQuery<User> query;
			if (userId != null) {
				query = em
						.createQuery("from User where userId = :userId and encryptedPassword = :pwd and active = :true",
								User.class)
						.setParameter("userId", userId);
			} else {

				query = em.createQuery("from User where email = :email and encryptedPassword = :pwd and active = :true",
						User.class).setParameter("email", email);
			}
			query.setParameter("true", true).setParameter("encryptedPassword", pwd); // FIXME
			// encrypt

			List<User> users = query.getResultList();

			tx.commit();

			if (users.isEmpty()) {
				checkThatOneUserExists();
				addActionError(getText("login.err.auth"));
				return LOGIN;
			} else if (users.size() > 1) {
				addActionError(getText("login.more.users", userId, email));
				return ERROR;
			}

			sessionMap.put(SESSION_ATTRIBUTE, user);
			return SUCCESS;

		} catch (Exception exc) {
			if (tx != null && tx.isActive())
				tx.rollback();
			return ERROR;
		}
	}

	protected void checkThatOneUserExists() {
		EntityManager em = EntityManagerFactory.createEntityManager(); // FIXME
																		// ...
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			TypedQuery<Long> query;
			query = em.createQuery("select count(*) from User", Long.class);
			Long n = query.getSingleResult();
			if (n == 0L) {
				// first access to database. We have to create a default user:
				// admin, with password admin
				User u = new User();
				u.setActive(true);
				u.setName("Admin");
				u.setUserId("admin");
				u.setEmail("admin@example.com");
				em.persist(u);
			}
			tx.commit();

		} catch (Exception exc) {
			if (tx != null && tx.isActive())
				tx.rollback();
		}
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPwd() {
		return pwd;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public void setSession(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}
}
