/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.i18n;

import java.util.Locale;

import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import com.example.myapp.main.util.AbstractResponseFilter;

/**
 * Set up default language, if none set.
 *
 */
@WebFilter(filterName = "i18nFilter", urlPatterns = { "*.html", "*.htm", "*.xhtml", "*.jsp" })
public class I18nFilter extends AbstractResponseFilter {

	public static String COOKIE_NAME = "JLANG";

	@Override
	public void filterResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String lang = null;

		// guess language
		// cfr. SessionSetupFilter.java
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

	}

}
