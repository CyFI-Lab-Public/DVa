package com.company.taint.stmt;

import com.company.data.IRunTimeManager;
import com.company.data.RunTimePoint;
import com.company.data.RunTimeTaintData;
import com.company.data.TaintedArg;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.Value;
import soot.jimple.ParameterRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JIdentityStmt;

public class IdentityStmtHandler implements IStmtHandler {

	@Override
	public void process(IRunTimeManager allinfo, Stmt tmp) {
		JIdentityStmt stmt = (JIdentityStmt) tmp;
		RunTimeTaintData rttd = allinfo.getRttd();

		Value valuel = stmt.getLeftOp();
		Value valuer = stmt.getRightOp();

		// @Modified here to handle function parameter taint analysis
		// currently only supports string constraints
		// @todo: add support for other types of constraints, network constraints, format output logs
		if (valuel instanceof Local) {
			rttd.removeLocalConstraint((Local) valuel);
			if (valuer instanceof ParameterRef)
//			{
//				Constraint cos = new Constraint((Local)valuel);
//				IConstraintSource consSource = new ConstraintSourcePara();
//				consSource.setSource((ParameterRef) valuer);
//				cos.setSource(consSource);
//				rttd.hasSource = true;
//				rttd.newTaintLocal((Local)valuel, cos);
//			}
			rttd.getimmValues().put((Local) valuel, null);
			// MyLog.info("#######remove " + valuel);
		}

		if (valuer instanceof ParameterRef) {

			ParameterRef parameter = (ParameterRef) valuer;
			Local locall = (Local) valuel;
			for (TaintedArg ta : rttd.getCurrentTaintedArgs()) {
				if (ta.getArgIndex() == parameter.getIndex()) {
					rttd.transfer(locall, ta.getSourceLocal());
				}
			}
		}

		Body body = allinfo.getBody();
		Unit nextUnit = body.getUnits().getSuccOf(stmt);
		RunTimePoint tmprtp = new RunTimePoint(rttd, body, nextUnit);
		allinfo.getWatingQueue().push(tmprtp);
	}

}
