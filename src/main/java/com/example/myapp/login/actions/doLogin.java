package com.example.myapp.login.actions;

import java.io.IOException;

import javax.inject.Inject;
import javax.security.auth.login.LoginContext;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.example.myapp.login.entity.User;
import com.example.myapp.login.helpers.UsersManager;
import com.example.myapp.main.util.ApplicationProperties;
import com.example.myapp.main.util.SessionBean;

/**
 * Perform authentication.
 * 
 * @author Luca Vercelli
 *
 */
@WebServlet(name = "loginServlet", urlPatterns = { "/ui/doLogin" })
public class doLogin extends HttpServlet {

	private static final long serialVersionUID = 651051473002232658L;

	@Inject
	ApplicationProperties appProps;
	@Inject
	SessionBean sessionBean;
	@Inject
	SessionManager sessionManager;
	@Inject
	UsersManager usersManager;
	@Inject
	Logger LOG;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {
			sessionBean.setLoginContext(null);
			sessionBean.setUser(null);

			String userId = request.getParameter("userId");
			char[] pwd = request.getParameter("pwd").toCharArray();

			User user = null;

			if (userId == null || userId.equals("")) {
				// This can also happen when user go to "Login" address for the
				// first time

				// addActionError(getText("login.missing.parameters"));
				response.sendRedirect(request.getContextPath() + appProps.getProperty("login.uri"));
				return;
			}

			LoginContext lc = usersManager.authenticate(userId, pwd);

			sessionBean.setLoginContext(lc);
			if (lc != null && !lc.getSubject().getPrincipals().isEmpty()) {
				user = (User) lc.getSubject().getPrincipals().iterator().next();

				sessionManager.fillDataInSessionBean(user, request.getParameter("language"));
			}

			if (user == null) {
				// FIXME Should send back some message? login failed
				response.sendRedirect(request.getContextPath() + appProps.getProperty("login.uri"));
				return;
			}

			// At last, user is authenticated
			LOG.info("User authenticated:" + user);
			response.sendRedirect(request.getContextPath());

		} catch (Exception e) {
			LOG.error("Exception while performing login", e);

			// FIXME Should send back some message? internal error
			response.sendRedirect(request.getContextPath() + appProps.getProperty("login.uri"));
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}