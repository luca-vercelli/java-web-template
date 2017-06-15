package com.example.myapp.login.helpers;

import javax.security.auth.spi.LoginModule;

import javax.security.auth.login.LoginException;

import java.util.Map;
import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.security.auth.Subject;
import javax.security.auth.callback.CallbackHandler;

/**
 * Cannot @Inject in a common LoginModule because JAAS build objects with a
 * brutal "new".
 * 
 * @author Daniel Straub
 * @see https://developer.jboss.org/thread/196807
 *
 */
public class CdiDelegatingLoginModule implements LoginModule {

	private LoginModule delegate;

	@Override
	public void initialize(Subject subject, CallbackHandler callbackHandler, Map<String, ?> sharedState,
			Map<String, ?> options) {

		String lmClassName = (String) options.get("code");
		delegate = createCdiInstance(lmClassName);
		delegate.initialize(subject, callbackHandler, sharedState, options);
	}

	private LoginModule createCdiInstance(String className) {
		try {
			Class<?> loginModuleClass = Thread.currentThread().getContextClassLoader().loadClass(className);
			BeanManager beanManager = (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
			Bean<LoginModule> loginModuleBean = (Bean<LoginModule>) beanManager.getBeans(loginModuleClass).iterator()
					.next();
			return (LoginModule) beanManager.getReference(loginModuleBean, loginModuleClass,
					beanManager.createCreationalContext(loginModuleBean));
		} catch (Exception x) {
			throw new IllegalStateException(x);
		}
	}

	@Override
	public boolean login() throws LoginException {
		return delegate.login();
	}

	@Override
	public boolean commit() throws LoginException {
		return delegate.commit();
	}

	@Override
	public boolean abort() throws LoginException {
		return delegate.abort();
	}

	@Override
	public boolean logout() throws LoginException {
		return delegate.logout();
	}
}