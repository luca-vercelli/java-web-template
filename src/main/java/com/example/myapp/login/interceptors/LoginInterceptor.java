package com.example.myapp.login.interceptors;

import java.util.Map;

import com.example.myapp.login.actions.Login;
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

		Map<String, Object> session = ActionContext.getContext().getSession();
		if (session.get(Login.SESSION_ATTRIBUTE) == null)
			return ActionSupport.LOGIN;

		String result = invocation.invoke();

		return result;
	}

}
