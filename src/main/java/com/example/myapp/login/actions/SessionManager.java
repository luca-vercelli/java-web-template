package com.example.myapp.login.actions;

import javax.ejb.Stateless;
import javax.inject.Inject;

import com.example.myapp.login.entity.User;
import com.example.myapp.login.helpers.UsersManager;
import com.example.myapp.main.util.SessionBean;

@Stateless
public class SessionManager {

	@Inject
	SessionBean sessionBean;
	@Inject
	UsersManager usersManager;

	public void clearSession() {
		sessionBean.setLoginContext(null);
		sessionBean.setUser(null);
		sessionBean.getMenus().clear();
		sessionBean.getRoles().clear();
		// language is *not* cleared
	}

	public void fillDataInSessionBean(User user, String language) {

		sessionBean.setUser(user);

		if (language != null)
			sessionBean.setLanguage(language);
		// else set language=default locale ...

		sessionBean.setMenus(usersManager.getMenusForUser(user));

		sessionBean.setRoles(user.getRoles());
	}
}
