/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.main.util;

import java.util.List;

import javax.enterprise.context.SessionScoped;
import javax.security.auth.login.LoginContext;

import com.example.myapp.authorization.db.Role;
import com.example.myapp.login.db.User;
import com.example.myapp.main.db.Menu;

@SessionScoped
public class SessionBean {

	private User user;
	private List<Role> roles;
	private List<Menu> menus;
	private LoginContext loginContext;
	
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
}
