/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.login.filters;

import javax.inject.Inject;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;
import javax.ws.rs.core.Response;

import org.slf4j.Logger;

import com.example.myapp.login.actions.TokenManager;
import com.example.myapp.main.util.AbstractRequestFilter;

/**
 * Filter for token-based (JWT) authentication. Currently not used.
 * 
 * @see https://stackoverflow.com/questions/26777083
 */
@WebFilter(filterName = "tokenFilter", value = "/secured/*")
public class TokenFilter
		extends AbstractRequestFilter /* may use ContainerRequestFilter ? */ {

	public static final String AUTHENTICATION_SCHEME = "Bearer"; // JWT
																	// standard

	@Inject
	TokenManager tokenManager;

	@Inject
	private Logger LOG;
	
	/**
	 * Do filter
	 */
	@Override
	public boolean filterRequest(HttpServletRequest request, HttpServletResponse response) throws Exception {

		// Get the Authorization header from the request
		String authorizationHeader = request.getHeader(HttpHeaders.AUTHORIZATION);

		// Validate the Authorization header
		if (!isTokenBasedAuthentication(authorizationHeader)) {
			abortWithUnauthorized(request, response);
			return false;
		}

		// Extract the token from the Authorization header
		String token = authorizationHeader.substring(AUTHENTICATION_SCHEME.length()).trim();

		// Validate the token
		boolean valid = tokenManager.validateToken(token, request.getRemoteAddr());

		LOG.info("TokenFilter finished.");

		if (!valid) {
			abortWithUnauthorized(request, response);
			return false;
		}

		return true;
	}

	/**
	 * Check if the Authorization header is valid. It must not be null and must
	 * be prefixed with "Bearer" plus a whitespace Authentication scheme
	 * comparison must be case-insensitive
	 * 
	 * @param authorizationHeader
	 * @return
	 */
	private boolean isTokenBasedAuthentication(String authorizationHeader) {

		return authorizationHeader != null
				&& authorizationHeader.toLowerCase().startsWith(AUTHENTICATION_SCHEME.toLowerCase() + " ");
	}

	/**
	 * Abort the filter chain with a 401 status code. The "WWW-Authenticate" is
	 * sent along with the response.
	 * 
	 * @param request
	 * @param response
	 */
	private void abortWithUnauthorized(HttpServletRequest request, HttpServletResponse response) {

		response.setStatus(Response.Status.UNAUTHORIZED.getStatusCode());
		response.setHeader(HttpHeaders.WWW_AUTHENTICATE, AUTHENTICATION_SCHEME);
	}

}