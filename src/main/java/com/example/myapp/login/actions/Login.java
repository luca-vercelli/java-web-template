/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.actions;

import java.util.Map;

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

	public final static String SESSION_ATTRIBUTE = "authUser";

	private String userId;
	private String email;
	private String pwd;

	private Map<String, Object> sessionMap;

	@Override
	public String execute() {

		LOG.debug("Entering login action");
		sessionMap.remove(SESSION_ATTRIBUTE);

		User user = null;
		try {
			if (email != null && !email.equals(""))
				user = UsersHelper.getInstance().getUserByEmailAndPassword(email, pwd);
			else if (userId != null && !userId.equals(""))
				user = UsersHelper.getInstance().getUserByUsernameAndPassword(userId, pwd);
			else {
				// This can also happen when user go to "Login" address for the
				// first time

				// addActionError(getText("login.missing.parameters"));
				return INPUT;
			}
			if (user == null) {
				addActionError(getText("login.err.auth"));
				return INPUT;

			}
			sessionMap.put(SESSION_ATTRIBUTE, user);
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

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public void setPwd(String pwd) {
		this.pwd = pwd;
	}

	@Override
	public void setSession(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}
}
