package com.example.myapp.main.util;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.apache.log4j.Logger;

/**
 * Load properties (if any) from file application.properties. The file is read
 * at first call to getInstance().
 *
 */
public class ApplicationProperties extends Properties {

	private static final long serialVersionUID = -3536285866839291581L;

	private static ApplicationProperties instance;

	protected static final Logger LOG = Logger.getLogger(ApplicationProperties.class);

	private File root;

	public static ApplicationProperties getInstance() {
		if (instance == null)
			instance = load();
		return instance;
	}

	private static ApplicationProperties load() {

		InputStream input = null;
		ApplicationProperties props = new ApplicationProperties();

		try {
			input = ApplicationProperties.class.getResourceAsStream("/application.properties");

			props.load(input);

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

		return props;
	}

	/**
	 * This File should point to WEB-INF/classes folder. Current implementation
	 * relies of fact that "application.properties" exists and it is placed
	 * non-compressed in WEB-INF/classes folder.
	 * 
	 * @return
	 */
	public File getRoot() {
		if (root == null) {
			root = new File(ApplicationProperties.class.getResource("/application.properties").getFile())
					.getParentFile();
		}
		return root;

	}
}
