package com.company.taint.stmt;

import com.company.data.IRunTimeManager;
import com.company.data.RunTimePoint;
import com.company.data.RunTimeTaintData;
import com.company.symbolic.constraint.Constraint;
import com.company.symbolic.constraint.ConstraintSourceClassField;
import com.company.symbolic.constraint.IConstraintSource;
import com.company.taint.ProcessExpr;
import com.company.taint.ValueTag;
import com.company.util.MyLog;
import soot.*;
import soot.jimple.Expr;
import soot.jimple.InstanceFieldRef;
import soot.jimple.StaticFieldRef;
import soot.jimple.Stmt;
import soot.jimple.internal.JAssignStmt;

import java.util.ArrayList;
import java.util.List;
//import sun.jvm.hotspot.oops.Instance;

public class AssignStmtHandler implements IStmtHandler {
	RefType editTextType;

	public AssignStmtHandler() {
		editTextType = RefType.v("android.widget.EditText");
	}

	public void before(Value valuel, Value valuer, RunTimeTaintData rttd) {
		// init ImmValue
		if (valuel instanceof Local && rttd.getimmValues().containsKey((Local) valuel)) {
			rttd.getimmValues().put((Local) valuel, null);
		}
		if (valuel instanceof Local) {
			rttd.setHasTransfered((Local) valuel, false);
		}
	}

	@Override
	public void process(IRunTimeManager allinfo, Stmt tmp) {
		JAssignStmt stmt = (JAssignStmt) tmp;
		RunTimeTaintData rttd = allinfo.getRttd();

		// System.out.println(stmt + "/" + stmt.hashCode());
		// System.out.println(stmt.getRightOp().getClass());
		// System.out.println("**************************************************************");
		Value valuel = stmt.getLeftOp();
		Value valuer = stmt.getRightOp();
		MyLog.info(valuer + "\t\t" + valuer.getClass());
		before(valuel, valuer, rttd);

		List<RunTimeTaintData> separateRtts = new ArrayList<RunTimeTaintData>();

		if (valuer instanceof Expr) {
			ProcessExpr pExpr = new ProcessExpr(allinfo, (Local) valuel, rttd);

			//valuer.apply sets the constraint r1 --> imm number of findviewbyid argument value
			valuer.apply(pExpr);
			separateRtts = pExpr.mergeSeparateRtts(separateRtts);
		}
		// handles situation like: r0.abcd = r9
		else if ((valuer instanceof Local) && (valuel instanceof InstanceFieldRef)) {
			// if right side already has constraint, retrieve the constraint
			if (allinfo.getRttd().contains((Local)valuer)) {
				Constraint cons = new Constraint((Local) valuer);
				cons.setSource(allinfo.getRttd().getTaintNode((Local)valuer).getSource());

				// if left side not contained in the tInstanceFieldRef, Create one and copy the constraint over
				if (!allinfo.getRttd().contains(valuel.toString())) {
					allinfo.getRttd().newTaintInstanceFieldRefStr(valuel.toString(), cons);
				}
				// else, just update the constraint
				else {
					allinfo.getRttd().getTaintInstanceFieldRefStr(valuel.toString()).setSource(allinfo.getRttd().getTaintNode((Local)valuer).getSource());
				}
			}
			separateRtts.add(rttd);

		} else if (valuer instanceof InstanceFieldRef) {

			InstanceFieldRef ifr = (InstanceFieldRef) valuer;
			if (ifr.getType().equals(editTextType)) {
				// taint source A
				Constraint cos = new Constraint((Local) valuel);
				IConstraintSource consSource = new ConstraintSourceClassField();
				consSource.setSource(valuer);
				cos.setSource(consSource);
				allinfo.getRttd().hasSource = true;

				allinfo.getRttd().newTaintLocal((Local) valuel, cos);
			}
			else if ((valuel instanceof Local) && (allinfo.getRttd().contains(valuer.toString()))) {
				Constraint cons = allinfo.getRttd().getTaintInstanceFieldRefStr(valuer.toString());
				allinfo.getRttd().newTaintLocal((Local) valuel, cons);
			}
			separateRtts.add(rttd);
		} else if (valuer instanceof StaticFieldRef) {
			
			
		
			StaticFieldRef ifr = (StaticFieldRef) valuer;
			if (ifr.getType().toString().equals("java.util.regex.Pattern") && ifr.getField().getDeclaringClass().toString().equals("android.util.Patterns")) {
				rttd.addTag((Local) valuel, new ValueTag("Patterns.Pattern", ifr.getField().getName()));
			}
			separateRtts.add(rttd);
		} else {
			separateRtts.add(rttd);
		}

		Body body = allinfo.getBody();
		// MyLog.info(body.getMethod() + " AssignStmtHandler " + stmt);
		Unit nextUnit = body.getUnits().getSuccOf(stmt);
		for (RunTimeTaintData tmprttd : separateRtts) {
			RunTimePoint tmprtp = new RunTimePoint(tmprttd, body, nextUnit);
			allinfo.getWatingQueue().push(tmprtp);
			if (valuel instanceof Local && !tmprttd.hasTransfered((Local) valuel)) {
				tmprttd.removeLocalConstraint((Local) valuel);
				// MyLog.info("#######remove " + valuel);
			}
		}
	}

}
