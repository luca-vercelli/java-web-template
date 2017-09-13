package com.example.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.glassfish.embeddable.archive.ScatteredArchive;

public class Main {

	public static final File CONFIG_FILE = new File("config", "domain.xml");
	public static final String CONTEXT_ROOT = "myapp";

	/**
	 * Run application with embedded Glassfish Server. You don't need to install
	 * Glassfish, just run this class, i.e. if you use Eclipse just click "Run
	 * as application..." or if you prefer command line run:
	 * 
	 * java com.example.webserver.Main
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) {

		GlassFish glassfish = null;
		Deployer deployer = null;
		String appName = null;

		try {

			// Start webserver

			try {

				fixBrandingStuff();

				GlassFishProperties properties = new GlassFishProperties();
				properties.setConfigFileURI(CONFIG_FILE.toURI().toString());
				glassfish = GlassFishRuntime.bootstrap().newGlassFish(properties);
				glassfish.start();

			} catch (GlassFishException e) {
				e.printStackTrace();
				return;
			}

			// Deploy webapp
			try {

				deployer = glassfish.getDeployer();
				ScatteredArchive archive = getScatteredArchive();
				appName = deployer.deploy(archive.toURI(), "--contextroot=" + CONTEXT_ROOT);
				// for other parameters see
				// https://docs.oracle.com/cd/E19798-01/821-1758/deploy-1/index.html

				System.out.println("Install root: " + System.getProperty("com.sun.aas.installRoot"));

				System.out.println("Instance root: " + System.getProperty("com.sun.aas.instanceRoot"));
				// If you prefer a static path, you can replace
				// ${com.sun.aas.instanceRoot} everywhere in domanin.xml
				// That way you can use e.g. FileSync plugin to update html's

				System.out.println("Listen url: http://localhost:8080/" + CONTEXT_ROOT);
				System.out.println("Listen url SHOULD BE ALSO: https://localhost:8181/" + CONTEXT_ROOT); // FIXME

			} catch (GlassFishException e) {
				e.printStackTrace();
			}

			// Wait for Enter

			System.out.println("Press Enter to stop server");
			try {
				new BufferedReader(new InputStreamReader(System.in)).readLine();
			} catch (IOException e) {
				e.printStackTrace();
			}

		} catch (Exception e) {

			// High-level exception handling

			e.printStackTrace();

		} finally {

			// Stop web server

			if (deployer != null && appName != null) {
				try {
					deployer.undeploy(appName);
				} catch (GlassFishException e) {
					e.printStackTrace();
				}
			}

			if (glassfish != null) {
				try {

					// ??? glassfish.dispose();
					glassfish.stop();

				} catch (GlassFishException e) {
					e.printStackTrace();
				}
			}
		}
	}

	/**
	 * Workaround to load branding file.
	 * 
	 * @see https://github.com/javaee/glassfish/issues/21101
	 * @throws IOException
	 * @throws ClassNotFoundException
	 */
	private static void fixBrandingStuff() throws IOException, ClassNotFoundException {
		System.setProperty("com.sun.aas.installRoot", new File(".").getAbsolutePath());
		Class.forName("com.sun.appserv.server.util.Version");
		System.clearProperty("com.sun.aas.installRoot");
	}

	/**
	 * Create Glassfish' scattered archive (i.e. WAR file) for the application.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static ScatteredArchive getScatteredArchive() throws IOException {

		File webapp = new File("src" + File.separator + "main" + File.separator + "webapp");
		File classes = new File("target" + File.separator + "classes");

		ScatteredArchive archive = new ScatteredArchive(CONTEXT_ROOT, ScatteredArchive.Type.WAR, webapp);
		archive.addClassPath(classes);

		return archive;

	}

}
