package com.company.taint.stmt;

import com.company.data.IRunTimeManager;
import com.company.data.RunTimeTaintData;
import com.company.taint.returnvalue.ReturnValueIntConstantHandler;
import com.company.taint.returnvalue.ReturnValueLocalHandler;
import com.company.util.MyLog;
import soot.Local;
import soot.Value;
import soot.jimple.Constant;
import soot.jimple.ReturnStmt;
import soot.jimple.Stmt;

public class ReturnStmtHandler implements IStmtHandler {

	@Override
	public void process(IRunTimeManager allinfo, Stmt tmp) {
		ReturnStmt rstmt = (ReturnStmt) tmp;
		RunTimeTaintData rttd = allinfo.getRttd();
		Value op = rstmt.getOp();
		Local returnLocal = null;
		if (op instanceof Local) {

			returnLocal = (Local) op;

			if (rttd.getimmValues().containsKey(returnLocal)) {
				rttd.setReturnValueHandler(new ReturnValueIntConstantHandler(rttd.getimmValues().get(returnLocal)));
				//System.out.println("YES");
				//System.exit(0);
			} else {
				rttd.setReturnValueHandler(new ReturnValueLocalHandler(returnLocal));
			}

		} else if (op instanceof Constant) {
			rttd.setReturnValueHandler(new ReturnValueIntConstantHandler((Constant) op));
			// MyLog.info("??????????????" + op);
		} else {
			MyLog.info("ReturnStmtHandler", "Unsuported return arg " + op.getClass());
		}

		allinfo.getRts().add(rttd);
	}

}
