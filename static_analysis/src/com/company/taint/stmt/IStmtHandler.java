package com.company.taint.stmt;

import com.company.data.IRunTimeManager;
import soot.jimple.Stmt;

public interface IStmtHandler {
	abstract void process(IRunTimeManager allinfo, Stmt stmt);
}
