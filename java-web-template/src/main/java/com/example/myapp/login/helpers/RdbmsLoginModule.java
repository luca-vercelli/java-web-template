package com.example.myapp.login.helpers;

import java.security.Principal;
import java.util.Collections;
import java.util.Set;

import javax.enterprise.inject.spi.Bean;
import javax.enterprise.inject.spi.BeanManager;
import javax.naming.InitialContext;
import javax.naming.NamingException;

import com.example.myapp.main.entity.User;

/**
 * This LoginModule authenticates a given username/password against local
 * database using
 * <code>UsersHelper.getInstance().getUserByNameAndPassword()</code>
 *
 */
public class RdbmsLoginModule extends AbstractLoginModule {

	// Cannot @Inject in JAAS LoginModules :(
	UsersManager usersHelper;

	public RdbmsLoginModule() throws NamingException {

		usersHelper = handMadeInject(UsersManager.class);
	}

	/**
	 * Inject an object in a context where you usually cannot.
	 * 
	 * @see https://developer.jboss.org/thread/196807
	 * @param clazz
	 * @return
	 * @throws NamingException
	 */
	@SuppressWarnings("unchecked")
	public <T> T handMadeInject(Class<T> clazz) throws NamingException {
		// You may want to move this method in some InjectUtils.class
		// However, that class cannot be injected anywhere...
		BeanManager beanManager = (BeanManager) new InitialContext().lookup("java:comp/BeanManager");
		Bean<T> bean = (Bean<T>) beanManager.getBeans(clazz).iterator().next();
		return (T) beanManager.getReference(bean, clazz, beanManager.createCreationalContext(bean));

	}

	@Override
	public Principal getPrincipalByNameAndPassword(String username, char[] password) {
		User user = usersHelper.getUserByNameAndPassword(username, password);
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
