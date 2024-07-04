package com.company.symbolic.constraint;

import com.company.data.IRunTimeManager;
import com.company.taint.ProcessExpr;
import soot.Local;
import soot.Value;
import soot.jimple.InvokeExpr;

import java.util.List;

public class IZ3Transfer_JustTransfer implements IZ3Transfer {

	@Override
	public void handle(IRunTimeManager rtm, ProcessExpr pExpr, InvokeExpr invokeExpr, Local target, Local base, String sign, List<Value> args) {
		// TODO Auto-generated method stub
		// MyLog.info(rtm.getRttd());
		// MyLog.info(base);
		if (base != null && rtm.getRttd().contains(base)) {
			rtm.getRttd().transfer(target, base);
		}
	}

	@Override
	public String transfer(Constraint cons) {
		// TODO Auto-generated method stub
		return "";
	}

}
