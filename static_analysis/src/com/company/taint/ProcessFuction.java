package com.company.taint;

import com.company.data.RunTimeTaintData;
import com.company.data.SClassAndMethod;
import com.company.data.SMethodUnit;
import com.company.data.TaintedArg;
import soot.Body;

import java.util.ArrayList;
import java.util.List;

public class ProcessFuction {

	public static List<RunTimeTaintData> process(String cName, String mName) {
		SClassAndMethod scm = new SClassAndMethod(cName, mName);
		List<RunTimeTaintData> rts = process(scm);
		return rts;
	}

	// Initialize RunTimeTaintData, Tainted arguments list, process SootClassandMethods.
	public static List<RunTimeTaintData> process(SClassAndMethod scm) {
		RunTimeTaintData rttd = new RunTimeTaintData();
		List<TaintedArg> targs = new ArrayList<TaintedArg>();
		return process(rttd, scm, targs);
	}

	public static boolean checkLoopOrDeep(RunTimeTaintData rttd, SClassAndMethod scm) {
		for (SClassAndMethod tmp : rttd.getMethods()) {
			// System.out.println(" *******************************" +
			// tmp.toString());
			// System.out.println(" -------------------------------" +
			// scm.toString());
			if (tmp.eqTo(scm)) {
				System.out.println("loop!");
				// System.exit(0);
				return false;
			}
		}

		if (rttd.getMethods().size() > 10) {
			System.out.println("deep!");
			return false;
		}

		return true;
	}

	// Process SootClassandMethods
	public static List<RunTimeTaintData> process(RunTimeTaintData rttd, SClassAndMethod scm, List<TaintedArg> targs) {

		if (!checkLoopOrDeep(rttd, scm)) {
			return null;
		}

		System.out.println("******" + rttd + " " + scm.toString());

		// PPPPPPPPPPPPPPPPPPPPPPPPPP
		// units of current method
		rttd.newUnitTmp(new ArrayList<SMethodUnit>());
		// methods is stack of sootclassandmethod
		rttd.pushMethods(scm);
		// TaintedArgs is stack of list of tainted arguments
		rttd.pushTaintedArgs(targs);

		Body body = scm.getSm().retrieveActiveBody();
		List<RunTimeTaintData> rts = ProcessBody.process(rttd, body);

//		System.out.println("LINE68 After ProcessBody.process");
//		System.out.println(rts + "\n\n");

		for (RunTimeTaintData tmp : rts) {
			// PPPPPPPPPPPPPPPPPPPPPPPPPP
			tmp.addTmptoAllUnitAndClearIt();
			tmp.popMethod();
			tmp.popTaintedArgs();
		}

		return rts;
	}

	// Obsolete
	// public static List<RunTimeTaintData> process__(SClassAndMethod scm) {
	// // built Graph for this function
	// CompleteBlockGraph bbg = new
	// CompleteBlockGraph(scm.getSm().retrieveActiveBody());
	//
	// List<RunTimeTaintData> rts = new ArrayList<RunTimeTaintData>();
	// for (Block block : bbg.getHeads()) {
	// rts.addAll(ProcessBody.process__(new RunTimeTaintData(), bbg, block));
	// }
	// MyLog.info("---------------------------------------------------------------------");
	// MyLog.info("len: " + rts.size());
	// for (RunTimeTaintData tmp : rts) {
	// MyLog.info(tmp.getUnitCount() + "/" + tmp.getConstrains().size());
	// // for (OblseleteConstrain cons : tmp.getConstrains()) {
	// // MyLog.info(cons.toString());
	// // }
	// }
	// return rts;
	// }

	// public static void main(String[] args) {
	// Envir.load();
	// Envir.apkName = "/home/cszuo/Desktop/condition/classes.dex.jar";
	// InitSoot.init(Envir.apkName);
	//
	// List<RunTimeTaintData> rts =
	// process("com.vkontakte.android.LoginActivity", "void doAuth()");
	//
	// RunTimeTaintData target = null;
	// for (RunTimeTaintData tmp : rts) {
	// if (target == null || target.getUnitCount() < tmp.getUnitCount()) {
	// target = tmp;
	// }
	// }
	//
	// JSONObject obj = new JSONObject("{}");
	// JSONObject tmp = null;
	// // for (OblseleteConstrain cons : target.getConstrains()) {
	// // cons.addToString(obj);
	// // }
	// for (String id : obj.keySet()) {
	// tmp = obj.getJSONObject(id);
	// tmp.append("z3", Z3Util.checkSat());
	// tmp.append("z3", Z3Util.checkSat());
	// }
	// MyLog.info(obj.toString());
	// }
}
