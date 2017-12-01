/*
* WebTemplate 1.0
* Luca Vercelli 2017
*   
*/
package com.example.myapp.login.eesecurity;

import java.io.IOException;
import java.util.Base64;
import java.util.Map;

import javax.security.auth.Subject;
import javax.security.auth.callback.Callback;
import javax.security.auth.callback.CallbackHandler;
import javax.security.auth.callback.UnsupportedCallbackException;
import javax.security.auth.message.AuthException;
import javax.security.auth.message.AuthStatus;
import javax.security.auth.message.MessageInfo;
import javax.security.auth.message.MessagePolicy;
import javax.security.auth.message.callback.CallerPrincipalCallback;
import javax.security.auth.message.callback.GroupPrincipalCallback;
import javax.security.auth.message.module.ServerAuthModule;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import static javax.security.auth.message.AuthStatus.*;

/**
 * Java EE Security login module, for JASPIC (i.e. custom) login. This is the actual
 * Server Authentication Module AKA SAM. Currently not used.
 * 
 * @see https://dzone.com/refcardz/getting-started-java-ee,
 *      http://arjan-tijms.omnifaces.org/2012/11/implementing-container-authentication.html,
 *      https://github.com/javaee/security-examples
 *      https://blogs.oracle.com/enterprisetechtips/adding-authentication-mechanisms-to-the-glassfish-servlet-container
 *
 */
public class MyServerAuthModule implements ServerAuthModule {

	private CallbackHandler handler;

	private Class<?>[] supportedMessageTypes = new Class[] { HttpServletRequest.class, HttpServletResponse.class };

	/**
	 * Called by container at startup
	 */
	@Override
	public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler,
			@SuppressWarnings("rawtypes") Map options) throws AuthException {

		this.handler = handler;
	}

	/**
	 * Called by container before each request? Should return SUCCESS or
	 * FAILURE.
	 */
	@Override
	public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject)
			throws AuthException {

		final HttpServletRequest request = (HttpServletRequest) messageInfo.getRequestMessage();

		// final String[] credentials = getBasicAuthCredentials(request);
		final String[] credentials = getFormCredentials(request);

		final String username = credentials[0];
		final String password = credentials[1];

		// Should we really check auth here? or handler.handle() does that?
		if (!"snoopy".equals(username) || !"woodst0ck".equals(password)) {
			return FAILURE;
		}

		CallerPrincipalCallback callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, username);

		GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(clientSubject,
				new String[] { "users" });
		try {

			// Communicate the details of the authenticated user to the
			// container. In many cases the handler will just store the details
			// and the container will actually handle the login after we return
			// from this method.
			handler.handle(new Callback[] { callerPrincipalCallback, groupPrincipalCallback });

		} catch (IOException | UnsupportedCallbackException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	/**
	 * A compliant implementation should return HttpServletRequest and
	 * HttpServletResponse, so the delegation class {@link ServerAuthContext}
	 * can choose the right SAM to delegate to. In this example there is only
	 * one SAM and thus the return value actually doesn't matter here.
	 */
	@Override
	public Class<?>[] getSupportedMessageTypes() {
		return supportedMessageTypes;
	}

	/**
	 * Get credentials from HTTP Basic Authentication. I don't like that.
	 * 
	 * @return username and password
	 */
	protected String[] getBasicAuthCredentials(HttpServletRequest request) {
		final String header = request.getHeader("Authorization");
		final byte[] decoded = Base64.getDecoder().decode(header.replace("Basic ", ""));
		return new String(decoded).split(":");
	}

	/**
	 * Get credentials from a custom login form.
	 * 
	 * @return username and password
	 */
	protected String[] getFormCredentials(HttpServletRequest request) {
		String username = request.getParameter("username");
		String password = request.getParameter("password");
		return new String[] { username, password };
	}

	/**
	 * This method will be called after the last Filter or Servlet in the
	 * request has been invoked. WebLogic 12c calls this before Servlet is
	 * called, Geronimo v3 after, JBoss EAP 6 and GlassFish 3.1.2.2 don't call
	 * this at all. WebLogic (seemingly) only continues if SEND_SUCCESS is
	 * returned, Geronimo completely ignores return value.
	 */
	@Override
	public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
		return SEND_SUCCESS;
	}

	/**
	 * This method will be called when HttpServletRequest#logout is explicitly
	 * called
	 */
	@Override
	public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
		if (subject != null) {
			subject.getPrincipals().clear();
		}
	}
}