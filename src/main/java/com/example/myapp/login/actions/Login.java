/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.actions;

import java.util.Map;

import javax.security.auth.login.LoginContext;

import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.interceptor.SessionAware;

import com.example.myapp.login.db.User;
import com.example.myapp.login.helpers.UsersHelper;
import com.opensymphony.xwork2.ActionSupport;

/**
 * This Action is called before and after user login page. This Action is in the
 * default namespace and uses the default interceptors stack.
 */
@InterceptorRefs({ @InterceptorRef("defaultStack") })
public class Login extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = 7397484529732988537L;

	public final static String SESSION_USER = "authUser";
	public final static String SESSION_LOGIN_CONTEXT = "authLC";

	private String userId;
	private String pwd;

	private Map<String, Object> sessionMap;

	@Override
	public String execute() {

		LOG.debug("Entering login action");
		sessionMap.remove(SESSION_USER);
		sessionMap.remove(SESSION_LOGIN_CONTEXT);

		User user = null;
		try {
			if (userId == null || userId.equals("")) {
				// This can also happen when user go to "Login" address for the
				// first time

				// addActionError(getText("login.missing.parameters"));
				return INPUT;
			}

			LoginContext lc = UsersHelper.getInstance().authenticate(userId, pwd);
			if (lc != null && !lc.getSubject().getPrincipals().isEmpty()) {
				user = (User) lc.getSubject().getPrincipals().iterator().next();
			}

			if (user == null) {
				addActionError(getText("login.err.auth"));
				return INPUT;

			}

			// At last, user is authenticated
			sessionMap.put(SESSION_LOGIN_CONTEXT, user);
			sessionMap.put(SESSION_USER, user);
			return SUCCESS;

		} finally {
			pwd = null; // should trigger GC...
		}
	}

	public String getUserId() {
		return userId;
	}

	public void setUserId(String userId) {
		this.userId = userId;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public void setSession(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}
}
