/*
* WebTemplate 1.0
* Luca Vercelli 2017

* Code taken from:
* 
* (1) SampleLoginModule - Copyright (c) 2000, 2002, Oracle and/or its affiliate
* 
* (2) John Musser and Paul Feuer, http://www.javaworld.com/article/2074873/java-web-development
* (copyright terms?)
*   
*/
package com.example.myapp.login.helpers;

import javax.security.auth.spi.LoginModule;

import com.example.myapp.authorization.db.Role;
import com.example.myapp.login.db.User;

import javax.security.auth.login.LoginException;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.Vector;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

/**
 * <p>
 * RdbmsLoginModule is a LoginModule that authenticates a given
 * username/password credential against a JDBC datasource.
 *
 * <p>
 * This <code>LoginModule</code> interoperates with any conformant JDBC
 * datasource. To direct this <code>LoginModule</code> to use a specific JNDI
 * datasource, two options must be specified in the login
 * <code>Configuration</code> for this <code>LoginModule</code>.
 * 
 * <pre>
 *    url=<b>jdbc_url</b>
 *    driverb>jdbc driver class</b>
 * </pre>
 *
 * <p>
 * For the purposed of this example, the format in which the user's information
 * must be stored in the database is in a table named "USER_AUTH" with the
 * following columns
 * 
 * <pre>
 *     USERID
 *     PASSWORD
 *     FIRST_NAME
 *     LAST_NAME
 *     DELETE_PERM
 *     UPDATE_PERM
 * </pre>
 *
 * @see javax.security.auth.spi.LoginModule
 * @author Paul Feuer and John Musser
 * @version 1.0
 */

public class RdbmsLoginModule implements LoginModule {

	// initial state
	CallbackHandler callbackHandler;
	Subject subject;
	Map<String, ?> sharedState;

	// temporary state
	Vector<Role> tempCredentials; // JASS uses Object as credentials, we use
									// Role
	Vector<Principal> tempPrincipals; // i.e. User

	// the authentication status
	boolean success;

	// configurable options
	boolean debug;

	/**
	 * <p>
	 * Creates a login module that can authenticate against a JDBC datasource.
	 */
	public RdbmsLoginModule() {
		tempCredentials = new Vector<Role>();
		tempPrincipals = new Vector<Principal>();
		success = false;
		debug = false;
	}

	/**
	 * Initialize this <code>LoginModule</code>.
	 *
	 * <p>
	 *
	 * @param subject
	 *            the <code>Subject</code> to be authenticated.
	 *            <p>
	 *
	 * @param callbackHandler
	 *            a <code>CallbackHandler</code> for communicating with the end
	 *            user (prompting for usernames and passwords, for example).
	 *            <p>
	 *
	 * @param sharedState
	 *            shared <code>LoginModule</code> state.
	 *            <p>
	 *
	 * @param options
	 *            options specified in the login <code>Configuration</code> for
	 *            this particular <code>LoginModule</code>.
	 */
	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {

		// save the initial state
		this.callbackHandler = callbackHandler;
		this.subject = subject;
		this.sharedState = sharedState;

		// initialize any configured options
		debug = "true".equalsIgnoreCase((String) options.get("debug"));

		if (debug) {
			System.out.println("\t\t[RdbmsLoginModule] initialize()");
		}
	}

	/**
	 * <p>
	 * Verify the password against the relevant JDBC datasource.
	 *
	 * @return true always, since this <code>LoginModule</code> should not be
	 *         ignored.
	 *
	 * @exception FailedLoginException
	 *                if the authentication fails.
	 *                <p>
	 *
	 * @exception LoginException
	 *                if this <code>LoginModule</code> is unable to perform the
	 *                authentication.
	 */
	public boolean login() throws LoginException {

		if (debug)
			System.out.println("\t\t[RdbmsLoginModule] login()");

		if (callbackHandler == null)
			throw new LoginException(
					"Error: no CallbackHandler available " + "to garner authentication information from the user");

		try {
			// Setup default callback handlers.
			NameCallback nameCallback = new NameCallback("Username: ");
			PasswordCallback passwordCallback = new PasswordCallback("Password: ", false);
			Callback[] callbacks = new Callback[] { nameCallback, passwordCallback };

			callbackHandler.handle(callbacks);

			String username = nameCallback.getName();
			String password = new String(passwordCallback.getPassword());

			passwordCallback.clearPassword();

			// This is the only point that can really change when you change
			// login module
			User principal = UsersHelper.getInstance().getUserByUsernameAndPassword(username, password);

			if (principal != null) {
				success = true;
				tempPrincipals.add(principal);
				tempCredentials.addAll(principal.getRoles());
			} else {
				success = false;
			}

			callbacks[0] = null;
			callbacks[1] = null;

			if (!success)
				throw new LoginException();

			return (true);

		} catch (Exception ex) {
			success = false;
			throw new RuntimeException("Unexpected exception during login", ex);
		}
	}

