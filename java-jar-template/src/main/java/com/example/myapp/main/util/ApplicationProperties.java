/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under MIT license 
*/
package com.example.myapp.main.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.enterprise.context.ApplicationScoped;
import javax.inject.Inject;
import org.slf4j.Logger;

/**
 * Load properties (if any) from file application.properties. The file is read
 * at first call to getInstance().
 * 
 * You may prefer store properties inside Settings.
 *
 */
@ApplicationScoped
public class ApplicationProperties {

	@Inject
	Logger LOG;

	private Properties internal = new Properties();

	public ApplicationProperties() {

		InputStream input = null;

		try {
			input = ApplicationProperties.class.getResourceAsStream("/application.properties");
			internal.load(input);

		} catch (IOException ex) {
			LOG.error("I/O Exception while loading application.properties", ex);

		} catch (NullPointerException ex) {
			LOG.error("NullPointerException while loading application.properties (missing file?)", ex);

		} finally {
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					LOG.error("Exception while loading application.properties", e);
				}
			}
		}
	}

	public String getProperty(String name) {
		return internal.getProperty(name);
	}

	public String put(String name, String value) {
		return (String) internal.put(name, value);
	}
}
