/*
* WebTemplate 1.0
* Luca Vercelli 2017
* Released under GPLv3 
*/
package com.example.myapp.main.util;

import java.io.File;
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
 */
@ApplicationScoped
public class ApplicationProperties {

	@Inject
	Logger LOG;

	private File root;
	private Properties internal = new Properties();

	public ApplicationProperties() {

		InputStream input = null;

		try {
			input = ApplicationProperties.class.getResourceAsStream("/application.properties");
			internal.load(input);

		} catch (IOException ex) {
			LOG.error("Exception while loading application.properties", ex);

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

	/**
	 * This File should point to WEB-INF/classes folder. Current implementation
	 * relies of fact that "application.properties" exists and it is placed
	 * non-compressed in WEB-INF/classes folder.
	 * 
	 * @return
	 */
	public File getAppRoot() {
		if (root == null) {
			root = new File(ApplicationProperties.class.getResource("/application.properties").getFile())
					.getParentFile();
		}
		return root;

	}
}
