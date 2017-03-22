package com.example.myapp.authorization.interceptors;

import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.example.myapp.login.actions.Login;
import com.example.myapp.authorization.db.AuthUser;
import com.example.myapp.crud.HibernateUtil;
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

		String className = invocation.getAction().getClass().getName();

		AuthUser user = (AuthUser) ActionContext.getContext().getSession().get(Login.SESSION_ATTRIBUTE);
		assert user != null;

		Session session = HibernateUtil.getSession();
		Transaction tx = session.beginTransaction();

		//TODO
		Query query = session.createQuery("from User u join fetch Page p where u.role = p.allowedRole ");

		boolean auth = !query.list().isEmpty();

		tx.commit();

		if (!auth)
			return ActionSupport.LOGIN; // FIXME should be another one?

		String result = invocation.invoke();

		return result;
	}

}
