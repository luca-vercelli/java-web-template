/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.filters;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.apache.log4j.Logger;

import com.example.myapp.login.db.User;
import com.example.myapp.main.util.ApplicationProperties;
import com.example.myapp.main.util.SessionBean;

/**
 * Login filter. Check if a User is already in session or not.
 * 
 * This filter must <b>not</b> apply to login-related pages. Unluckily,
 * WebFilter's do not allow to exclude paths. So we must implement an ad-hoc
 * (quite ugly) solution.
 *
 */
@WebFilter(value = "loginFilter", urlPatterns = { "*.html", "*.htm", "*.xhtml", "*.jsp" })
public class LoginFilter implements Filter {

	private final static Logger LOG = Logger.getLogger(LoginFilter.class); // FIXME
																			// can
																			// Inject?

	@Inject
	ApplicationProperties appProps;

	@Inject
	SessionBean sessionBean;
	
	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {

			boolean loginRequired = true;
			boolean loginSuccess = false;

			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) resp;
			String contextPath = request.getContextPath();
			String uri = request.getRequestURI();

			uri = uri.replaceAll("/+", "/"); // convert /myapp///ui -> /myapp/ui

			for (String allowedPath : appProps.getProperty("login.not.required.uris").split(",")) {

				if (!allowedPath.equals("") && uri.startsWith(contextPath + allowedPath)) {
					loginRequired = false;
					break;
				}
			}

			if (sessionBean != null && loginRequired) {
				LOG.info("User: " + sessionBean.getUser());
				loginSuccess = (sessionBean.getUser() != null);
			}

			if (!loginRequired || loginSuccess) {
				chain.doFilter(req, resp); // Just continue chain
			} else {
				LOG.info("Redirecting to login page");
				response.sendRedirect(contextPath + appProps.getProperty("login.uri"));
			}

		} else {
			// should not pass here
			LOG.error("Not HTTP ? Why here?");
			chain.doFilter(req, resp); // Just continue chain
		}
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
