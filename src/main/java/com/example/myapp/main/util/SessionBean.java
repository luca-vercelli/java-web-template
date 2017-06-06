package com.example.myapp.main.util;

import java.util.List;

import com.example.myapp.authorization.db.Menu;
import com.example.myapp.authorization.db.Role;
import com.example.myapp.login.db.User;

public class SessionBean {

	private User user;
	private List<Role> roles;
	private List<Menu> menus;
	
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
}
