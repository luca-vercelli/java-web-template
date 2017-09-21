package com.example.myapp.main.util;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

/**
 * Do you prefer using Filter or ContainerResponseFilter?
 */
public abstract class AbstractResponseFilter implements Filter {

	@Inject
	protected Logger LOG;

	@Override
	public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {

			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) resp;

			chain.doFilter(req, resp); // continue chain
			try {

				LOG.info("Entering filter " + this.getClass().getName());

				filterResponse(request, response);
			} catch (IOException e) {
				throw e;
			} catch (ServletException e) {
				throw e;
			} catch (Exception e) {
				throw new ServletException(e);
			}

		} else {
			// Not HTTP ? Why here?
			chain.doFilter(req, resp); // Just continue chain
		}
	}

	/**
	 * Executed after filter chain.
	 * 
	 * @param request
	 * @param response
	 */
	public abstract void filterResponse(HttpServletRequest request, HttpServletResponse response) throws Exception;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {
	}

}
