package com.hw.process.utils;

import org.apache.commons.dbcp2.BasicDataSource;

public class DBUtility {
	private static BasicDataSource dataSource;
	final static String databaseURL = "jdbc:pgsql://10.33.129.44:5432/xaviertrans";
	// final static String databaseURL =
	// "jdbc:p6spy:pgsql://10.33.129.44:5432/xaviertrans";
//	final static String databaseURL = "jdbc:pgsql://10.33.129.44:5432/xaviertrans";

	final static String user = "xavierappuser";
	final static String password = "people_scout123";

	public static BasicDataSource getDataSource() {

		if (dataSource == null) {
			dataSource = new BasicDataSource();
			dataSource.setUrl(databaseURL);
			dataSource.setUsername(user);
			dataSource.setPassword(password);

			dataSource.setMinIdle(5);
			dataSource.setMaxIdle(10);
			dataSource.setMaxOpenPreparedStatements(100);
		}
		return dataSource;
	}

}