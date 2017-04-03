/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.actions;

import java.util.Map;

import javax.security.auth.login.LoginContext;
import javax.security.auth.login.LoginException;

import org.apache.struts2.convention.annotation.InterceptorRef;
import org.apache.struts2.convention.annotation.InterceptorRefs;
import org.apache.struts2.interceptor.SessionAware;

import com.opensymphony.xwork2.ActionSupport;

/**
 * Perform JAAS logout, then clears session variables.
 */
@InterceptorRefs({ @InterceptorRef("defaultStack") })
public class Logout extends ActionSupport implements SessionAware {

	private static final long serialVersionUID = -7729139570317620807L;

	private Map<String, Object> sessionMap;

	@Override
	public String execute() {

		if (sessionMap.get(Login.SESSION_LOGIN_CONTEXT) != null) {
			try {
				((LoginContext) sessionMap.get(Login.SESSION_LOGIN_CONTEXT)).logout();
			} catch (LoginException e) {
				// Nothing to do
			}
		}
		
		LOG.debug("Entering login action");
		sessionMap.remove(Login.SESSION_USER);
		sessionMap.remove(Login.SESSION_LOGIN_CONTEXT);

		return SUCCESS;
	}

	@Override
	public void setSession(Map<String, Object> sessionMap) {
		this.sessionMap = sessionMap;
	}
}
