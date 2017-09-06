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

import com.example.myapp.main.util.AbstractRequestFilter;
import com.example.myapp.main.util.ApplicationProperties;
import com.example.myapp.main.util.SessionBean;
import com.example.myapp.main.util.WebFilterExclude;

import org.slf4j.Logger;

/**
 * Login filter. Check if a User is already in session or not.
 * 
 * This filter must <b>not</b> apply to login-related pages. Unluckily,
 * WebFilter's do not allow to exclude paths. So we must implement an ad-hoc
 * (quite ugly) solution.
 *
 */
@WebFilter(value = "loginFilter", urlPatterns = { "*.html", "*.htm", "*.xhtml", "*.jsp" })
public class LoginFilter extends AbstractRequestFilter {

	@Inject
	Logger LOG;
	@Inject
	ApplicationProperties appProps;
	@Inject
	SessionBean sessionBean;
	@Inject
	WebFilterExclude webFilterExclude;

	@Override
	public boolean filterRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {
		boolean loginRequired = true;
		boolean loginSuccess = false;

		String contextPath = request.getContextPath();

		loginRequired = !webFilterExclude.excludeUrl(appProps.getProperty("login.not.required.uris").split(","),
				request);

		if (sessionBean != null && loginRequired) {
			loginSuccess = (sessionBean.getUser() != null);
		}

		if (!loginRequired || loginSuccess) {
			return true;
		} else {
			LOG.info("Redirecting to login page");
			response.sendRedirect(contextPath + appProps.getProperty("login.uri"));
			return false;
		}
	}
}
