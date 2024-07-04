package com.company.symbolic.constraint;

import com.company.data.IRunTimeManager;
import com.company.taint.ITag;
import com.company.taint.ProcessExpr;
import com.company.taint.ValueTag;
import com.company.util.MyLog;
import soot.Local;
import soot.Value;
import soot.jimple.InvokeExpr;
import soot.jimple.StringConstant;

import java.util.List;

public class IZ3Transfer_Pattern_compile implements IZ3Transfer {

	static String tagName = "Patterns.Pattern";

	@Override
	public void handle(IRunTimeManager rtm, ProcessExpr pExpr, InvokeExpr invokeExpr, Local target, Local base, String sign, List<Value> args) {
		// TODO Auto-generated method stub
		Value arg0 = args.get(0);
		if (arg0 instanceof StringConstant && rtm.getRttd().contains(base)) {
			
			StringConstant argStr = (StringConstant) arg0;
			
			rtm.getRttd().addTag(target, new ValueTag(tagName, argStr.value));


		} else {
			MyLog.info("Pattern_compile", "Not StringConstant arg " + args.get(0));
		}
	}

	@Override
	public String transfer(Constraint cons) {
		// TODO Auto-generated method stub
		String source = cons.getSource().toZ3();
		ITag tag = cons.getTag(tagName);

		String target = "";
		if (tag.getData() instanceof String) {
			target = tag.getData().toString();
		} else if (tag.getData() instanceof IConstraintSource) {
			target = ((IConstraintSource) tag.getData()).toZ3();
		}
		return String.format("'%s' equals '%s' -- (%s)%s", source, target, cons.getCondition(), cons.isSatisfy());
	}

	public static void main(String[] ad) {
		System.out.println("" instanceof String);
		System.out.println("" instanceof Object);
	}

}
