package com.example.myapp.login.actions;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

/**
 * If you use FORM or BASIC login, you don't need this. This is required to
 * manually handle login.
 * 
 * @author Luca Vercelli
 */
@WebServlet("/ui/doLogin")
public class DoLoginServlet extends HttpServlet {

	private static final long serialVersionUID = 5991070092686568306L;

	@Inject
	Logger LOG;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		// TODO interact with SessionBean

		if (request.getRemoteUser() != null) {
			request.logout();
		}

		String username = request.getParameter("j_username");
		String password = request.getParameter("j_password");

		try {
			request.login(username, password);
		} catch (SecurityException exc) {
			response.sendRedirect(request.getContextPath() + "/ui/login.jsp?error=1");
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
