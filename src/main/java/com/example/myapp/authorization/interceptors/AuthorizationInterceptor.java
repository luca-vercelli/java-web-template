/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.authorization.interceptors;

import com.example.myapp.login.actions.Login;
import com.example.myapp.authorization.db.AuthUser;
import com.example.myapp.crud.EntityManagerFactory;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;
import org.apache.log4j.Logger;

import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * This interceptor should handle authorization. If user is not authorized to
 * execute the requested action, result to LOGIN. User must be already logged.
 * 
 * @author LV
 *
 */
public class AuthorizationInterceptor implements Interceptor {

	private static final long serialVersionUID = 294058339606947197L;

	private static final Logger LOG = Logger.getLogger(AuthorizationInterceptor.class);

	@Override
	public void init() {
		// NO OP
	}

	@Override
	public void destroy() {
		// NO OP
	}

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		String actionClassName = invocation.getAction().getClass().getName();

		AuthUser user = (AuthUser) ActionContext.getContext().getSession().get(Login.SESSION_ATTRIBUTE);
		assert user != null;
		LOG.debug("AuthorizationInterceptor - User " + user + " accessing action " + actionClassName);

		boolean auth = false;

		EntityManager em = EntityManagerFactory.createEntityManager(); // FIXME
																		// ...
		EntityTransaction tx = em.getTransaction();
		try {
			tx.begin();

			TypedQuery<AuthUser> query = em
					.createQuery("from AuthUser u join fetch Page p where u.role = p.allowedRole ", AuthUser.class);

			// TODO

			auth = !query.getResultList().isEmpty();

			tx.commit();
		} catch (Exception exc) {
			if (tx != null && tx.isActive())
				tx.rollback();
		}

		if (!auth) {
			LOG.info("Unauthorized access: " + actionClassName + " by user " + user.getId());
			return ActionSupport.LOGIN; // FIXME should be another one?
		}

		String result = invocation.invoke();

		return result;
	}

}
