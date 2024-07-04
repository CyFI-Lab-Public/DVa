package com.company.symbolic.constraint;

import com.company.data.IRunTimeManager;
import com.company.taint.ProcessExpr;
import org.json.JSONException;
import soot.Local;
import soot.Value;
import soot.jimple.InvokeExpr;

import java.util.List;

public interface IZ3Transfer {
	public void handle(IRunTimeManager rtm, ProcessExpr pExpr, InvokeExpr invokeExpr, Local target, Local base, String sign, List<Value> args);

	public String transfer(Constraint cons) throws JSONException;
}
