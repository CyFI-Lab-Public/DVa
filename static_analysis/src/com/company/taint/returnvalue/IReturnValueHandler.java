package com.company.taint.returnvalue;

import com.company.data.RunTimeTaintData;
import soot.Local;

public interface IReturnValueHandler {
	public void handle(RunTimeTaintData rttd,Local locall);
}
