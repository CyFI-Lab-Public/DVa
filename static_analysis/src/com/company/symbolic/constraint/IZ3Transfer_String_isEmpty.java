package com.company.symbolic.constraint;

import com.company.config.Envir;
import com.company.data.IRunTimeManager;
import com.company.taint.ProcessExpr;
import com.company.util.LogFileUtil;
import org.json.JSONException;
import org.json.JSONObject;
import soot.Local;
import soot.Value;
import soot.jimple.InvokeExpr;

import java.util.List;

public class IZ3Transfer_String_isEmpty implements IZ3Transfer {

	@Override
	public void handle(IRunTimeManager rtm, ProcessExpr pExpr, InvokeExpr invokeExpr, Local target, Local base, String sign, List<Value> args) {
		// TODO Auto-generated method stub
		if (rtm.getRttd().contains(base)) {
			rtm.getRttd().transfer(target, base);
			rtm.getRttd().getTaintNode(target).setCompareMethod(sign);
		}
	}

	@Override
	public String transfer(Constraint cons) throws JSONException {
		// TODO Auto-generated method stub
		String source = cons.getSource().toZ3();
		savetoJson(cons);
		return String.format("'%s' String.isEmpty -- (%s)%s", source, cons.getCondition(), cons.isSatisfy());
	}

	public void savetoJson(Constraint cons) throws JSONException {

		JSONObject js = new JSONObject();
//		js.append("fileName", Envir.apkName);
		js.append("consSrc", cons.getSource().toZ3());

		js.append("consName", "String_isEmpty");
		js.append("consArg", "");
		js.append("consCondition", cons.getCondition().toString());
		js.append("consIsSatisfy", cons.isSatisfy());

		LogFileUtil.json(js.toString());
	}
}
