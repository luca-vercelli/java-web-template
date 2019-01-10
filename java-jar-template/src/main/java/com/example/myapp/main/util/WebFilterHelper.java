package com.example.myapp.main.util;

import javax.ejb.Stateless;
import javax.servlet.http.HttpServletRequest;

/**
 * So far as JavaEE7, @WebFilter does not allow to exclude specific pages.
 * 
 * @author Luca Vercelli
 *
 */
@Stateless
public class WebFilterHelper {

	/**
	 * Return true if the given url is to be excluded by WebFilter. This is
	 * because @WebFilter annotation does not allow excluding specific paths.
	 * 
	 * @param url
	 * @param excludeUrls
	 * @param request
	 * @return
	 */
	public boolean excludeUrl(String[] excludeUrls, HttpServletRequest request) {
		String uri = request.getRequestURI();
		String contextPath = request.getContextPath();

		uri = uri.replaceAll("/+", "/"); // convert /myapp///ui -> /myapp/ui

		for (String excludeUrl : excludeUrls) {
			excludeUrl = excludeUrl.trim();
			if (!excludeUrl.equals("") && uri.startsWith(contextPath + excludeUrl)) {
				return true;
			}
		}
		return false;
	}
}
