package com.company.symbolic;

import com.company.data.InternetTag;
import com.company.data.RunTimeTaintData;
import com.company.util.MyLog;
import soot.Local;
import soot.SootClass;
import soot.SootMethod;
import soot.Value;
import soot.jimple.InvokeExpr;

import java.util.ArrayList;
import java.util.List;

public class MethodBoundaryChecker {

	static String OUTBOUNDARYINFO0 = "Method %s is out of Boundary because of no Internet";
	static String OUTBOUNDARYINFO1 = "Method %s is out of Boundary because of no Internet and no Taint";
	static String OUTBOUNDARYINFO2 = "Method %s is out of Boundary because of lib class";
	static String OUTBOUNDARYINFO3 = "Method %s is out of Boundary because of excludeList";
	static String INBOUNDARYINFO = "Method %s is inside of Boundary because of Internet(%s), Taint(%s)";

	static List<String> excludeList = new ArrayList<String>();

	static {
		excludeList.add("com.facebook.");
		excludeList.add("android.support.");
		excludeList.add("com.google.android.");
	}

	public static boolean inExcludeList(SootClass sc) {
		String packageName = sc.getPackageName();
		for (String str : excludeList) {
			if (packageName.startsWith(str)) {
				// System.out.println(sc.getName());
				// System.out.println(packageName);
				// System.out.println(str);
				// System.exit(0);
				return true;
			}
		}
		return false;
	}

	public static boolean shoulProcessInvokeExpr(InvokeExpr invExpr) {
		SootMethod sm = invExpr.getMethod();
		String sign = sm.getSignature();

		if (inExcludeList(sm.getDeclaringClass())) {
			MyLog.info("MethodBoundaryChecker", String.format(OUTBOUNDARYINFO3, sign));
			return false;
		}

		boolean islibclass = sm.getDeclaringClass().isLibraryClass();
		if (islibclass) {
			MyLog.info("MethodBoundaryChecker", String.format(OUTBOUNDARYINFO2, sign));
			return false;
		}

		boolean yes = sm.hasTag(InternetTag.name());
		if (!yes) {
			MyLog.info("MethodBoundaryChecker", String.format(OUTBOUNDARYINFO0, sign));
		}
		return yes;
	}

	public static boolean shoulProcessInvokeExpr(RunTimeTaintData rttd, InvokeExpr invExpr, List<Value> args) {
		SootMethod sm = invExpr.getMethod();
		String sign = sm.getSignature();

		if (inExcludeList(sm.getDeclaringClass())) {
			MyLog.info("MethodBoundaryChecker", String.format(OUTBOUNDARYINFO3, sign));
			return false;
		}

		boolean islibclass = sm.getDeclaringClass().isLibraryClass();
		if (islibclass) {
			MyLog.info("MethodBoundaryChecker", String.format(OUTBOUNDARYINFO2, sign));
			return false;
		}

		boolean yes_internet = sm.hasTag(InternetTag.name());
		boolean yes_taint = false;
		if (!yes_internet) {
			for (Value value : args) {
				if (value instanceof Local && rttd.contains((Local) value)) {
					yes_taint = true;
					break;
				}
			}
		}
		if (yes_internet || yes_taint) {
			MyLog.info("MethodBoundaryChecker", String.format(INBOUNDARYINFO, sign, yes_internet, yes_taint));
		} else {
			MyLog.info("MethodBoundaryChecker", String.format(OUTBOUNDARYINFO1, sign));
		}
		return yes_internet || yes_taint;
	}
}
