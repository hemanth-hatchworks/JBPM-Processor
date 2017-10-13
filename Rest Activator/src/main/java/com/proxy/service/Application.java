package com.proxy.service;

import java.io.IOException;
import java.net.InetAddress;
import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.core.env.Environment;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@SpringBootApplication
@RestController

public class Application extends Base {

	private static Log log = LogFactory.getLog(Application.class);

	Application() {
		log = LogFactory.getLog(Application.class);
		log.info("CTOR");
		// Map<String, String> props = java.lang.management.ManagementFactory.getRuntimeMXBean().getSystemProperties();
		// log.info(props.toString());
		// BASE_URL= "http://" + environment.getHostname() + ":82";
		// log.info("BASE_URL is '" + BASE_URL + "'");
	}

	/**
	 * Main function starts the Spring Boot application, and logs some environment details and the access URL.
	 * 
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException { // throws UnknownHostException {
		log = LogFactory.getLog(Application.class);


		Thread.setDefaultUncaughtExceptionHandler(new UEH(log));

		try {
			SpringApplication app = new SpringApplication(Application.class);
			addDefaultProfile(app);
			app.setBannerMode(Banner.Mode.OFF);
			Environment env = app.run().getEnvironment();
			String envDetails = "\n----------------------------------------------------------\n\t" + "Application '" + env.getProperty("spring.application.name") + "' is running!"
					// +"\n\tLocal: \t\thttp://127.0.0.1:"+env.getProperty("server.port")
					+ "\n Access URL: is: \thttp://" + InetAddress.getLocalHost().getHostAddress() + ":" + env.getProperty("server.port")
					+ "\n----------------------------------------------------------";
			log.info(envDetails);
		} catch (Exception e) {
			log.error(e, e);
		}

	}

	/**
	 * Default profile is 'dev' for development, when no other profiles are defined in application.yml or on the command line as as system variable, e.g.
	 * -Dspring.profiles.active=test
	 * 
	 * @param app
	 */
	protected static void addDefaultProfile(SpringApplication app) {
		Map<String, Object> defProperties = new HashMap<>();

		defProperties.put("spring.profiles.active", "dev");
		app.setDefaultProperties(defProperties);
	}

	/**
	 * RESTful endpoint (GET) to verify the current user's information. This is a secure URI, and will return 'not authorized' if called before obtaining an OAuth2 token.
	 *
	 * @param user
	 * @return JSON corresponding to all information known about the current user.
	 */
	@RequestMapping("/user")
	public Principal user(Principal user) {
		return user;
	}

}

/**
 * Uncaught Exception Handling, with recursive nested exception stack tracing.
 */
class UEH implements Thread.UncaughtExceptionHandler {

	private Log log;

	UEH(Log logger) {
		log = logger;
		log.info("\nUEH LOGGING INITIALIZED.");
	}

	@Override
	public void uncaughtException(Thread t, Throwable e) {
		StackTraceElement[] ses = e.getStackTrace();
		log.info("Exception in thread \"" + t.getName() + "\" " + e.toString());
		for (StackTraceElement se : ses) {
			log.info("\tat " + se);
		}
		Throwable ec = e.getCause();
		if (ec != null) {
			uncaughtException(t, ec);
		}
	}
}
