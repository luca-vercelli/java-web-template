/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.interceptors;

import java.util.Map;

import org.apache.log4j.Logger;

import com.example.myapp.login.actions.Login;
import com.example.myapp.login.db.User;
import com.opensymphony.xwork2.ActionContext;
import com.opensymphony.xwork2.ActionInvocation;
import com.opensymphony.xwork2.ActionSupport;
import com.opensymphony.xwork2.interceptor.Interceptor;

/**
 * This interceptor checks that user is logged in looking at
 * Login.SESSION_ATTRIBUTE session bean. If there is no such bean, result to
 * LOGIN.
 * 
 * @author LV
 *
 */
public class LoginInterceptor implements Interceptor {

	private static final long serialVersionUID = 294058339606947197L;

	private static final Logger LOG = Logger.getLogger(LoginInterceptor.class);

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
		Map<String, Object> session = ActionContext.getContext().getSession();
		User user = (User) session.get(Login.SESSION_ATTRIBUTE);
		
		LOG.debug("LoginInterceptor - User " + user + " accessing action " + actionClassName);
		
		if (user == null)
			return ActionSupport.LOGIN;

		String result = invocation.invoke();

		return result;
	}

}
