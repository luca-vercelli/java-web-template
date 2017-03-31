package com.example.myapp.firstrun.interceptors;

import javax.persistence.EntityManager;
import javax.persistence.EntityTransaction;
import javax.persistence.TypedQuery;

import com.example.myapp.crud.EntityManagerUtil;
import com.example.myapp.util.interceptors.AbstractInterceptor;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;

/**
 * This interceptor determines if database exists and is populated. After
 * installation, you should disable this.
 *
 */
public class FirstRunInterceptor extends AbstractInterceptor {

	private static final long serialVersionUID = 2135861363314951301L;

	@Override
	public String intercept(ActionInvocation invocation) throws Exception {

		LOG.debug("Entering first-run interceptor");

		EntityManager em;
		EntityTransaction tx;
		Long n;
		try {
			em = EntityManagerUtil.getEntityManager();
			tx = em.getTransaction();

			tx.begin();
			//FIXME should use Criteria
			TypedQuery<Long> query = em.createQuery("SELECT COUNT(*) FROM Settings", Long.class);
			n = query.getSingleResult();
			LOG.debug("" + n + " rows found in APP_SETTINGS");
			tx.commit();

		} catch (Exception exc) {
			// FIXME should call addActionError() ...
			LOG.error("Database error", exc);
			return ActionSupport.ERROR;
		}

		if (n == 0L) {
			return "first-run";
		}

		return invocation.invoke();
	}

}
