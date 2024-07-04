package com.company.symbolic.constraint;

import com.company.config.Envir;
import com.company.data.IRunTimeManager;
import com.company.taint.ITag;
import com.company.taint.ProcessExpr;
import com.company.taint.ValueTag;
import com.company.util.LogFileUtil;
import org.json.JSONException;
import org.json.JSONObject;
import soot.Local;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.StringConstant;

import java.util.List;

public class IZ3Transfer_String_startsWith implements IZ3Transfer {

	static String STR = "String.startsWith";

	@Override
	public void handle(IRunTimeManager rtm, ProcessExpr pExpr, InvokeExpr invokeExpr, Local target, Local base, String sign, List<Value> args) {
		// TODO Auto-generated method stub
		if (rtm.getRttd().contains(base)) {

			rtm.getRttd().transfer(target, base);
			rtm.getRttd().getTaintNode(target).setCompareMethod(sign);

			String arg = args.get(0).toString();
			if (args.get(0) instanceof StringConstant) {
				arg = ((StringConstant) args.get(0)).value;
			} else {
				LogFileUtil.helpInfo(String.format("String.startsWith:(%s) arg not String", rtm.getRttd().getCurrentMethod()));
			}

			rtm.getRttd().addTag(target, new ValueTag(STR, arg));
		}
	}

	@Override
	public String transfer(Constraint cons) throws JSONException {
		// TODO Auto-generated method stub
		String source = cons.getSource().toZ3();
		ITag tag = cons.getTag(STR);
		savetoJson(cons);
		return String.format("%s String.startsWith %s -- (%s)%s", source, tag.getData(), cons.getCondition(), cons.isSatisfy());
	}

	public void savetoJson(Constraint cons) throws JSONException {

		JSONObject js = new JSONObject();
//		js.append("fileName", Envir.apkName);
		js.append("consSrc", cons.getSource().toZ3());

		js.append("consName", "String_startsWith");
		js.append("consArg", cons.getTag(STR).getData());
		js.append("consCondition", cons.getCondition().toString());
		js.append("consIsSatisfy", cons.isSatisfy());
		LogFileUtil.json(js.toString());
	}
}
