/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.login.actions;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.slf4j.Logger;

import com.example.myapp.login.helpers.UsersManager;
import com.example.myapp.main.entity.User;
import com.example.myapp.main.util.SessionBean;

@Stateless
// FIXME: Better @Stateful + @Inject SessionBean?
public class SessionManager {

	@Inject
	UsersManager usersManager;
	@Inject
	Logger LOG;

	public void clearSession(SessionBean sessionBean) {

		sessionBean.setUser(null);
		sessionBean.getMenus().clear();
		sessionBean.getRoles().clear();
	}

	public void fillDataInSessionBean(SessionBean sessionBean, User user) {

		sessionBean.setUser(user);
		sessionBean.setMenus(usersManager.getMenusForUser(user));
		sessionBean.setRoles(user.getRoles());
	}
}
