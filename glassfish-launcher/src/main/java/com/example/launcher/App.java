package com.example.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URI;

import org.glassfish.embeddable.CommandResult;
import org.glassfish.embeddable.Deployer;
import org.glassfish.embeddable.GlassFish;
import org.glassfish.embeddable.GlassFishException;
import org.glassfish.embeddable.GlassFishProperties;
import org.glassfish.embeddable.GlassFishRuntime;
import org.glassfish.embeddable.archive.ScatteredArchive;
import org.glassfish.embeddable.archive.ScatteredEnterpriseArchive;

/**
 * Launcher for Embedded Glassfish + all required projects.
 * 
 * We deploy WAR's, not EAR, nor JAR. You must configure this project with the
 * same values you normally would put in EAR. Glassfish create a WAR for each
 * project, without any JAR inside: all JAR's must be passed as dependency to
 * this project. EJB JAR's must be expanded hereFor example, you can tell
 * Eclipse this project (glassfish-launcher) depends on the others (JAR's and
 * WAR's).
 * 
 * Side effect: all WAR's will share the same libraries!
 */
public class App {

	public static final File CONFIG_FILE = new File("config", "domain.xml");
	public static final Project[] PROJECTS = { new Project("java-web-template", "myapp", "java-jar-template"),
			new Project("java-ws-template", "ws", "java-jar-template") };

	// public static final Boolean ENABLE_HTTPS = true;
	// public static final Integer HTTPS_PORT = 8083;

	/**
	 * Run application with embedded Glassfish Server. You don't need to install
	 * Glassfish, just run this class, i.e. if you use Eclipse just click "Run
	 * as application..." or if you prefer command line run:
	 * 
	 * java com.example.launcher.App
	 * 
	 * @throws Exception
	 * 
	 * @throws IOException
	 */
	public static void main(String[] args) throws Exception {

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

				// if (ENABLE_HTTPS)
				// createHttpListener(glassfish, HTTPS_PORT);

			} catch (GlassFishException e) {
				e.printStackTrace();
				return;
			}

