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

public class IZ3Transfer_String_contains implements IZ3Transfer {

	static String tagName = "String.contains";

	@Override
	public void handle(IRunTimeManager rtm, ProcessExpr pExpr, InvokeExpr invokeExpr, Local target, Local base, String sign, List<Value> args) {
		// TODO Auto-generated method stub
		if (args.get(0) instanceof StringConstant && rtm.getRttd().contains(base)) {
			rtm.getRttd().transfer(target, base);
			// Sets the compareMethod field in tLocal (tainted argument constraint)
			rtm.getRttd().getTaintNode(target).setCompareMethod(sign);
			// adds the constraint from expression to comparemethod name and argument
			rtm.getRttd().addTag(target, new ValueTag(tagName, ((StringConstant) args.get(0)).value));
		} else {
			MyLog.info("String_contains", "Not StringConstant arg " + args.get(0));
		}
	}

	@Override
	public String transfer(Constraint cons) throws JSONException {
		// TODO Auto-generated method stub
		String source = cons.getSource().toZ3();
		ITag tag = cons.getTag(tagName);
		savetoJson(cons);
		return String.format("'%s' contains '%s' -- (%s)%s", source, tag.getData(), cons.getCondition(), cons.isSatisfy());
	}

	public void savetoJson(Constraint cons) throws JSONException {
		JSONObject js = new JSONObject();
//		js.append("fileName", Envir.apkName);
		js.append("consSrc", cons.getSource().toZ3());

		js.append("consName", "String_contains");
		js.append("consArg", cons.getTag(tagName).getData());
		js.append("consCondition", cons.getCondition().toString());
		js.append("consIsSatisfy", cons.isSatisfy());

		LogFileUtil.json(js.toString());
	}
}
