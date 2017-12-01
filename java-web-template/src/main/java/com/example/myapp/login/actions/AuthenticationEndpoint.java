/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.login.actions;

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

import com.example.myapp.login.helpers.UsersManager;
import com.example.myapp.main.entity.User;
import com.example.myapp.main.util.SessionBean;

/**
 * Perform authentication. Currently not used.
 * 
 */
@Path("/doLogin")
public class AuthenticationEndpoint {

	@Inject
	SessionBean sessionBean;
	@Inject
	UsersManager usersManager;
	@Inject
	Logger LOG;

	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("userId") String userId, @FormParam("pwd") String pwd) {

		try {
			sessionBean.setUser(null);

			User user = null;

			if (userId == null || userId.equals("")) {
				// This can also happen when user go to "Login" address for the
				// first time

				// addActionError(getText("login.missing.parameters"));
				return Response.ok(Status.BAD_REQUEST).build();
			}

			// TODO integrate with Java EE Security
			LoginContext lc = usersManager.authenticate(userId, pwd.toCharArray(), "MainApp");

			if (lc != null && !lc.getSubject().getPrincipals().isEmpty()) {
				user = (User) lc.getSubject().getPrincipals().iterator().next();
				sessionBean.setUser(user);
			}

			if (user == null) {
				return Response.ok(Status.UNAUTHORIZED).build();

			}

			// At last, user is authenticated
			return Response.ok().build();

		} catch (Exception e) {
			return Response.status(Response.Status.INTERNAL_SERVER_ERROR).build();
		}

	}
}