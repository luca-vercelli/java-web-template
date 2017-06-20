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
import org.slf4j.Logger;

import com.example.myapp.main.util.ApplicationProperties;
import com.example.myapp.main.util.SessionBean;

/**
 * Most implementations assume that roles are hard-written in some
 * annotations, @see e.g. https://stackoverflow.com/questions/26777083. Instead,
 * we read roles from a database table.
 * 
 * @author luca vercelli
 *
 */
@WebFilter(value = "authFilter", urlPatterns = { "*.html", "*.htm", "*.xhtml", "*.jsp", "/rest" })
public class AuthorizationFilter implements Filter {

	@Inject
	Logger LOG;

	@Inject
	ApplicationProperties appProps;

	@Inject
	SessionBean sessionBean;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {

			boolean authorizaionRequired = false; // TODO: true
			boolean authorizationSuccess = false;

			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) resp;
			String contextPath = request.getContextPath();
			String uri = request.getRequestURI();

			uri = uri.replaceAll("/+", "/"); // convert /myapp///ui -> /myapp/ui

			// TODO: authorizationSuccess = ...

			if (!authorizaionRequired || authorizationSuccess) {
				chain.doFilter(req, resp); // Just continue chain
			} else {
				LOG.info("Redirecting to login page");
				response.sendRedirect(contextPath + appProps.getProperty("404errorPage.uri"));
			}

		} else {
			// should not pass here
			LOG.error("Not HTTP ? Why here?");
			chain.doFilter(req, resp); // Just continue chain
		}
	}

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
	}

	@Override
	public void destroy() {
	}
}