	/**
	 * Abstract method to commit the authentication process (phase 2).
	 *
	 * <p>
	 * This method is called if the LoginContext's overall authentication
	 * succeeded (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules succeeded).
	 *
	 * <p>
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> method),
	 * then this method associates a <code>RdbmsPrincipal</code> with the
	 * <code>Subject</code> located in the <code>LoginModule</code>. If this
	 * LoginModule's own authentication attempted failed, then this method
	 * removes any state that was originally saved.
	 *
	 * <p>
	 *
	 * @exception LoginException
	 *                if the commit fails
	 *
	 * @return true if this LoginModule's own login and commit attempts
	 *         succeeded, or false otherwise.
	 */
	public boolean commit() throws LoginException {

		if (debug)
			System.out.println("\t\t[RdbmsLoginModule] commit");

		if (success) {

			if (subject.isReadOnly()) {
				throw new LoginException("Subject is Readonly");
			}

			try {

				if (debug) {
					for (Principal p : tempPrincipals)
						System.out.println("\t\t[RdbmsLoginModule] Principal: " + p.toString());
				}

				subject.getPrincipals().addAll(tempPrincipals);
				subject.getPublicCredentials().addAll(tempCredentials);

				tempPrincipals.clear();
				tempCredentials.clear();

				if (callbackHandler instanceof PassiveCallbackHandler)
					((PassiveCallbackHandler) callbackHandler).clearPassword();

				return (true);
			} catch (Exception ex) {
				ex.printStackTrace(System.out);
				throw new LoginException(ex.getMessage());
			}
		} else {
			tempPrincipals.clear();
			tempCredentials.clear();
			return (true);
		}
	}

	/**
	 * <p>
	 * This method is called if the LoginContext's overall authentication
	 * failed. (the relevant REQUIRED, REQUISITE, SUFFICIENT and OPTIONAL
	 * LoginModules did not succeed).
	 *
	 * <p>
	 * If this LoginModule's own authentication attempt succeeded (checked by
	 * retrieving the private state saved by the <code>login</code> and
	 * <code>commit</code> methods), then this method cleans up any state that
	 * was originally saved.
	 *
	 * <p>
	 *
	 * @exception LoginException
	 *                if the abort fails.
	 *
	 * @return false if this LoginModule's own login and/or commit attempts
	 *         failed, and true otherwise.
	 */
	public boolean abort() throws javax.security.auth.login.LoginException {

		if (debug)
			System.out.println("\t\t[RdbmsLoginModule] abort");

		// Clean out state
		success = false;

		tempPrincipals.clear();
		tempCredentials.clear();

		if (callbackHandler instanceof PassiveCallbackHandler)
			((PassiveCallbackHandler) callbackHandler).clearPassword();

		logout();

		return (true);
	}

	/**
	 * Logout a user.
	 *
	 * <p>
	 * This method removes the Principals that were added by the
	 * <code>commit</code> method.
	 *
	 * <p>
	 *
	 * @exception LoginException
	 *                if the logout fails.
	 *
	 * @return true in all cases since this <code>LoginModule</code> should not
	 *         be ignored.
	 */
	public boolean logout() throws javax.security.auth.login.LoginException {

		if (debug)
			System.out.println("\t\t[RdbmsLoginModule] logout");

		tempPrincipals.clear();
		tempCredentials.clear();

		if (callbackHandler instanceof PassiveCallbackHandler)
			((PassiveCallbackHandler) callbackHandler).clearPassword();

		// remove the principals the login module added
		for (Principal p : Collections.unmodifiableCollection(subject.getPrincipals())) {
			if (debug)
				System.out.println("\t\t[RdbmsLoginModule] removing principal " + p.toString());
			subject.getPrincipals().remove(p);
		}

		// remove the credentials the login module added
		for (Object c : Collections.unmodifiableCollection(subject.getPublicCredentials())) {
			if (debug)
				System.out.println("\t\t[RdbmsLoginModule] removing credential " + c.toString());
			subject.getPublicCredentials().remove(c);
		}

		return (true);
	}

}
