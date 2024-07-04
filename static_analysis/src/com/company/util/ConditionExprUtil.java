package com.company.util;

import com.company.data.RunTimeTaintData;
import soot.Local;
import soot.jimple.*;

public class ConditionExprUtil {

	public static boolean canHandle(RunTimeTaintData rttd, ConditionExpr cond) {
		try {
			Local op1 = (Local) cond.getOp1();
			Constant op2 = (Constant) cond.getOp2();
			Constant c1 = rttd.getimmValues().get(op1);
			return op2.getClass().equals(c1.getClass());
		} catch (Exception e) {

		}
		return false;
	}

	public static boolean satisfy(RunTimeTaintData rttd, ConditionExpr cond) {
		Local op1 = (Local) cond.getOp1();
		Constant op2 = (Constant) cond.getOp2();
		Constant c1 = rttd.getimmValues().get(op1);
		if (c1 instanceof IntConstant) {
			return satisfy((IntConstant) c1, cond.getSymbol(), (IntConstant) op2);
		} else if (c1 instanceof NullConstant) {
			return satisfy((NullConstant) c1, cond.getSymbol(), (NullConstant) op2);
		} else {
			LogFileUtil.log("ConditionExpr args do not match " + cond);
		}
		return false;
	}

	public static boolean satisfy(IntConstant op1, String symbo, IntConstant op2) {
		// MyLog.info(op1 + " " + symbo + " " + op2);
		symbo = symbo.trim();
		NumericConstant res = IntConstant.v(0);
		if (symbo.equals("==")) {
			res = op1.equalEqual(op2);
		} else if (symbo.equals("!=")) {
			res = op1.notEqual(op2);
		} else if (symbo.equals("<")) {
			res = op1.lessThan(op2);
		} else if (symbo.equals("<=")) {
			res = op1.lessThanOrEqual(op2);
		} else if (symbo.equals(">")) {
			res = op1.greaterThan(op2);
		} else if (symbo.equals(">=")) {
			res = op1.greaterThanOrEqual(op2);
		}
		// MyLog.info(res.equivTo(IntConstant.v(1)));
		return res.equivTo(IntConstant.v(1));
	}

	public static boolean satisfy(NullConstant op1, String symbo, NullConstant op2) {
		symbo = symbo.trim();
		if (symbo.equals("==")) {
			return true;
		} else {
			return false;
		}
	}
}
