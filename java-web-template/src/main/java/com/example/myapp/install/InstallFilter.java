/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.install;

import java.io.IOException;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.myapp.main.util.AbstractRequestFilter;
import com.example.myapp.main.util.ApplicationProperties;

/**
 * Try to understand if this is first run, so that installation is needed.
 *
 */
@WebFilter(value = "installFilter", urlPatterns = { "/*" })
public class InstallFilter extends AbstractRequestFilter {

	@EJB
	InstallEJB ejb;
	@Inject
	ApplicationProperties applicationProperties;

	@Override
	public boolean filterRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

		if (!request.getRequestURL().toString().endsWith("/install")) {

			if (!ejb.checkIfDbExists()) {
				LOG.error("Cannot connect to database, or tables not created. Nothing done.");
				return false;
			}

			if (ejb.checkIfDbPopulated()) {
				return true;
			}

			LOG.info("Database not populated yet. Going to install.");
			response.sendRedirect("install");
			return false;
		}

		// Go on to InstallServlet
		return true;
	}
}