			// Deploy webapp
			try {

				deployer = glassfish.getDeployer();
				// ScatteredEnterpriseArchive archive =
				// getScatteredEAR(PROJECTS);
				// appName = deployer.deploy(archive.toURI());
				// for other parameters see
				// https://docs.oracle.com/cd/E19798-01/821-1758/deploy-1/index.html

				for (Project project : PROJECTS) {
					ScatteredArchive archiveModule = getScatteredArchive(project.name, project.jars);
					URI uri = archiveModule.toURI(); // This will actually
														// create the .war
					String thisaAppName = deployer.deploy(new File(uri), "--contextroot=" + project.contextroot);
					System.out.println("just deployed: " + thisaAppName);
				}

				System.out.println("Install root: " + System.getProperty("com.sun.aas.installRoot"));

				System.out.println("Instance root: " + System.getProperty("com.sun.aas.instanceRoot"));
				// If you prefer a static path, you can replace
				// ${com.sun.aas.instanceRoot} everywhere in domanin.xml
				// That way you can use e.g. FileSync plugin to update html's

				fixLoginConf();

				// First app should be the main one...
				System.out.println("Listen url: http://localhost:8080/" + PROJECTS[0].contextroot);
				System.out.println("SHOULD BE ALSO: https://localhost:8181/" + PROJECTS[0].contextroot);

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
	 * Set up path for login.conf. If you want use custom login.conf, you must
	 * modify this.
	 */
	private static void fixLoginConf() {

		System.setProperty("java.security.auth.login.config",
				System.getProperty("com.sun.aas.installRoot") + "/config/login.conf");

		// If you want use custom login.conf:
		// System.setProperty("java.security.auth.login.config",
		// "./config/login.conf);

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
	 * Create Glassfish' scattered archive (i.e. WAR file, not JAR) for the
	 * application.
	 * 
	 * JAR must follow canonical Maven directory structure.
	 * 
	 * WAR must follow canonical Maven webapp directory structure.
	 * 
	 * @return
	 * @throws IOException
	 */
	public static ScatteredArchive getScatteredArchive(String projectName, String... jarProjects) throws IOException {

		String root = "../" + projectName + "/";

		File webapp = new File(root + "src" + File.separator + "main" + File.separator + "webapp");
		File classes = new File(root + "target" + File.separator + "classes");

		ScatteredArchive archive = new ScatteredArchive(projectName, ScatteredArchive.Type.WAR, webapp);
		archive.addClassPath(classes);
		for (String otherProject : jarProjects) {
			String otherRoot = "../" + otherProject + "/";
			File otherClasses = new File(otherRoot + "target" + File.separator + "classes");
			archive.addClassPath(otherClasses);
		}
		return archive;

	}

	/**
	 * Create Glassfish' scattered EAR archive for the application (it must
	 * include all required WAR's).
	 * 
	 * We assume that the app name in application.xml is equal to the project
	 * name plus the file extension.
	 * 
	 */
	public static ScatteredEnterpriseArchive getScatteredEAR(String[] projectNames) throws IOException {

		final String APP_NAME = "myapp"; // EAR Application name
		final String APPLICATION_XML = "../java-ear-template/target/application.xml";

		ScatteredEnterpriseArchive archive = new ScatteredEnterpriseArchive(APP_NAME);
		archive.addMetadata(new File(APPLICATION_XML));
		for (String projectName : projectNames) {
			ScatteredArchive archiveModule = getScatteredArchive(projectName);
			URI uri = archiveModule.toURI();
			String archiveModuleFileName = uri.getPath().substring(uri.getPath().lastIndexOf('/'));
			archive.addArchive(uri, archiveModuleFileName);
		}
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
	private static void createHttpListener(GlassFish glassfish, Integer port) throws GlassFishException {

		run(glassfish, "create-http-listener", "--listeneraddress=0.0.0.0", "--listenerport=" + port.toString(),
				"--defaultvs=server", "--securityenabled=true", "--enabled=true", "http-listener3");
		run(glassfish, "create-ssl", "--type=http-listener", "--certname=s1as", "http-listener3");
	}

	/**
	 * Create email resource.
	 * 
	 * @param glassfish
	 * @throws GlassFishException
	 */
	private static void createMailResource(GlassFish glassfish) throws GlassFishException {
		final String COMMAND = "create-javamail-resource";
		final String ARGS = "--mailhost=smtp.gmail.com --mailuser=java.web.template@gmail.com --fromaddress=java.web.template@gmail.com mail/mainMailSession";
		run(glassfish, COMMAND, ARGS);
	}

	/**
	 * Facility: split arguments, execute asadmin command, print output.
	 * Algorithm for splitting args is naive, just search for spaces.
	 * 
	 * @param glassfish
	 * @param command
	 * @param args
	 * @return
	 * @throws GlassFishException
	 */
	private static CommandResult run(GlassFish glassfish, String command, String args) throws GlassFishException {
		return run(glassfish, command, args.split(" "));
	}

	/**
	 * Facility: execute asadmin command, print output.
	 * 
	 * @param glassfish
	 * @param command
	 * @param args
	 * @return
	 * @throws GlassFishException
	 */
	private static CommandResult run(GlassFish glassfish, String command, String... args) throws GlassFishException {
		CommandResult cr = glassfish.getCommandRunner().run(command, args);
		System.out.println("Calling asadmin " + command);
		// CommandResult is
		// com.sun.enterprise.admin.cli.embeddable.CommandExecutorImpl$1
		System.out.println("Command output: " + cr.getOutput());
		if (cr.getFailureCause() != null)
			cr.getFailureCause().printStackTrace();
		return cr;
	}

	public static class Project {
		public String name;
		public String contextroot;
		public String[] jars;

		public Project(String name, String contextroot, String... jars) {
			this.name = name;
			this.contextroot = contextroot;
			this.jars = jars;
		}
	}
}
