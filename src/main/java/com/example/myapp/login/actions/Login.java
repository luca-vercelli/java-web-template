package com.example.myapp.login.actions;

import java.util.Map;

import org.apache.struts2.interceptor.SessionAware;

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
		User user = null;

		// TODO: load user from session

		if (user != null) {
			sessionMap.put(SESSION_ATTRIBUTE, user);
			return SUCCESS;
		}

		addActionError("login.err.auth");
		return LOGIN;
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
