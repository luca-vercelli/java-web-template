/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.login.filters;

import java.util.List;

import javax.inject.Inject;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.myapp.crud.DataManager;
import com.example.myapp.main.entity.Page;
import com.example.myapp.main.entity.Role;
import com.example.myapp.main.util.AbstractRequestFilter;
import com.example.myapp.main.util.ApplicationProperties;
import com.example.myapp.main.util.SessionBean;
import com.example.myapp.main.util.WebFilterHelper;

/**
 * Most implementations assume that roles are hard-written in some
 * annotations, @see e.g. https://stackoverflow.com/questions/26777083. Instead,
 * we read roles from a database table.
 * 
 * @author luca vercelli
 *
 */
@WebFilter(filterName = "authFilter", urlPatterns = { "*.html", "*.htm", "*.xhtml", "*.jsp", "/rest" })
public class AuthorizationFilter extends AbstractRequestFilter {

	@Inject
	ApplicationProperties appProps;
	@Inject
	SessionBean sessionBean;
	@Inject
	DataManager genericManager;
	@Inject
	WebFilterHelper webFilterExclude;

	@Override
	public boolean filterRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

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

				LOG.warn("No Page item found for uri " + uri + ", so it is allowed");
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

			// FIXME should be 401

			LOG.info("Redirecting to 403 page");
			String contextPath = request.getContextPath();
			response.sendRedirect(contextPath + appProps.getProperty("error403.uri"));
			return false;
		} else {
			return true;
		}
	}
}