/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.login.resources;

import java.security.Principal;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.security.auth.login.LoginContext;
import javax.ws.rs.Consumes;
import javax.ws.rs.FormParam;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import org.slf4j.Logger;

import com.example.myapp.login.actions.TokenManager;
import com.example.myapp.login.helpers.UsersManager;

/**
 * Authentication endpoint for token-based (JWT) security. Currently we do not
 * use such authentication.
 * 
 * @author Luca Vercelli
 *
 */
@Stateless
@Path("/")
public class TokenAuthenticationEndPoint {

	@Inject
	UsersManager usersManager;
	@Inject
	TokenManager tokenManager;
	@Inject
	Logger LOG;

	/**
	 * Plain-test authentication point.
	 * 
	 * @param userId
	 * @param pwd
	 * @return
	 */
	@POST
	@Path("authenticate")
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	@Produces(MediaType.TEXT_PLAIN)
	public Response find(@FormParam("userId") String userId, @FormParam("pwd") String pwd) {

		Credentials credentials = new Credentials();
		credentials.userId = userId;
		credentials.pwd = pwd;
		JSONResponse resp = commonGetToken(credentials);

		if (resp.errorCode == Status.OK.ordinal())
			return Response.ok(resp.token).build();
		else
			return Response.ok(resp.errorMessage).status(resp.errorCode).build();

	}

	/**
	 * JSON authentication point.
	 * 
	 * @param userId
	 * @param pwd
	 * @return
	 */
	@POST
	@Path("authenticate")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.APPLICATION_JSON)
	public Response find(Credentials credentials) {

		JSONResponse resp = commonGetToken(credentials);

		return Response.ok(resp).status(resp.errorCode).build();
	}

	/**
	 * Common code
	 * 
	 * @param credentials
	 * @return
	 */
	private JSONResponse commonGetToken(Credentials credentials) {
		Principal user = null;
		JSONResponse resp = new JSONResponse();

		if (credentials == null || credentials.userId == null || credentials.userId.equals("")) {
			// FIXME what's the best error code?
			resp.errorCode = Status.EXPECTATION_FAILED.ordinal();
			resp.errorMessage = "Missing credentials";
			return resp;
		}

		// TODO integrate with Java EE Security
		LoginContext lc = usersManager.authenticate(credentials.userId, credentials.pwd.toCharArray(), "MainApp");

		if (lc != null && !lc.getSubject().getPrincipals().isEmpty()) {
			user = lc.getSubject().getPrincipals().iterator().next();
		}

		if (user == null) {
			// FIXME what's the best error code?
			resp.errorCode = Status.EXPECTATION_FAILED.ordinal();
			resp.errorMessage = "Invalid credentials";
			return resp;
		}

		// At last, user is authenticated
		LOG.info("User authenticated: " + user);

		resp.token = tokenManager.issueToken(credentials.userId);

		if (resp.token == null) {
			resp.errorCode = Status.INTERNAL_SERVER_ERROR.ordinal();
			resp.errorMessage = "Error during token generation";
			return resp;
		}

		return resp;
	}

	public static class Credentials {
		String userId;
		String pwd;
	}

	public static class JSONResponse {
		Integer errorCode = Status.OK.ordinal(); // 200
		String errorMessage;
		String token;
	}
}
