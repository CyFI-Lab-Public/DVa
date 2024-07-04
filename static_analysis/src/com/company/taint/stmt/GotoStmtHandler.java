package com.company.taint.stmt;

import com.company.data.IRunTimeManager;
import com.company.data.RunTimePoint;
import com.company.data.RunTimeTaintData;
import soot.Body;
import soot.Unit;
import soot.jimple.Stmt;
import soot.jimple.internal.JGotoStmt;

public class GotoStmtHandler implements IStmtHandler {

	@Override
	public void process(IRunTimeManager allinfo, Stmt tmp) {
		JGotoStmt gotoStmt = (JGotoStmt) tmp;

		RunTimeTaintData rttd = allinfo.getRttd();

		Body body = allinfo.getBody();
		Unit nextUnit = gotoStmt.getTarget();

		// modified to avoid infinite loop and while true loop
		String nextUnitStr = nextUnit.toString();
		if (allinfo.getRttd().visiting(nextUnitStr)) {
			RunTimePoint tmprtp = new RunTimePoint(rttd, body, nextUnit);
			allinfo.getWatingQueue().push(tmprtp);
		}
		else {
			Unit nU = body.getUnits().getSuccOf(tmp);
			RunTimePoint tmprtp = new RunTimePoint(rttd, body, nU);
			allinfo.getWatingQueue().push(tmprtp);
		}
	}

}
