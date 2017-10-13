package com.hw.process.utils;


import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Date;

import org.apache.http.client.utils.DateUtils;

public class JavaUtils implements Base{

	public static java.sql.Timestamp getTimeStamp(String s) {
		Date d = DateUtils.parseDate(s);
		System.out.println("Date:" + d);
		return new java.sql.Timestamp(d.getTime());
	}
	
	public static void printResultSet(ResultSet rs) throws SQLException {
		ResultSetMetaData rsmd = rs.getMetaData();
		int columnsNumber = rsmd.getColumnCount();
		while (rs.next()) {
			for (int i = 1; i <= columnsNumber; i++) {
				if (i > 1)
					log.debug(",  ");
				String columnValue = rs.getString(i);
				log.debug(columnValue + "=" + rsmd.getColumnName(i));
			}
		}
	}
}
