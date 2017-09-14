/*
* WebTemplate 1.0
* Luca Vercelli 2017
*   
*/
package com.example.myapp.login.helpers;

import java.io.IOException;
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
 * Java EE Security login module.
 * 
 * @see https://dzone.com/refcardz/getting-started-java-ee
 *
 */
public class EESecurityAuthModule implements ServerAuthModule {
	private CallbackHandler handler;
	private Class<?>[] supportedMessageTypes = new Class[] { HttpServletRequest.class, HttpServletResponse.class };

	@Override
	public void initialize(MessagePolicy requestPolicy, MessagePolicy responsePolicy, CallbackHandler handler,
			@SuppressWarnings("rawtypes") Map options) throws AuthException {
		this.handler = handler;
	}

	@Override
	public AuthStatus validateRequest(MessageInfo messageInfo, Subject clientSubject, Subject serviceSubject)
			throws AuthException {
		CallerPrincipalCallback callerPrincipalCallback = new CallerPrincipalCallback(clientSubject, "Bob");
		GroupPrincipalCallback groupPrincipalCallback = new GroupPrincipalCallback(clientSubject,
				new String[] { "users" });
		try {
			handler.handle(new Callback[] { callerPrincipalCallback, groupPrincipalCallback });
		} catch (IOException | UnsupportedCallbackException e) {
			e.printStackTrace();
		}
		return SUCCESS;
	}

	@Override
	public Class<?>[] getSupportedMessageTypes() {
		return supportedMessageTypes;
	}

	@Override
	public AuthStatus secureResponse(MessageInfo messageInfo, Subject serviceSubject) throws AuthException {
		return SEND_SUCCESS;
	}

	@Override
	public void cleanSubject(MessageInfo messageInfo, Subject subject) throws AuthException {
	}
}