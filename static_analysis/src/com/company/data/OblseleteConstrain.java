package com.company.data;

import com.company.taint.ITag;
import com.company.taint.TaintNode;
import com.company.util.MyLog;
import com.company.util.Z3Util;
import org.json.JSONException;
import org.json.JSONObject;
import soot.Unit;
import soot.Value;
import soot.jimple.internal.AbstractJimpleIntBinopExpr;
import soot.jimple.internal.JEqExpr;
import soot.jimple.internal.JNeExpr;

public class OblseleteConstrain {
	// private List<Value> conditions = new ArrayList<Value>();

	Unit sourceStmt;
	Value condition;
	TaintNode tn;
	boolean satisfy = false;

	public OblseleteConstrain(Unit sourceStmt, Value condition, boolean satisfy, TaintNode tn) {
		this.sourceStmt = sourceStmt;
		this.condition = condition;
		this.tn = tn;
		this.satisfy = satisfy;
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		JSONObject js = null;
		try {
			js = new JSONObject("{}");
			addToString(js);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		StringBuilder sb = new StringBuilder();
		sb.append("------------------------------------\n");
		sb.append("Constrain:\n");
		sb.append("    Source Code : " + sourceStmt + "\n");
		sb.append("    Condition   : " + condition + condition.getClass() + "\n");
		sb.append("    Satisfy     : " + satisfy + "\n");
		sb.append("    Tags        : " + tn.toString() + "\n");
		sb.append("    z3          : " + js.toString() + "\n");
		sb.append("====================================\n");

		return sb.toString();
	}

	// {'544': {'name': 'X', 'z3': '......'}}
	public void addToString(JSONObject obj) throws JSONException {
		String vid = null;
		String name = null;

		String state = "init";
		if ((vid = tn.getTag("viewid").getData().toString()) != null) {
			JSONObject tmp = initJSON(vid, obj);
			name = tmp.getString("name");

			for (ITag tag : tn.getTags()) {
				if (tag.getName().equals("gettext")) {
					state = "text";
				} else if (tag.getName().equals("tostring")) {
					state = "text";
				} else if (tag.getName().equals("length")) {
					if (state.equals("text")) {
						state = "textlen";
					} else {
						MyLog.info("Unknow taint state!:" + toString());
					}
				}
			}
			generateZ3(state, tmp, name);
		}

	}

	public void generateZ3(String state, JSONObject tmp, String name) throws JSONException {
		if (state.equals("textlen")) {
			AbstractJimpleIntBinopExpr econdition = (AbstractJimpleIntBinopExpr) condition;
			String o1, o2;
			if (econdition.getOp1() == tn.getLocal()) {
				o1 = name;
				o2 = econdition.getOp2().toString();
			} else {
				o1 = econdition.getOp2().toString();
				o2 = name;
			}
			tmp.append("z3", Z3Util.assertLen(isEq(), o1, o2));
		}
	}

	public boolean isEq() {
		if (condition instanceof JEqExpr) {
			if (satisfy) {
				return true;
			} else {
				return false;
			}
		} else if (condition instanceof JNeExpr) {
			if (satisfy) {
				return false;
			} else {
				return true;
			}
		} else {
			MyLog.info("Unknow symbol:" + toString());
		}
		return false;
	}

	public JSONObject initJSON(String vid, JSONObject obj) throws JSONException {
		JSONObject tmp = null;
		String name = null;
		if (obj.has(vid)) {
			tmp = obj.getJSONObject(vid);
		} else {
			name = (char) (obj.length() + 'A') + "";
			tmp = new JSONObject("{}");
			tmp.accumulate("name", name);

			tmp.append("z3", Z3Util.declare(name, "String"));

			obj.accumulate(vid, tmp);
		}
		return tmp;
	}

	public static void main(String[] asd) throws JSONException {
		JSONObject obj = new JSONObject("{}");
		JSONObject tp = new JSONObject("{}");
		obj.accumulate("a", tp);
		obj.accumulate("b", tp);
		tp.append("a", "a");
		tp.append("a", 1);
		System.out.println(obj.toString());
		System.out.println(obj.length());
		System.out.println(tp.get("a").getClass());
		System.out.println(tp.getJSONArray("a"));
		

	}
}
