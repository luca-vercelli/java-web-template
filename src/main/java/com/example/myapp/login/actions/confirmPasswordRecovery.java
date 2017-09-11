package com.example.myapp.login.actions;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;

import com.example.myapp.login.helpers.UsersManager;
import com.example.myapp.main.entity.User;
import com.example.myapp.main.util.ApplicationProperties;
import com.example.myapp.main.util.MailManager;
import com.example.myapp.main.util.SessionBean;

/**
 * Send an email to user to confirm password reset.
 * 
 * @author Luca Vercelli
 *
 */
@WebServlet("/ui/confirmPasswordRecovery")
public class confirmPasswordRecovery extends HttpServlet {

	private static final long serialVersionUID = -6310872623683319986L;

	@Inject
	ApplicationProperties appProps;
	@Inject
	SessionBean sessionBean;
	@Inject
	UsersManager usersManager;
	@Inject
	Logger LOG;
	@Inject
	MailManager mailManager;

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		try {

			String pwdRecCode = request.getParameter("code");

			User user = usersManager.getUserByPasswordRecoveryCode(pwdRecCode);
			if (user == null) {
				request.setAttribute("error.message", "err.wrong.code");
				response.sendRedirect(request.getContextPath() + appProps.getProperty("password.recovery.uri"));
				return;
				// response.sendRedirect(request.getContextPath());
			}

			String newPassword = usersManager.newPassword(user);

			LOG.info("User changed password:" + user);
			request.setAttribute("newPassword", newPassword);
			response.sendRedirect(request.getContextPath() + appProps.getProperty("password.recovery.uri"));

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
