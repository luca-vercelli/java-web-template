/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
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

import com.example.myapp.main.util.SessionBean;

/**
 * Clear session, so the user is logged out.
 * 
 * @author Luca Vercelli
 *
 */
@WebServlet("/ui/doLogout")
public class DoLogoutServlet extends HttpServlet {

	private static final long serialVersionUID = 651051473002562658L;

	@Inject
	SessionBean sessionBean;
	@Inject
	SessionManager sessionManager;
	@Inject
	Logger LOG;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		sessionManager.clearSession(sessionBean);

		request.logout();

		response.sendRedirect(request.getContextPath());

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
