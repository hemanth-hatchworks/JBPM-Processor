package com.hw.process.utils;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class PropertyUtils implements Base{

	private static Properties prop = new Properties();
	private final static String filename = "application.properties";

	static {
		load(filename);
//		prop.list(System.out);
	}

	private static void load(String file) {
		try (InputStream is = PropertyUtils.class.getClassLoader().getResourceAsStream(filename)) {
			if (is == null) {
				log.error("Sorry, unable to find " + filename);
				return;
			}
			prop.load(is);
			log.debug("Application Properties Loaded");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static String get(String key) {
		return prop.getProperty(key);
	}

}