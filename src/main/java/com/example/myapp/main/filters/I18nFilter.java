/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.main.filters;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.inject.Inject;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.ws.rs.core.HttpHeaders;

import org.slf4j.Logger;

import com.example.myapp.main.util.AbstractResponseFilter;
import com.example.myapp.main.util.ResourceBundleMap;
import com.example.myapp.main.util.SessionBean;

/**
 * Simple filter that load translations from globals.properties into "labels"
 * request field.
 * 
 * In JSP's this is more elegant than JSTL's fmt tag. However, parameters are
 * not supported, so if you need parameters you must consider either fmt or some
 * kind of EL replacement.
 *
 */
@WebFilter(filterName = "i18nFilter", urlPatterns = { "*.html", "*.htm", "*.xhtml", "*.jsp" })
public class I18nFilter extends AbstractResponseFilter {

	static Map<String, Map<String, String>> langMap = new HashMap<String, Map<String, String>>();

	public static String COOKIE_NAME = "JLANG";
	public static String ATTR_REQ_NAME = "labels";

	@Inject
	SessionBean sessionBean;
	@Inject
	Logger LOG;

	private Map<String, String> getLabelsMap(String lang) {
		if (!langMap.containsKey(lang)) {

			// No fallback. Who cares about server's language?!
			ResourceBundle bundle = ResourceBundle.getBundle("global", Locale.forLanguageTag(lang),
					ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_PROPERTIES));

			Map<String, String> newMap = new ResourceBundleMap(bundle);
			langMap.put(lang, newMap);
		}
		return langMap.get(lang);
	}

	@Override
	public void filterResponse(HttpServletRequest request, HttpServletResponse response) throws Exception {

		String lang = null;

		// guess language
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
		sessionBean.setLanguage(lang);
		Cookie cookie = new Cookie(COOKIE_NAME, lang);
		cookie.setHttpOnly(true);
		cookie.setPath("/"); // FIXME /myapp
		response.addCookie(cookie); // FIXME

		// set labels for JSP
		request.setAttribute(ATTR_REQ_NAME, getLabelsMap(lang));

	}

}
