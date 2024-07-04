package com.company.taint.stmt;

import com.company.data.*;
import com.company.symbolic.MethodBoundaryChecker;
import com.company.symbolic.constraint.Constraint;
import com.company.taint.ProcessFuction;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.ValueBox;
import soot.jimple.InvokeExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.JInvokeStmt;

import java.util.ArrayList;
import java.util.List;

public class InvokeStmtHandler implements IStmtHandler {

	@Override
	public void process(IRunTimeManager allinfo, Stmt tmp) {
		JInvokeStmt stmt = (JInvokeStmt) tmp;
		RunTimeTaintData rttd = allinfo.getRttd();

		boolean should = MethodBoundaryChecker.shoulProcessInvokeExpr(stmt.getInvokeExpr());
		if (should) {
			InvokeExpr iexpr = tmp.getInvokeExpr();
			SClassAndMethod scm = new SClassAndMethod(iexpr.getMethod());
			Local tmpl;
			List<TaintedArg> args = new ArrayList<TaintedArg>();
			int len = iexpr.getArgs().size();
			for (int i = 0; i < len; i++) {
				if (iexpr.getArgs().get(i) instanceof Local) {
					tmpl = (Local) iexpr.getArgs().get(i);
					if (rttd.contains(tmpl)) {
						args.add(new TaintedArg(i, tmpl));
					}
				}
			}

			List<RunTimeTaintData> tmprts = ProcessFuction.process(rttd, scm, args);

			Body body = allinfo.getBody();
			Unit nextUnit = body.getUnits().getSuccOf(tmp);
			RunTimePoint tmprtp;

			if (tmprts != null) {
				for (RunTimeTaintData tmprttd : tmprts) {
					tmprtp = new RunTimePoint(tmprttd, body, nextUnit);
					allinfo.getWatingQueue().push(tmprtp);
				}
			} else {
				tmprtp = new RunTimePoint(rttd, body, nextUnit);
				allinfo.getWatingQueue().push(tmprtp);
			}
		} else {
			// Modified to map button ID to its activity/sub activity ID
			List<ValueBox> valueBoxes = stmt.getInvokeExprBox().getValue().getUseBoxes();
			String methodString = stmt.toString();
			if (methodString.contains("setOnClickListener"))
			{
				try {
					Local baseBox = (Local) valueBoxes.get(1).getValue();
					Constraint buttonIDConstraint = rttd.getTaintNode(baseBox);
					String buttonID = buttonIDConstraint.getSource().toZ3();

					Local argBox = (Local) valueBoxes.get(0).getValue();
					String referClass = argBox.getType().toString();
					allinfo.getRttd().addButtonToClass(buttonID, referClass);
				} catch (Exception e) {

				}
			}

			Body body = allinfo.getBody();
			Unit nextUnit = body.getUnits().getSuccOf(tmp);
			RunTimePoint tmprtp = new RunTimePoint(rttd, body, nextUnit);
			allinfo.getWatingQueue().push(tmprtp);
		}
	}

}
