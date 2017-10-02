package com.example.webserver;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.logging.Logger;

import org.glassfish.api.admin.ParameterMap;
import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.CommandRunner;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.glassfish.embeddable.archive.ScatteredArchive;

public class Main {

	public static final File CONFIG_FILE = new File("config", "domain.xml");
	public static final File JAAS_FILE = new File("config", "login.conf");
	public static final String CONTEXT_ROOT = "myapp";
	public static final Boolean ENABLE_HTTPS = false;
	public static final Integer HTTPS_PORT = 8083; // should be 8081, used by
													// domain.xml

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

				System.setProperty("java.security.auth.login.config", JAAS_FILE.getPath());

				fixBrandingStuff();

				GlassFishProperties properties = new GlassFishProperties();
				properties.setConfigFileURI(CONFIG_FILE.toURI().toString());
				glassfish = GlassFishRuntime.bootstrap().newGlassFish(properties);
				glassfish.start();

				if (ENABLE_HTTPS)
					fixHttpsStuff(glassfish, HTTPS_PORT);

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

				if (ENABLE_HTTPS)
					System.out.println("Listen url HTTPS: https://localhost:" + HTTPS_PORT + "/" + CONTEXT_ROOT);
				else
					System.out.println("Listen url HTTP: http://localhost:8080/" + CONTEXT_ROOT);

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

	/**
	 * Configure HTTPS. Glassfish Embedded currently ignores domain.xml
	 * settings.
	 * 
	 * @throws GlassFishException
	 * 
	 * @see https://stackoverflow.com/questions/2401341
	 */
	private static void fixHttpsStuff(GlassFish glassfish, Integer port) throws GlassFishException {

		System.out.println("Calling: asadmin create-http-listener");
		// this is org.glassfish.embeddable.CommandRunner,
		// not org.glassfish.api.admin.CommandRunner
		CommandRunner runner = glassfish.getCommandRunner();
		CommandResult result = runner.run("create-http-listener", "--listeneraddress=0.0.0.0",
				"--listenerport=" + port.toString(), "--defaultvs=server", "--securityenabled=true", "--enabled=true",
				"http-listener3");
		System.out.println("Command output: " + result.getOutput());
		if (result.getFailureCause() != null)
			result.getFailureCause().printStackTrace();

	}
}
