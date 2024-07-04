package com.company.symbolic.constraint;

import com.company.config.Envir;
import com.company.data.IRunTimeManager;
import com.company.taint.ITag;
import com.company.taint.ProcessExpr;
import com.company.taint.ValueTag;
import com.company.util.LogFileUtil;
import com.company.util.MyLog;
import org.json.JSONException;
import org.json.JSONObject;
import soot.Local;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.StringConstant;

import java.util.List;

public class IZ3Transfer_String_equals implements IZ3Transfer {

	static String tagName = "String.equals";

	@Override
	public void handle(IRunTimeManager rtm, ProcessExpr pExpr, InvokeExpr invokeExpr, Local target, Local base, String sign, List<Value> args) {
		// TODO Auto-generated method stub
		Value arg0 = args.get(0);
		if (arg0 instanceof StringConstant && rtm.getRttd().contains(base)) {
			StringConstant argStr = (StringConstant) arg0;

			rtm.getRttd().transfer(target, base);
			rtm.getRttd().getTaintNode(target).setCompareMethod(sign);
			rtm.getRttd().addTag(target, new ValueTag(tagName, argStr.value));

		} else if (arg0 instanceof Local && rtm.getRttd().contains(base) && rtm.getRttd().contains((Local) arg0)) {
			Local local = (Local) arg0;
			IConstraintSource targetSource = rtm.getRttd().getTaintNode(local).getSource();

			rtm.getRttd().transfer(target, base);
			rtm.getRttd().getTaintNode(target).setCompareMethod(sign);
			rtm.getRttd().addTag(target, new ValueTag(tagName, targetSource));

		} else {
			MyLog.info("String_equals", "Not StringConstant arg " + args.get(0));
		}
	}

	@Override
	public String transfer(Constraint cons) throws JSONException {
		// TODO Auto-generated method stub
		String source = cons.getSource().toZ3();
		ITag tag = cons.getTag(tagName);

		String target = "";
		if (tag.getData() instanceof String) {
			target = tag.getData().toString();
		} else if (tag.getData() instanceof IConstraintSource) {
			target = ((IConstraintSource) tag.getData()).toZ3();
		}
		savetoJson(cons);
		return String.format("'%s' equals '%s' -- (%s)%s", source, target, cons.getCondition(), cons.isSatisfy());
	}

	public void savetoJson(Constraint cons) throws JSONException {

		ITag tag = cons.getTag(tagName);

		String target = "";
		if (tag.getData() instanceof String) {
			target = tag.getData().toString();
		} else if (tag.getData() instanceof IConstraintSource) {
			target = ((IConstraintSource) tag.getData()).toZ3();
		}

		JSONObject js = new JSONObject();
		js.append("fileName", Envir.apkName);
		js.append("consSrc", cons.getSource().toZ3());

		js.append("consName", "String_equals");
		js.append("consArg", target);
		js.append("consCondition", cons.getCondition().toString());
		js.append("consIsSatisfy", cons.isSatisfy());

		LogFileUtil.json(js.toString());
	}

	public static void main(String[] ad) {
		System.out.println("" instanceof String);
		System.out.println("" instanceof Object);
	}

}
