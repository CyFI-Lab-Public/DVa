package com.company.taint.stmt;

import com.company.data.IRunTimeManager;
import com.company.data.RunTimeTaintData;
import com.company.taint.returnvalue.ReturnValueVoidHandler;
import soot.jimple.Stmt;

public class ReturnVoidStmtHandler implements IStmtHandler {

	@Override
	public void process(IRunTimeManager allinfo, Stmt tmp) {

		RunTimeTaintData rttd = allinfo.getRttd();

		rttd.setReturnValueHandler(new ReturnValueVoidHandler());

		allinfo.getRts().add(rttd);
	}

}
