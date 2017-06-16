/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.main.util;

import java.io.Serializable;
import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.inject.Named;
import javax.security.auth.login.LoginContext;

import com.example.myapp.authorization.entity.Role;
import com.example.myapp.login.entity.User;
import com.example.myapp.main.entity.Menu;

/**
 * This bean contains all data that need to be stored in session (user,
 * authorizations, ...)
 * 
 * @author Luca Vercelli
 *
 */
@Named("sessionBean")
@SessionScoped
public class SessionBean implements Serializable {

	private static final long serialVersionUID = -2545698895043737577L;
	
	private User user;
	private List<Role> roles;
	private List<Menu> menus;
	private LoginContext loginContext;
	private String language;

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public List<Role> getRoles() {
		return roles;
	}

	public void setRoles(List<Role> roles) {
		this.roles = roles;
	}

	public List<Menu> getMenus() {
		return menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public LoginContext getLoginContext() {
		return loginContext;
	}

	public void setLoginContext(LoginContext loginContext) {
		this.loginContext = loginContext;
	}

	public String getLanguage() {
		return language;
	}

	public void setLanguage(String language) {
		this.language = language;
	}
}
