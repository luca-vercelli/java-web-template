/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.login.filters;

import java.io.IOException;
import java.util.Locale;

import javax.inject.Inject;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import com.example.myapp.login.actions.SessionManager;
import com.example.myapp.login.helpers.UsersManager;
import com.example.myapp.main.entity.User;
import com.example.myapp.main.util.AbstractRequestFilter;
import com.example.myapp.main.util.SessionBean;

/**
 * Login filter. Check if a User is already in session or not.
 * 
 * This filter must <b>not</b> apply to login-related pages. Unluckily,
 * WebFilter's do not allow to exclude paths. So we must implement an ad-hoc
 * (quite ugly) solution.
 * 
 * This is <b>not</b> meant to check if user is logged in or not. User <b>is</b>
 * logged in. We just need to populate sessionBean.
 *
 */
@WebFilter(value = "sessionSetupFilter", urlPatterns = { "*.html", "*.htm", "*.xhtml", "*.jsp" })
public class SessionSetupFilter extends AbstractRequestFilter {

	@Inject
	SessionBean sessionBean;
	@Inject
	UsersManager usersManager;
	@Inject
	SessionManager sessionManager;

	public static String COOKIE_NAME = "JLANG";
	
	@Override
	public boolean filterRequest(HttpServletRequest request, HttpServletResponse response) throws IOException {

		String lang = null;

		// guess language
		// cfr. I18nFilter.java
		Cookie[] cookies = request.getCookies();
		if (cookies != null)
			for (Cookie c : cookies)
				if (c.getName().equals(COOKIE_NAME)) {
					lang = c.getValue();
					break;
				}
		if (lang == null) {
			lang = request.getHeader(HttpHeaders.ACCEPT_LANGUAGE);
		}
		if (lang == null) {
			lang = Locale.getDefault().getCountry();
		}
		if (lang == null) {
			lang = "en"; // whatever you want. But please don't use server
							// default !
		}

		if (lang.indexOf(',') > 0)
			lang = lang.substring(0, lang.indexOf(','));
		if (lang.indexOf('"') > 0)
			lang = lang.replaceAll("\"", "");
		
		// set cookie
		Cookie cookie = new Cookie(COOKIE_NAME, lang);
		cookie.setHttpOnly(true);
		cookie.setPath("/"); // FIXME /myapp
		response.addCookie(cookie);
		
		String username = request.getRemoteUser();
		if (username != null) {
			User user = usersManager.getUserByUsername(username);
			if (user != null) {
				sessionManager.fillDataInSessionBean(sessionBean, user);
			}
		}
		System.out.println(sessionBean.toString());
		return true;
	}
}
