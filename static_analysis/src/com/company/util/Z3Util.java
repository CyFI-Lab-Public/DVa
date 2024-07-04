package com.company.util;

import com.company.data.OblseleteConstrain;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Iterator;
import java.util.List;

public class Z3Util {

	static String DECLARE = "(declare-variable %s %s)";
	static String ASSERT_LEN_EQ = "(assert (= (Length %s) %s))";
	static String ASSERT_LEN_NQ = "(assert (not (= (Length %s) %s)))";

	static String CHECKSAT = "(check-sat)";
	static String GETMODEL = "(get-model)";

	public static String declare(String name, String type) {
		return String.format(DECLARE, name, type);
	}
	
	public static String assertLen(boolean iseq, String op1, String op2) {
		if (iseq) {
			return String.format(ASSERT_LEN_EQ, op1, op2);
		} else {
			return String.format(ASSERT_LEN_NQ, op1, op2);
		}
	}

	public static String checkSat() {
		return CHECKSAT;
	}

	public static String getModel() {
		return GETMODEL;
	}

	public static String constrains2Z3(List<OblseleteConstrain> cs) throws JSONException {
		JSONObject obj = new JSONObject("{}");
		JSONObject tmp = null;
		for (OblseleteConstrain cons : cs) {
			cons.addToString(obj);
		}
		
		Iterator keys = obj.keys();

		for (;keys.hasNext(); ) {
			String key = (String)keys.next(); // Here's your key
			try {
				tmp = obj.getJSONObject(key);
				tmp.append("z3", Z3Util.checkSat());
				tmp.append("z3", Z3Util.getModel());
			} catch (JSONException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}

		}
		return obj.toString();
	}
}
