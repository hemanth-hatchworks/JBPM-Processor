package com.hw.process.utils;

import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;

import com.google.gson.JsonElement;

public class LookupData {
	private static LookupData instance = null;

	private Map<String, Integer> applicantStatus = new HashMap<>();
	private Map<String, Integer> interviewStatus = new HashMap<>();
	private Map<String, Integer> interviewType = new HashMap<>();

	private LookupData() {

	}
	
	private LookupData initializeCache() {
		try {
			interviewStatus = cacheMapData("r_interviewstatus", "r_interviewstatusid","fn_lu_interviewstatus_read");
			interviewType = cacheMapData("r_interviewtype", "r_interviewtypeid", "fn_lu_interviewtype_read");
		} catch (SQLException e) {
			e.printStackTrace();
		}
		
		return this;
	}

	public static LookupData i() {
		if (instance == null) {
			synchronized (LookupData.class) {
				if (instance == null) {
					instance = new LookupData().initializeCache();;
				}
			}
		}
		return instance;
	}
	

	private Map<String,Integer> cacheMapData(String keyIdentifier, String valueIdentifier, String sp) throws SQLException {
		Map<String, Integer> map = new HashMap<>();
		try (Connection conn = DBUtility.getDataSource().getConnection();) {
			CallableStatement cs = conn.prepareCall(PropertyUtils.get(sp));
			cs.execute();
			for (JsonElement element : JsonUtils.convertToJSON(cs.getResultSet())) {

				map.put(element.getAsJsonObject().get(keyIdentifier).getAsString(),
						element.getAsJsonObject().get(valueIdentifier).getAsInt());
			}
			cs.close();
		}
		return map;

	}

	public Map<String, Integer> getApplicantStatus() {
		return applicantStatus;
	}

	public void setApplicantStatus(Map<String, Integer> applicantStatus) {
		this.applicantStatus = applicantStatus;
	}

	public Map<String, Integer> getInterviewStatus() {
		return interviewStatus;
	}

	public void setInterviewStatus(Map<String, Integer> interviewStatus) {
		this.interviewStatus = interviewStatus;
	}

	public Map<String, Integer> getInterviewType() {
		return interviewType;
	}

	public void setInterviewType(Map<String, Integer> interviewType) {
		this.interviewType = interviewType;
	}

}
