package com.example.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;

import static java.nio.file.Files.*;
import java.nio.file.Path;
import java.nio.file.Paths;

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

				System.out.println("Install root: " + System.getProperty("com.sun.aas.installRoot"));

				System.out.println("Instance root: " + System.getProperty("com.sun.aas.instanceRoot"));
				// If you prefer a static path, you can replace
				// ${com.sun.aas.instanceRoot} everywhere in domanin.xml
				// That way you can use e.g. FileSync plugin to update html's

				System.out.println("Listen url: http://localhost:8080/myapp");
				// FIXME this should not be static....

				// FIXME let branding stuff work!
				// see also fixBrandingStuff()
				// com.sun.appserv.server.util.Version reads the file when it
				// does not exist yet
				// see https://github.com/javaee/glassfish/issues/21101

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

	// FIXME currently useless.
	protected static void fixBrandingStuff() throws IOException {
		String installRoot = System.getProperty("com.sun.aas.installRoot");
		if (installRoot != null && !installRoot.trim().equals("")) {
			Path p1 = Paths.get("config", "branding", "glassfish-version.properties");
			Path p2 = Paths.get(installRoot, "config", "branding", "glassfish-version.properties");

			if (p1.toFile().exists()) {
				p2.getParent().toFile().mkdirs();
				copy(p1, p2);
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
