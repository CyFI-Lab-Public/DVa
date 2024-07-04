package com.company.symbolic.constraint;

import com.company.config.Envir;
import com.company.data.IRunTimeManager;
import com.company.taint.ITag;
import com.company.taint.ProcessExpr;
import com.company.util.LogFileUtil;
import com.company.util.MyLog;
import soot.Local;
import soot.Value;
import soot.jimple.InvokeExpr;

import java.util.List;

public class IZ3Transfer_Pattern_matcher implements IZ3Transfer {

	@Override
	public void handle(IRunTimeManager rtm, ProcessExpr pExpr, InvokeExpr invokeExpr, Local target, Local base, String sign, List<Value> args) {
		// TODO Auto-generated method stub
		if (args.get(0) instanceof Local) {
			rtm.getRttd().transfer(target, (Local) args.get(0));
			ITag tag = rtm.getRttd().getTag(base, "Patterns.Pattern");
			if (tag != null)
				rtm.getRttd().addTag(target, tag);
			else {
				LogFileUtil.helpInfo(Envir.apkName + " no Patterns.Pattern");
			}
		} else {
			MyLog.info("IZ3Transfer_Pattern_matcher", "Not Local arg " + args.get(0));
		}
	}

	@Override
	public String transfer(Constraint cons) {
		// TODO Auto-generated method stub
		return "";
	}

}
