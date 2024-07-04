package com.company.taint.stmt;

import com.company.data.IRunTimeManager;
import com.company.data.RunTimePoint;
import com.company.data.RunTimeTaintData;
import soot.Body;
import soot.Unit;
import soot.jimple.*;

import java.util.List;
import java.util.Stack;

public class DefaultStmtHandler extends AbstractStmtSwitch implements IRunTimeManager {

	List<RunTimeTaintData> rts;
	Body body;
	Stack<RunTimePoint> watingQueue;
	RunTimePoint trt;
	RunTimeTaintData rttd;

	// static DefaultStmtHandler dstmt = new DefaultStmtHandler();

	public static DefaultStmtHandler getInstance(List<RunTimeTaintData> rts, Body body, Stack<RunTimePoint> watingQueue, RunTimePoint trt) {
		DefaultStmtHandler dstmt = new DefaultStmtHandler();
		dstmt.setRts(rts);
		dstmt.setBody(body);
		dstmt.setWatingQueue(watingQueue);
		dstmt.setTrt(trt);
		dstmt.setRttd(trt.getRttd());
		return dstmt;
	}

	AssignStmtHandler aStmt;
	IdentityStmtHandler idStmt;
	InvokeStmtHandler inStmt;
	ReturnStmtHandler reStmt;
	ReturnVoidStmtHandler revStmt;
	IfStmtHandler ifStmt;
	GotoStmtHandler gotoStmt;

	private DefaultStmtHandler() {
		this.aStmt = new AssignStmtHandler();
		this.idStmt = new IdentityStmtHandler();
		this.inStmt = new InvokeStmtHandler();
		this.reStmt = new ReturnStmtHandler();
		this.revStmt = new ReturnVoidStmtHandler();
		this.ifStmt = new IfStmtHandler();
		this.gotoStmt = new GotoStmtHandler();

	}

	@Override
	public RunTimeTaintData getRttd() {
		return rttd;
	}

	private void setRttd(RunTimeTaintData rttd) {
		this.rttd = rttd;
	}

	@Override
	public List<RunTimeTaintData> getRts() {
		return rts;
	}

	private void setRts(List<RunTimeTaintData> rts) {
		this.rts = rts;
	}

	@Override
	public Body getBody() {
		return body;
	}

	private void setBody(Body body) {
		this.body = body;
	}

	@Override
	public Stack<RunTimePoint> getWatingQueue() {
		return watingQueue;
	}

	private void setWatingQueue(Stack<RunTimePoint> watingQueue) {
		this.watingQueue = watingQueue;
	}

	@Override
	public RunTimePoint getTrt() {
		return trt;
	}

	private void setTrt(RunTimePoint trt) {
		this.trt = trt;
	}

	@Override
	public void caseAssignStmt(AssignStmt stmt) {
		// TODO Auto-generated method stub
		aStmt.process(this, stmt);
		super.caseAssignStmt(stmt);
	}

	@Override
	public void caseBreakpointStmt(BreakpointStmt stmt) {
		// TODO Auto-generated method stub
		goNextUnit(stmt);
		super.caseBreakpointStmt(stmt);
	}

	@Override
	public void caseEnterMonitorStmt(EnterMonitorStmt stmt) {
		// TODO Auto-generated method stub
		goNextUnit(stmt);
		super.caseEnterMonitorStmt(stmt);
	}

	@Override
	public void caseExitMonitorStmt(ExitMonitorStmt stmt) {
		// TODO Auto-generated method stub
		goNextUnit(stmt);
		super.caseExitMonitorStmt(stmt);
	}

	@Override
	public void caseGotoStmt(GotoStmt stmt) {
		// TODO Auto-generated method stub
		gotoStmt.process(this, stmt);
		super.caseGotoStmt(stmt);
	}

	@Override
	public void caseIdentityStmt(IdentityStmt stmt) {
		// TODO Auto-generated method stub
		// method parameter should be handle here
		idStmt.process(this, stmt);
		super.caseIdentityStmt(stmt);
	}

	@Override
	public void caseIfStmt(IfStmt stmt) {
		// TODO Auto-generated method stub
		// System.out.println("IF:" + stmt);
		// System.out.println("condition:" + stmt.getCondition() + "/" +
		// stmt.getCondition().getClass());
		// System.out.println("condition:" + ((AbstractJimpleIntBinopExpr)
		// stmt.getCondition()).getOp1());
		// System.out.println("target:" + stmt.getTarget() + "/" +
		// stmt.hashCode());
		ifStmt.process(this, stmt);

		super.caseIfStmt(stmt);
	}

	@Override
	public void caseInvokeStmt(InvokeStmt stmt) {
		// TODO Auto-generated method stub
		inStmt.process(this, stmt);
		super.caseInvokeStmt(stmt);
	}

	@Override
	public void caseLookupSwitchStmt(LookupSwitchStmt stmt) {
		// TODO Auto-generated method stub
		goNextUnit(stmt);
		super.caseLookupSwitchStmt(stmt);
	}

	@Override
	public void caseNopStmt(NopStmt stmt) {
		// TODO Auto-generated method stub
		goNextUnit(stmt);
		super.caseNopStmt(stmt);
	}

	@Override
	public void caseRetStmt(RetStmt stmt) {
		// TODO Auto-generated method stub
		goNextUnit(stmt);
		super.caseRetStmt(stmt);
	}

	@Override
	public void caseReturnStmt(ReturnStmt stmt) {
		// TODO Auto-generated method stub
		reStmt.process(this, stmt);
		super.caseReturnStmt(stmt);
	}

	@Override
	public void caseReturnVoidStmt(ReturnVoidStmt stmt) {
		// TODO Auto-generated method stub
		revStmt.process(this, stmt);
		super.caseReturnVoidStmt(stmt);
	}

	@Override
	public void caseTableSwitchStmt(TableSwitchStmt stmt) {
		// TODO Auto-generated method stub
		goNextUnit(stmt);
		super.caseTableSwitchStmt(stmt);
	}

	@Override
	public void caseThrowStmt(ThrowStmt stmt) {
		// TODO Auto-generated method stub
		goNextUnit(stmt);
		super.caseThrowStmt(stmt);
	}

	private void goNextUnit(Unit stmt) {
		Unit nextUnit = body.getUnits().getSuccOf(stmt);
		RunTimePoint tmprtp = new RunTimePoint(rttd, body, nextUnit);
		getWatingQueue().push(tmprtp);
	}

}
