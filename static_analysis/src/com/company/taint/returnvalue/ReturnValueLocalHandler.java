package com.company.taint.returnvalue;

import com.company.data.RunTimeTaintData;
import soot.Local;

public class ReturnValueLocalHandler implements IReturnValueHandler {
	Local returnVale;

	public ReturnValueLocalHandler(Local returnVale) {
		this.returnVale = returnVale;
	}

	@Override
	public void handle(RunTimeTaintData rttd, Local locall) {
		// TODO Auto-generated method stub
		rttd.transfer(locall, returnVale);
	}

}
