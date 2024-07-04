package com.company.symbolic.constraint;

import com.company.data.IRunTimeManager;
import com.company.taint.ProcessExpr;
import com.company.util.LogFileUtil;
import soot.Local;
import soot.Value;
import soot.jimple.IntConstant;
import soot.jimple.InvokeExpr;

import java.util.List;

public class IZ3Transfer_Activity_findViewById implements IZ3Transfer {

	@Override
	public void handle(IRunTimeManager rtm, ProcessExpr pExpr, InvokeExpr invokeExpr, Local target, Local base, String sign, List<Value> args) {
		// TODO Auto-generated method stub
		Value arg = args.get(0);
		if (arg instanceof IntConstant) {

			// if (!Envir.tLocals.containsKey(local)) {
			Constraint cos = new Constraint(target);
			IConstraintSource consSource = new ConstraintSourceInt();
			consSource.setSource(arg);
			cos.setSource(consSource);
			rtm.getRttd().hasSource = true;

			rtm.getRttd().newTaintLocal(target, cos);
			// }
		} else {
			LogFileUtil.log(String.format("findViewById arg not int(%s)", arg.getClass()));
		}
	}

	@Override
	public String transfer(Constraint cons) {
		// TODO Auto-generated method stub
		return "";
	}

}
