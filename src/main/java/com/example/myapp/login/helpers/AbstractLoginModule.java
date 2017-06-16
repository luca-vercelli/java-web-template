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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.security.auth.login.LoginException;

import java.security.Principal;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.Vector;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.NameCallback;
import javax.security.auth.callback.PasswordCallback;

/**
 * Abstract LoginModule that authenticates a given username/password credential
 * against "something".
 *
 * @see javax.security.auth.spi.LoginModule
 * @author Paul Feuer and John Musser
 * @version 1.0
 */

public abstract class AbstractLoginModule implements LoginModule {

	// Cannot @Inject
	public final static Logger LOG = LoggerFactory.getLogger(AbstractLoginModule.class);

	// initial state
	CallbackHandler callbackHandler;
	Subject subject;
	Map<String, ?> sharedState;

	// temporary state
	Vector<Object> tempCredentials; // i.e. Role
	Vector<Principal> tempPrincipals; // i.e. User

	// the authentication status
	boolean success;

	// configurable options (currently, none)

	/**
	 * <p>
	 * Creates a login module that can authenticate against a JDBC datasource.
	 */
	public AbstractLoginModule() {
		tempCredentials = new Vector<Object>();
		tempPrincipals = new Vector<Principal>();
		success = false;
	}

	/**
	 * Retrieve user, if any.
	 * 
	 * @return the Principal with given credentials, or null.
	 */
	public abstract Principal getPrincipalByNameAndPassword(String username, char[] password);

	/**
	 * Retrieve credentials for given principal, if any.
	 * 
	 * @return the list of credentials, possibly empty.
	 */
	public abstract Set<?> getCredentialsForPricipal(Principal principal);

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
		// currently, none

		LOG.debug("\t\t[RdbmsLoginModule] initialize()");

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

		LOG.debug("\t\t[RdbmsLoginModule] login()");

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
			char[] password = passwordCallback.getPassword();

			passwordCallback.clearPassword();

			Principal principal = getPrincipalByNameAndPassword(username, password);

			if (principal != null) {
				success = true;
				tempPrincipals.add(principal);
				tempCredentials.addAll(getCredentialsForPricipal(principal));
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

		LOG.debug("\t\t[RdbmsLoginModule] commit");

		if (success) {

			if (subject.isReadOnly()) {
				throw new LoginException("Subject is Readonly");
			}

			try {

				for (Principal p : tempPrincipals)
					LOG.debug("\t\t[RdbmsLoginModule] Principal: " + p.toString());

				subject.getPrincipals().addAll(tempPrincipals);
				subject.getPublicCredentials().addAll(tempCredentials);

				tempPrincipals.clear();
				tempCredentials.clear();

				if (callbackHandler instanceof PassiveCallbackHandler)
					((PassiveCallbackHandler) callbackHandler).clearPassword();

				return (true);
			} catch (Exception ex) {
				LOG.error("Exception during commit()", ex);
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
	public boolean abort() throws LoginException {

		LOG.debug("\t\t[RdbmsLoginModule] abort");

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

		LOG.debug("\t\t[RdbmsLoginModule] logout");

		tempPrincipals.clear();
		tempCredentials.clear();

		if (callbackHandler instanceof PassiveCallbackHandler)
			((PassiveCallbackHandler) callbackHandler).clearPassword();

		// remove the principals the login module added
		for (Principal p : Collections.unmodifiableCollection(subject.getPrincipals())) {
			LOG.debug("\t\t[RdbmsLoginModule] removing principal " + p.toString());
			subject.getPrincipals().remove(p);
		}

		// remove the credentials the login module added
		for (Object c : Collections.unmodifiableCollection(subject.getPublicCredentials())) {
			LOG.debug("\t\t[RdbmsLoginModule] removing credential " + c.toString());
			subject.getPublicCredentials().remove(c);
		}

		return (true);
	}

}
