package com.company.taint.stmt;

import com.company.data.IRunTimeManager;
import com.company.data.RunTimePoint;
import com.company.data.RunTimeTaintData;
import com.company.util.ConditionExprUtil;
import com.company.util.MyLog;
import soot.Body;
import soot.Local;
import soot.Unit;
import soot.jimple.ConditionExpr;
import soot.jimple.Stmt;
import soot.jimple.internal.JIfStmt;

public class IfStmtHandler implements IStmtHandler {

	@Override
	public void process(IRunTimeManager allinfo, Stmt tmp) {
		JIfStmt ifStmt = (JIfStmt) tmp;
		RunTimeTaintData rttd = allinfo.getRttd();

		// MyLog.info(ifStmt.getCondition() + "\t\t" +
		// ifStmt.getCondition().getClass());

		ConditionExpr condition = (ConditionExpr) ifStmt.getCondition();

		if (rttd.countofUnit(allinfo.getBody().getMethod(), ifStmt) > 1) {
			MyLog.info("skip loop");
			// skip loop
			Unit UnitTure = ifStmt.getTarget();
			Unit UnitFalse = allinfo.getBody().getUnits().getSuccOf(ifStmt);

			int countofUnitTure = rttd.countofUnit(allinfo.getBody().getMethod(), UnitTure);
			int countofUnitFalse = rttd.countofUnit(allinfo.getBody().getMethod(), UnitFalse);

			if (countofUnitTure > countofUnitFalse) {
				goFalseWay(allinfo, ifStmt, rttd);
			} else {
				goTrueWay(allinfo, ifStmt, rttd);
			}
			// else {
			// LogFileUtil.log("Total confused by IfStmtHandler-process-skipLoop
			// of " + allinfo.getBody().getMethod());
			// System.exit(0);
			// }

		} else if (ConditionExprUtil.canHandle(rttd, condition)) {
			MyLog.info("canHandle");
			if (ConditionExprUtil.satisfy(rttd, condition)) {
				// MyLog.info("satisfy go goTrueWay");
				goTrueWay(allinfo, ifStmt, rttd);
			} else {
				// MyLog.info("satisfy go goFalseWay");
				goFalseWay(allinfo, ifStmt, rttd);
			}
		} else {

			if (condition.getOp1() instanceof Local && rttd.contains((Local) condition.getOp1())) {
				MyLog.info("contains");

				RunTimeTaintData rttdTrue = goTrueWay(allinfo, ifStmt, rttd);
				// Use clone of rttd! for two ways of if stmt
				RunTimeTaintData rttdFalse = goFalseWay(allinfo, ifStmt, rttd.clone());

				Local condiLocal = (Local) condition.getOp1();
				rttdTrue.getTaintNode(condiLocal).setSatisfy(true);
				rttdTrue.getTaintNode(condiLocal).setCondition(condition, rttdTrue.getTags(condiLocal));
				rttdTrue.addConstrain(rttdTrue.getTaintNode(condiLocal).clone(condiLocal));

				rttdFalse.getTaintNode(condiLocal).setSatisfy(false);
				rttdFalse.getTaintNode(condiLocal).setCondition(condition, rttdTrue.getTags(condiLocal));
				rttdFalse.addConstrain(rttdFalse.getTaintNode(condiLocal).clone(condiLocal));
			} else {
				MyLog.info("goFalseWay");
				goFalseWay(allinfo, ifStmt, rttd);
			}
		}
	}

	private RunTimeTaintData goTrueWay(IRunTimeManager allinfo, JIfStmt ifStmt, RunTimeTaintData rttd) {
		MyLog.info("goTrueWay");
		Body body = allinfo.getBody();
		Unit nextUnit = ifStmt.getTarget();
		// MyLog.info("Next " + nextUnit);
		RunTimePoint tmprtp = new RunTimePoint(rttd, body, nextUnit);
		allinfo.getWatingQueue().push(tmprtp);
		return rttd;
	}

	private RunTimeTaintData goFalseWay(IRunTimeManager allinfo, JIfStmt ifStmt, RunTimeTaintData rttd) {
		MyLog.info("goFalseWay");
		Body body = allinfo.getBody();
		Unit nextUnit = body.getUnits().getSuccOf(ifStmt);
		// MyLog.info("Next " + nextUnit);
		RunTimePoint tmprtp = new RunTimePoint(rttd, body, nextUnit);
		allinfo.getWatingQueue().push(tmprtp);
		return rttd;
	}
}
