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
@WebServlet("/ui/doPasswordRecovery")
public class doPasswordRecovery extends HttpServlet {

	private static final long serialVersionUID = -1853319438734405659L;

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

			String address = request.getParameter("address"); // request's data
																// not reliable

			String email = request.getParameter("email");
			if (email.isEmpty()) {
				request.setAttribute("error_message", "err.missing.email"); //FIXME won't be seen
				response.sendRedirect(request.getContextPath() + appProps.getProperty("password.recovery.uri"));
				return;
				// response.sendRedirect(request.getContextPath());
			}

			User user = usersManager.getUserByEmail(email);
			if (user == null) {
				request.setAttribute("error_message", "err.unknown.email");//FIXME won't be seen
				response.sendRedirect(request.getContextPath() + appProps.getProperty("password.recovery.uri"));
				return;
				// response.sendRedirect(request.getContextPath());
			}

			String pwdRecCode = usersManager.createPasswordRecoveryCode(user);

			// TODO some more clever text
			mailManager.sendSimpleTextMail(email, "Password reset",
					"You have requested to reset " + address + " login password.\r\n"
							+ "If you really want to proceed, follow this link:\r\n" + address
							+ "/ui/confirmPasswordRecovery?code=" + pwdRecCode + "\r\n");

			request.setAttribute("error.message", "email.recovery.sent");
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
