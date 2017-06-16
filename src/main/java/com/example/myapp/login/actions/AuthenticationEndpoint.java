/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
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

import com.example.myapp.login.entity.User;
import com.example.myapp.login.helpers.UsersManager;
import com.example.myapp.main.util.SessionBean;
import com.sun.messaging.jmq.io.Status;

/**
 * Perform authentication.
 * 
 */
@Path("/doLogin")
public class AuthenticationEndpoint {

	@Inject
	SessionBean sessionBean;
	@Inject
	UsersManager usersHelper;
	
	@POST
	@Produces(MediaType.APPLICATION_JSON)
	@Consumes(MediaType.APPLICATION_FORM_URLENCODED)
	public Response authenticateUser(@FormParam("userId") String userId, @FormParam("pwd") String pwd) {

		try {
			sessionBean.setLoginContext(null);
			sessionBean.setUser(null);

			User user = null;

			if (userId == null || userId.equals("")) {
				// This can also happen when user go to "Login" address for the
				// first time

				// addActionError(getText("login.missing.parameters"));
				return Response.ok(Status.BAD_REQUEST).build();
			}

			LoginContext lc = usersHelper.authenticate(userId, pwd.toCharArray());

			sessionBean.setLoginContext(lc);
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