/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.login.actions;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.example.myapp.main.util.ApplicationProperties;
import com.example.myapp.main.util.SessionBean;

/**
 * Clear session, so the user is logged out.
 * 
 * @author Luca Vercelli
 *
 */
@WebServlet("/ui/doLogout")
public class doLogout extends HttpServlet {

	private static final long serialVersionUID = 651051473002562658L;

	@Inject
	ApplicationProperties appProps;
	@Inject
	SessionBean sessionBean;
	@Inject
	SessionManager sessionManager;
	@Inject
	Logger LOG;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		/* FIXME logincontext is not serializable, should we save it in session? how to get it?
		if (sessionBean != null && sessionBean.getLoginContext() != null) {
			try {
				sessionBean.getLoginContext().logout();
			} catch (LoginException e) {
				LOG.error("Exception while logging out", e);
			}
		}
		*/

		sessionManager.clearSession(sessionBean);

		response.sendRedirect(request.getContextPath() + appProps.getProperty("login.uri"));

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
