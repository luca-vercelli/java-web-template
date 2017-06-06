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
	 * Avvia l'applicazione con l'embedded server. Vuol dire che non c'è bisogno
	 * di installare Glassfish, basta fare "run as application" da Eclipse.
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

				// deployer.deploy(war, "--name=simple", "--contextroot=simple",
				// "--force=true");
				// deployer.deploy(war) can be invoked instead. Other parameters
				// are
				// optional.

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

	public static ScatteredArchive getScatteredArchive() throws IOException {

		File webapp = new File("src" + File.separator + "main" + File.separator + "webapp");
		File classes = new File("target" + File.separator + "classes");

		ScatteredArchive archive = new ScatteredArchive(CONTEXT_ROOT, ScatteredArchive.Type.WAR, webapp);
		archive.addClassPath(classes);

		return archive;

	}

}
