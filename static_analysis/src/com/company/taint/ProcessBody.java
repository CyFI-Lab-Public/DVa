package com.company.taint;

import com.company.data.RunTimePoint;
import com.company.data.RunTimeTaintData;
import com.company.data.SMethodUnit;
import com.company.taint.stmt.DefaultStmtHandler;
import com.company.util.LogFileUtil;
import com.company.util.MyLog;
import soot.Body;
import soot.Local;
import soot.PatchingChain;
import soot.Unit;
import soot.jimple.IfStmt;
import soot.jimple.ReturnStmt;
import soot.jimple.ReturnVoidStmt;
import soot.jimple.Stmt;
import soot.jimple.internal.AbstractJimpleIntBinopExpr;
import soot.toolkits.graph.Block;
import soot.toolkits.graph.CompleteBlockGraph;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Stack;

public class ProcessBody {

	public static List<RunTimeTaintData> process(RunTimeTaintData runtime, Body body) {

		List<RunTimeTaintData> rts = new ArrayList<RunTimeTaintData>();

		PatchingChain<Unit> units = body.getUnits();

		for (Unit unit : units) {
			if (unit.toString().contains("goto [?= $r5 = r0.<com.android.volley.CacheDispatcher: java.util.concurrent.BlockingQueue mCacheQueue>]")) {
				break;
			}
		}

		// Runtimepoint is rttd, one body and one unit
		Stack<RunTimePoint> watingQueue = new Stack<RunTimePoint>();
		watingQueue.add(new RunTimePoint(runtime, body, units.getFirst()));

		RunTimePoint rtp;
		Unit unit;
		//System.out.println("Before entering waiting queue loop");
		while (!watingQueue.isEmpty()) {
			//System.out.println("waiting queue");
			rtp = watingQueue.pop();
			unit = rtp.getUnit();

			// add unit to Runtime
			rtp.getRttd().addUnittoTmp(new SMethodUnit(body.getMethod(), unit));
			//main print, printing rttd, body.method, unit
			MyLog.resrult(rtp.getRttd() + " " + body.getMethod() + " ==>> " + unit);// +
																					// "\t\t"
																					// +
																					// unit.getClass());
			//System.out.println(rtp.getRttd() + " " + body.getMethod() + " ==>> " + unit);
			try {
				unit.apply(DefaultStmtHandler.getInstance(rts, body, watingQueue, rtp));
			} catch (Exception e) {
				e.printStackTrace();
			}
			//System.out.println("LINE55");
		}
		//System.out.println("\n\n\nexiting waiting queue loop");
		return rts;
	}

	public static List<RunTimeTaintData> process__(RunTimeTaintData runtime, CompleteBlockGraph bg, Block block) {
		Unit tail = block.getTail();
		for (Unit u : block) {
			if (u == tail)
				break;
			// u.apply(DefaultStmtHandler.getInstance(runtime));
		}
		if (tail instanceof IfStmt) {
			IfStmt iftail = (IfStmt) tail;
			AbstractJimpleIntBinopExpr condition = (AbstractJimpleIntBinopExpr) iftail.getCondition();
			Local condiLocal = (Local) condition.getOp1();

			RunTimeTaintData runtimeTrue = runtime.clone();
			RunTimeTaintData runtimeFalse = runtime.clone();
			MyLog.info(runtime.contains(condiLocal) + "/" + condiLocal);
			if (runtime.contains(condiLocal)) {
				// OblseleteConstrain consTrue = new OblseleteConstrain(tail,
				// condition, true, runtime.getTaintNode(condiLocal).clone());
				// runtimeTrue.addConstrain(consTrue);

				// OblseleteConstrain consFalse = new OblseleteConstrain(tail,
				// condition, false, runtime.getTaintNode(condiLocal).clone());
				// runtimeFalse.addConstrain(consFalse);
			}

			Block blockTrue = getSuccsor(bg, block, iftail.getTarget(), true);
			Block blockFalse = getSuccsor(bg, block, iftail.getTarget(), false);

			List<RunTimeTaintData> rts = process__(runtimeTrue, bg, blockTrue);
			rts.addAll(process__(runtimeFalse, bg, blockFalse));

			return rts;

		} else if (tail instanceof ReturnStmt) {
			return new ArrayList<>(Arrays.asList(runtime));
		} else if (tail instanceof ReturnVoidStmt) {
			return new ArrayList<>(Arrays.asList(runtime));
		} else {
			LogFileUtil.log(String.format("last statment of Block is not IfStmt it is (%s) (%s) ", tail.toString(), tail.getClass()));
		}
		return new ArrayList<RunTimeTaintData>();
	}

	public static Block getSuccsor(CompleteBlockGraph bg, Block block, Stmt stmt, boolean satisfy) {
		List<Block> bs = bg.getSuccsOf(block);
		for (Block b : bs) {
			if (satisfy) {
				if (b.getHead() == stmt) {
					return b;
				}
			} else {
				if (b.getHead() != stmt) {
					return b;
				}
			}
		}
		return null;
	}
}
