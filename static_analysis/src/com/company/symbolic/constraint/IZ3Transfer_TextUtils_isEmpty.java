package com.company.symbolic.constraint;

import com.company.config.Envir;
import com.company.data.IRunTimeManager;
import com.company.taint.ProcessExpr;
import com.company.util.LogFileUtil;
import com.company.util.MyLog;
import org.json.JSONException;
import org.json.JSONObject;
import soot.Local;
import soot.Value;
import soot.jimple.InvokeExpr;

import java.util.List;

public class IZ3Transfer_TextUtils_isEmpty implements IZ3Transfer {

	@Override
	public void handle(IRunTimeManager rtm, ProcessExpr pExpr, InvokeExpr invokeExpr, Local target, Local base, String sign, List<Value> args) {
		// TODO Auto-generated method stub
		if (args.get(0) instanceof Local && rtm.getRttd().contains((Local) args.get(0))) {
			rtm.getRttd().transfer(target, (Local) args.get(0));
			rtm.getRttd().getTaintNode(target).setCompareMethod(sign);
		} else {
			MyLog.info("IZ3Transfer_TextUtils_isEmpty", "Not Local arg " + args.get(0));
		}
	}

	@Override
	public String transfer(Constraint cons) throws JSONException {
		// TODO Auto-generated method stub
		String source = cons.getSource().toZ3();
		savetoJson(cons);
		return String.format("'%s' isEmpty -- (%s)%s", source, cons.getCondition(), cons.isSatisfy());
	}

	public void savetoJson(Constraint cons) throws JSONException {

		JSONObject js = new JSONObject();
//		js.append("fileName", Envir.apkName);
		js.append("consSrc", cons.getSource().toZ3());

		js.append("consName", "TextUtils_isEmpty");
		js.append("consArg", "");
		js.append("consCondition", cons.getCondition().toString());
		js.append("consIsSatisfy", cons.isSatisfy());

		LogFileUtil.json(js.toString());
	}
}
