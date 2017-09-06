package com.example.myapp.main.util;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Do you prefer using Filter or ContainerRequestFilter?
 */
public abstract class AbstractRequestFilter implements Filter {

	@Override
	public final void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {
		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {

			HttpServletRequest request = (HttpServletRequest) req;
			HttpServletResponse response = (HttpServletResponse) resp;

			try {
				if (filterRequest(request, response))
					chain.doFilter(req, resp); // continue chain
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
	 * Executed before filter chain.
	 * 
	 * @param request
	 * @param response
	 * @return success. If true, chain may continue.
	 * @throws IOException
	 */
	public abstract boolean filterRequest(HttpServletRequest request, HttpServletResponse response) throws Exception;

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {

	}

	@Override
	public void destroy() {
	}
}
