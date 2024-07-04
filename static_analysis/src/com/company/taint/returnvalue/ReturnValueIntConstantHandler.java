package com.company.taint.returnvalue;

import com.company.data.RunTimeTaintData;
import soot.Local;
import soot.jimple.Constant;

public class ReturnValueIntConstantHandler implements IReturnValueHandler {
	Constant returnVale;

	public ReturnValueIntConstantHandler(Constant returnVale) {
		this.returnVale = returnVale;
	}

	@Override
	public void handle(RunTimeTaintData rttd, Local locall) {
		// TODO Auto-generated method stub
		// set constant int value to locall
		rttd.getimmValues().put(locall, returnVale);
	}

}
