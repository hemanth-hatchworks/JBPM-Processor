package com.hw.process.utils;

import java.sql.ResultSet;
import java.sql.SQLException;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.MapperFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public class JsonUtils {

	private static final ObjectMapper mapper = new ObjectMapper().disable(MapperFeature.DEFAULT_VIEW_INCLUSION);

	public static JsonArray convertToJSON(ResultSet resultSet) throws SQLException {
		JsonArray jsonArray = new JsonArray();
		while (resultSet.next()) {
			int total_rows = resultSet.getMetaData().getColumnCount();
			JsonObject obj = new JsonObject();
			for (int i = 0; i < total_rows; i++) {
				obj.addProperty(resultSet.getMetaData().getColumnLabel(i + 1).toLowerCase(),
						resultSet.getString(i + 1));
			}
			jsonArray.add(obj);
		}
		return jsonArray;
	}

	// single element parser filter
	public static String filterByClass(JsonElement element, Class<?> filter, Class<?> target) {
		String s = null;
		try {
			s = mapper.writerWithView(filter).writeValueAsString(new Gson().fromJson(element, target));
			return s;
		} catch (JsonProcessingException e) {
			e.printStackTrace();
		}
		return s;
	}

	// Json Array parser filter
	public static JsonArray filterByClass(JsonArray jsonArray, Class<?> filter, Class<?> target) {
		JsonArray arr = new JsonArray();
		for (JsonElement element : jsonArray) {
			arr.add(filterByClass(element, filter, target));
		}
		return arr;
	}

}
