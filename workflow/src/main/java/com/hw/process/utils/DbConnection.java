package com.hw.process.utils;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Collections;
import java.util.Map;
import java.util.TreeMap;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

//Singleton class for db connetions with datasource  tracking/storing
public class DbConnection {
	private static DbConnection instance = null;
	private static final Logger log = LoggerFactory.getLogger(DbConnection.class);

	private Context context = null;

	static private Map<String, DataSource> sources = Collections
			.synchronizedSortedMap(new TreeMap<String, DataSource>());


	private DbConnection() {
		try {
			context = new InitialContext();
		} catch (NamingException ex) {
			log.error("Data source initial context error.", ex);
		}
	}

	public static DbConnection i() {
		if (instance == null) {
			synchronized (DbConnection.class) {
				if (instance == null) {
					instance = new DbConnection();
				}
			}
		}
		return instance;
	}

	/**
	 * This method allows you to create your own Connection object and still use all
	 * the code that is written around data sources DataSource.
	 * 
	 * @param name
	 * @param ds
	 */
	public static synchronized void addDataSource(String name, DataSource ds) {

		sources.put(name, ds);
	}

	public synchronized Connection getConnection(String jndi_name) throws NamingException, SQLException {
		DataSource ds = sources.get(jndi_name);

		if (ds == null && context != null) {
			ds = (javax.sql.DataSource) context.lookup(jndi_name);
			sources.put(jndi_name, ds);
			log.info((ds == null ? "Missing" : "Add") + " data source for " + jndi_name);
		}
		Connection conn = (ds != null ? ds.getConnection() : null);
		log.debug("Found connection " + (conn != null ? conn : "?") + " for " + jndi_name);
		return conn;

	}
}