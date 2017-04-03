package com.example.myapp.login.helpers;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

import com.example.myapp.login.db.User;

/**
 * This LoginModule authenticates a given username/password against local
 * database using
 * <code>UsersHelper.getInstance().getUserByNameAndPassword()</code>
 *
 */
public class RdbmsLoginModule extends AbstractLoginModule {

	@Override
	public Principal getPrincipalByNameAndPassword(String username, String password) {
		User user = UsersHelper.getInstance().getUserByNameAndPassword(username, password);
		return user;
	}

	@Override
	public Set<?> getCredentialsForPricipal(Principal principal) {
		if (principal instanceof User)
			return ((User) principal).getRoles();
		else
			return Collections.emptySet();
	}

}
