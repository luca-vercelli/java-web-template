/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.firstrun.filters;

import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.example.myapp.firstrun.helpers.FirstRun;
import com.example.myapp.main.util.AbstractRequestFilter;
import com.example.myapp.main.util.ApplicationProperties;
import com.example.myapp.main.util.WebFilterExclude;

/**
 * This Filter determines if database exists and is populated. After
 * installation, you should disable this.
 * 
 * If database tables do not exist, this will raise an exception.
 * 
 * Anyway, check will be performed just once after each reboot.
 * 
 * This should execute *before* any filter that use database. How to?
 *
 */
@WebFilter(filterName="firstRunFilter", urlPatterns = { "*.html", "*.htm", "*.xhtml", "*.jsp" })
public class FirstRunFilter extends AbstractRequestFilter {

	@PersistenceContext
	EntityManager em;
	@Inject
	FirstRun firstRunPopulator;
	@Inject
	ApplicationProperties appProps;
	@Inject
	WebFilterExclude webFilterExclude;

	private boolean databasePopulated = false;

	@Override
	public boolean filterRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {
		if (!databasePopulated) {

			String contextPath = request.getContextPath();

			if (!webFilterExclude.excludeUrl(appProps.getProperty("errors.uris").split(","), request)) {

				Long n = 0L;

				// is transaction required?
				try {
					TypedQuery<Long> query = em.createQuery("SELECT COUNT(*) FROM Settings", Long.class);
					n = query.getSingleResult();

					LOG.debug("" + n + " rows found in APP_SETTINGS");

					if (n == 0L) {
						firstRunPopulator.populateDatabase();
						databasePopulated = true;
					}

				} catch (Exception exc) {

					LOG.error("Error during firstRun db connection", exc);

					response.sendRedirect(
							contextPath + appProps.getProperty("error500.uri") + "?errorcode=error.db.connection");
					return false;

				}
			}
		}
		return true;
	}
}