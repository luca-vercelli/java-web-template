/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.main.filters;

import java.io.IOException;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Simple filter that load translations from globals.properties into "labels"
 * request field.
 * 
 * In JSP's this is more elegant than JSTL's fmt tag. However, parameters are
 * not supported, so if you need parameters you must consider either fmt or some
 * kind of EL replacement.
 *
 */
@WebFilter(urlPatterns = { "*.html", "*.htm", "*.xhtml", "*.jsp" })
public class I18nFilter implements Filter {

	static Map<String, Map<String, String>> properties = new HashMap<String, Map<String, String>>();

	private Map<String, String> loadBundle(String lang) {
		if (!properties.containsKey(lang)) {
			Map<String, String> newMap = new HashMap<String, String>();
			ResourceBundle bundle = ResourceBundle.getBundle("global", Locale.forLanguageTag(lang));
			for (String key : bundle.keySet()) {
				newMap.put(key, bundle.getString(key));
			}
			properties.put(lang, newMap);
		}
		return properties.get(lang);
	}

	@Override
	public void doFilter(ServletRequest req, ServletResponse resp, FilterChain chain)
			throws IOException, ServletException {

		if (req instanceof HttpServletRequest && resp instanceof HttpServletResponse) {

			String lang = "it"; // FIXME

			req.setAttribute("labels", loadBundle(lang));

			chain.doFilter(req, resp); // Just continue chain

		} else {
			// should not pass here
			System.err.println("Not HTTP ? Why here?");
			chain.doFilter(req, resp); // Just continue chain
		}
	}

	@Override
	public void init(FilterConfig fc) throws ServletException {
	}

	@Override
	public void destroy() {
	}

}
