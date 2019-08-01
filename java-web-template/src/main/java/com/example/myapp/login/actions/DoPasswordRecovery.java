package com.example.myapp.login.actions;

import java.io.IOException;

import javax.inject.Inject;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;

import com.example.myapp.login.helpers.UsersManager;
import com.example.myapp.main.entity.User;
import com.example.myapp.main.util.ApplicationProperties;
import com.example.myapp.main.util.MailManager;
import com.example.myapp.main.util.SimpleMessage;
import com.example.myapp.main.util.SessionBean;

/**
 * Send an email to user to confirm password reset.
 * 
 * @author Luca Vercelli
 *
 */
@WebServlet("/ui/doPasswordRecovery")
public class DoPasswordRecovery extends HttpServlet {

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

	public final static String ERROR_MESSAGE = "error_message";

	@Override
	public void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {

		HttpSession session = request.getSession();
		session.removeAttribute(ERROR_MESSAGE);
		try {

			String address = request.getParameter("address"); // request's data
																// not reliable

			String email = request.getParameter("email");
			if (email.isEmpty()) {
				session.setAttribute(ERROR_MESSAGE, "err.missing.email"); // FIXME
																			// won't
																			// be
																			// seen
				response.sendRedirect(request.getContextPath() + appProps.getProperty("password.recovery.uri"));
				return;
				// response.sendRedirect(request.getContextPath());
			}

			User user = usersManager.getUserByEmail(email);
			if (user == null) {
				session.setAttribute(ERROR_MESSAGE, "err.unknown.email");// FIXME
																			// won't
																			// be
																			// seen
				response.sendRedirect(request.getContextPath() + appProps.getProperty("password.recovery.uri"));
				return;
				// response.sendRedirect(request.getContextPath());
			}

			String pwdRecCode = usersManager.createPasswordRecoveryCode(user);

			// TODO some more clever text
			SimpleMessage msg = mailManager.prepareEmail(email, "Password reset")
				.setText("You have requested to reset " + address + " login password.\r\n" +
					"If you really want to proceed, follow this link:\r\n" + address +
					"/ui/confirmPasswordRecovery?code=" + pwdRecCode + "\r\n")
				.send();

			session.setAttribute(ERROR_MESSAGE, "email.recovery.sent");
			response.sendRedirect(request.getContextPath() + appProps.getProperty("password.recovery.uri"));

		} catch (Exception e) {
			LOG.error("Exception while sending email", e);

			// FIXME Should send back some message? internal error
			session.setAttribute(ERROR_MESSAGE, "err.cannot.send.email");
			response.sendRedirect(request.getContextPath() + appProps.getProperty("login.uri"));
		}

	}

	@Override
	protected void doPost(HttpServletRequest request, HttpServletResponse response)
			throws ServletException, IOException {
		doGet(request, response);
	}
}
