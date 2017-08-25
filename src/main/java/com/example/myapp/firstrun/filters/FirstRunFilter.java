/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.firstrun.filters;

import java.io.IOException;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
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

import com.example.myapp.firstrun.ejb.FirstRun;

/**
 * This Filter determines if database exists and is populated. After
 * installation, you should disable this.
 * 
 * If database tables do not exist, this will raise an exception.
 * 
 * Anyway, check will be performed just once after each reboot.
 * 
 * This should execute *before* login filter...
 *
 */
@WebFilter("/*")
public class FirstRunFilter implements Filter {

	@Inject
	Logger LOG;
	@PersistenceContext
	EntityManager em;
	@Inject
	FirstRun firstRunPopulator;

	private boolean databasePopulated = false;

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {

			if (!databasePopulated) {
				Long n = 0L;

				// is transaction required?
				TypedQuery<Long> query = em.createQuery("SELECT COUNT(*) FROM Settings", Long.class);
				n = query.getSingleResult();
				LOG.debug("" + n + " rows found in APP_SETTINGS");

				if (n == 0L) {
					firstRunPopulator.populateDatabase();
					databasePopulated = true;
				}

				chain.doFilter(req, resp); // Just continue chain
			}
		} else {
			// should not pass here
			System.err.println("Not HTTP ? Why here?");
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