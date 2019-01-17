/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
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
import com.example.myapp.main.util.ApplicationProperties;
import com.example.myapp.main.util.SessionBean;
import com.example.myapp.main.util.WebFilterHelper;

/**
 * Login filter. To be used instead of standard /j_security_check servlet. Currently not used.
 *
 */
// @WebFilter(value = "loginFilter", urlPatterns = { "*.html", "*.htm", "*.xhtml", "*.jsp" })
public class LoginFilter extends AbstractRequestFilter {

	@Inject
	ApplicationProperties appProps;
	@Inject
	SessionBean sessionBean;
	@Inject
	WebFilterHelper webFilterHelper;
	@Inject
	UsersManager usersManager;
	@Inject
	SessionManager sessionManager;

	@Override
	public boolean filterRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!webFilterHelper.excludeUrl(appProps.getProperty("login.not.required.uris").split(","), request)) {		
			String username = request.getRemoteUser();
			if (username != null) {
				User user = usersManager.getUserByUsername(username);
				if (user != null) {
					sessionManager.fillDataInSessionBean(sessionBean, user);
				}
			}
		}

		return true;
	}
}
