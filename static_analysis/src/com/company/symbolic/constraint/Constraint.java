package com.company.symbolic.constraint;

import com.company.config.Envir;
import com.company.data.RunTimeTaintData;
import com.company.taint.ITag;
import com.company.util.LogFileUtil;
import org.json.JSONException;
import org.json.JSONObject;
import soot.Local;
import soot.jimple.ConditionExpr;

import java.util.HashMap;

public class Constraint {
	Local local;
	IConstraintSource source = null;
	String compareMethod = null;
	ConditionExpr condition = null;
	boolean satisfy = false;

	/*
	 * only concrete Constraint has this field contains some needed information
	 */
	HashMap<String, ITag> tags = new HashMap<String, ITag>();

	// Constant immValue;

	public Constraint(Local local) {
		this.local = local;
	}

	public boolean hasSource() {
		return source == null;
	}

	public boolean hasCompareMethod() {
		return compareMethod == null;
	}

	public boolean hasCondition() {
		return condition == null;
	}

	public boolean hasImmValue() {
		return condition == null;
	}

	//////////////// geter && setter/////////////////

	public Local getLocal() {
		return local;
	}

	public IConstraintSource getSource() {
		return source;
	}

	public void setSource(IConstraintSource source) {
		this.source = source;
	}

	public String getCompareMethod() {
		return compareMethod;
	}

	public void setCompareMethod(String compareMethod) {
		this.compareMethod = compareMethod;
	}

	public ConditionExpr getCondition() {
		return condition;
	}

	public void setCondition(ConditionExpr condition, HashMap<String, ITag> ttags) {
		this.condition = condition;
		if (ttags != null) {
			for (ITag tag : ttags.values()) {
				tags.put(tag.getName(), tag);
			}
		}
	}

	public boolean hasTag(String name) {
		return tags.containsKey(name);
	}

	public ITag getTag(String name) {
		return tags.get(name);
	}

	public boolean isSatisfy() {
		return satisfy;
	}

	public void setSatisfy(boolean satisfy) {
		this.satisfy = satisfy;
	}

	///////////////////////////////////////////////

	public String toZ3(RunTimeTaintData rttd) throws JSONException {
		String res = null;
		if (compareMethod == null) {
			res = this.getSource().toZ3() + " " + this.condition + " --" + this.isSatisfy();
			savetoJson();
		} else {
			res = JavaMethodsHandler.getHandler(this.compareMethod).transfer(this);
		}
		return res;
	}

	public Constraint clone(Local newLocal) {
		Constraint cons = new Constraint(newLocal);
		cons.setSource(this.getSource());
		cons.setCompareMethod(this.getCompareMethod());
		cons.setCondition(this.getCondition(), this.tags);
		cons.setSatisfy(this.isSatisfy());

		return cons;
	}

	public void savetoJson() {

		JSONObject js = new JSONObject();
		try {
//			js.append("fileName", Envir.apkName);
			js.append("consSrc", this.getSource().toZ3());

			js.append("consName", "null");
			js.append("consArg", "");
			js.append("consCondition", this.getCondition().toString());
			js.append("consIsSatisfy", this.isSatisfy());
		} 

		catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		LogFileUtil.json(js.toString());
	}
}
