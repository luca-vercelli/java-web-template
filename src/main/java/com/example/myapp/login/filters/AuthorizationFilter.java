/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.filters;

import java.io.IOException;
import java.util.List;

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

import com.example.myapp.crud.GenericManager;
import com.example.myapp.main.entity.Page;
import com.example.myapp.main.entity.Role;
import com.example.myapp.main.util.ApplicationProperties;
import com.example.myapp.main.util.SessionBean;
import com.example.myapp.main.util.WebFilterExclude;

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
	@Inject
	GenericManager genericManager;
	@Inject
	WebFilterExclude webFilterExclude;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {

			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) resp;

			boolean authorizationRequired = !webFilterExclude
					.excludeUrl(appProps.getProperty("login.not.required.uris").split(","), request);

			boolean authorizationSuccess = false;

			if (authorizationRequired) {

				String uri = request.getRequestURI();
				uri = uri.replaceAll("/+", "/"); // convert /myapp///ui ->
													// /myapp/ui

				String contextPath = request.getContextPath();
				if (uri.length() > contextPath.length())
					uri = uri.substring(contextPath.length());

				// FIXME should cache auth?
				List<Page> lp = genericManager.findByProperty(Page.class, "url", uri);
				if (lp == null || lp.isEmpty()) {

					LOG.warn("No Page item found for uri " + uri);
					authorizationRequired = false;

				} else {

					LOG.info("Verifying authentication for Page " + uri);
					Page page = lp.get(0);
					boolean someAuthorizationWasSet = false;
					for (Role r : sessionBean.getRoles()) {
						someAuthorizationWasSet = true;
						if (page.getAuthorizedRoles().contains(r)) {
							authorizationSuccess = true;
							break;
						}
					}

					if (!someAuthorizationWasSet) {
						// Page is subject to auth, however no auth were set
						authorizationSuccess = true;
					}
				}
			}

			if (authorizationRequired && !authorizationSuccess) {
				LOG.info("Redirecting to 403 page");
				String contextPath = request.getContextPath();
				response.sendRedirect(contextPath + appProps.getProperty("error403.uri"));
			} else {
				chain.doFilter(req, resp); // Just continue chain
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