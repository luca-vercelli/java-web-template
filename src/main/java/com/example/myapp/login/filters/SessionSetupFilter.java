/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.filters;

import java.io.IOException;
import javax.inject.Inject;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.myapp.login.actions.SessionManager;
import com.example.myapp.login.helpers.UsersManager;
import com.example.myapp.main.entity.User;
import com.example.myapp.main.util.AbstractRequestFilter;
import com.example.myapp.main.util.SessionBean;

/**
 * Login filter. Check if a User is already in session or not.
 * 
 * This filter must <b>not</b> apply to login-related pages. Unluckily,
 * WebFilter's do not allow to exclude paths. So we must implement an ad-hoc
 * (quite ugly) solution.
 * 
 * As we are using EE Security, this is <b>not</b> meant to check if user is
 * logged in or not. User <b>is</b> logged in. We just need to populate
 * sessionBean.
 *
 */
@WebFilter(value = "sessionSetupFilter", urlPatterns = { "*.html", "*.htm", "*.xhtml", "*.jsp" })
public class SessionSetupFilter extends AbstractRequestFilter {

	@Inject
	SessionBean sessionBean;
	@Inject
	UsersManager usersManager;
	@Inject
	SessionManager sessionManager;

	@Override
	public boolean filterRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String username = request.getRemoteUser();
		if (username != null) {
			User user = usersManager.getUserByUsername(username);
			if (user != null) {
				sessionManager.fillDataInSessionBean(sessionBean, user);
			}
		}

		return true;
	}
}
