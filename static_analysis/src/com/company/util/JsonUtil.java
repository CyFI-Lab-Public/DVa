package com.company.util;

import org.json.JSONArray;
import org.json.JSONException;

public class JsonUtil {

	public static void insert(JSONArray jarray, int index, Object obj) {
		// TODO Auto-generated method stub
	}

	public static void main(String[] args) {
		JSONArray jarray = null;
		try {
			jarray = new JSONArray("[0,1]");
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		insert(jarray,1,2);
		System.out.println(jarray);
	}

}
