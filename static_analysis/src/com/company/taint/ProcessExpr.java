package com.company.taint;

import com.company.data.IRunTimeManager;
import com.company.data.RunTimeTaintData;
import com.company.data.SClassAndMethod;
import com.company.data.TaintedArg;
import com.company.symbolic.MethodBoundaryChecker;
import com.company.symbolic.constraint.JavaMethodsHandler;
import com.company.util.MyLog;
import soot.Local;
import soot.Value;
import soot.jimple.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ProcessExpr extends AbstractExprSwitch {

	Local local = null;
	RunTimeTaintData rttd;
	IRunTimeManager rtm;

	public ProcessExpr(IRunTimeManager rtm, Local valuel, RunTimeTaintData rttd) {
		setLeftV(valuel);
		this.rtm = rtm;
		this.rttd = rttd;
	}

	public void setLeftV(Local valuel) {
		this.local = valuel;
	}

	List<RunTimeTaintData> separateRtts = null;

	public List<RunTimeTaintData> mergeSeparateRtts(List<RunTimeTaintData> tmp) {
		if (separateRtts != null) {
			tmp.addAll(separateRtts);
		} else {
			tmp.add(rttd);
		}
		return tmp;
	}

	public void setSeparateRtts(List<RunTimeTaintData> separateRtts) {
		this.separateRtts = separateRtts;
	}

	@Override
	public void caseAddExpr(AddExpr v) {
		// TODO Auto-generated method stub
		super.caseAddExpr(v);
	}

	@Override
	public void caseAndExpr(AndExpr v) {
		// TODO Auto-generated method stub
		super.caseAndExpr(v);
	}

	@Override
	public void caseCastExpr(CastExpr v) {
		// TODO Auto-generated method stub
		MyLog.info(v.getOp() + " ? " + rttd.contains((Local) v.getOp()));
		Local from = (Local) v.getOp();
		rttd.transfer(local, from);
		MyLog.info(local + " ? " + rttd.contains(local));
	}

	@Override
	public void caseCmpExpr(CmpExpr v) {
		// TODO Auto-generated method stub
		super.caseCmpExpr(v);
	}

	@Override
	public void caseCmpgExpr(CmpgExpr v) {
		// TODO Auto-generated method stub
		super.caseCmpgExpr(v);
	}

	@Override
	public void caseCmplExpr(CmplExpr v) {
		// TODO Auto-generated method stub
		super.caseCmplExpr(v);
	}

	@Override
	public void caseDivExpr(DivExpr v) {
		// TODO Auto-generated method stub
		super.caseDivExpr(v);
	}

	@Override
	public void caseDynamicInvokeExpr(DynamicInvokeExpr v) {
		// TODO Auto-generated method stub
		super.caseDynamicInvokeExpr(v);
	}

	@Override
	public void caseEqExpr(EqExpr v) {
		// TODO Auto-generated method stub
		super.caseEqExpr(v);
	}

	@Override
	public void caseGeExpr(GeExpr v) {
		// TODO Auto-generated method stub
		super.caseGeExpr(v);
	}

	@Override
	public void caseGtExpr(GtExpr v) {
		// TODO Auto-generated method stub
		super.caseGtExpr(v);
	}

	@Override
	public void caseInstanceOfExpr(InstanceOfExpr v) {
		// TODO Auto-generated method stub
		super.caseInstanceOfExpr(v);
	}

	@Override
	public void caseInterfaceInvokeExpr(InterfaceInvokeExpr v) {
		// TODO Auto-generated method stub
		Local base = (Local) v.getBase();
		String sign = v.getMethod().getSignature();
		List<Value> args = v.getArgs();
		handleExpr(v, base, sign, args);
	}

	@Override
	public void caseLeExpr(LeExpr v) {
		// TODO Auto-generated method stub
		super.caseLeExpr(v);
	}

	@Override
	public void caseLengthExpr(LengthExpr v) {
		// TODO Auto-generated method stub
		super.caseLengthExpr(v);
	}

	@Override
	public void caseLtExpr(LtExpr v) {
		// TODO Auto-generated method stub
		super.caseLtExpr(v);
	}

	@Override
	public void caseMulExpr(MulExpr v) {
		// TODO Auto-generated method stub
		super.caseMulExpr(v);
	}

	@Override
	public void caseNeExpr(NeExpr v) {
		// TODO Auto-generated method stub
		super.caseNeExpr(v);
	}

	@Override
	public void caseNegExpr(NegExpr v) {
		// TODO Auto-generated method stub
		super.caseNegExpr(v);
	}

	@Override
	public void caseNewArrayExpr(NewArrayExpr v) {
		// TODO Auto-generated method stub
		super.caseNewArrayExpr(v);
	}

	@Override
	public void caseNewExpr(NewExpr v) {
		// TODO Auto-generated method stub
		super.caseNewExpr(v);
	}

	@Override
	public void caseNewMultiArrayExpr(NewMultiArrayExpr v) {
		// TODO Auto-generated method stub
		super.caseNewMultiArrayExpr(v);
	}

	@Override
	public void caseOrExpr(OrExpr v) {
		// TODO Auto-generated method stub
		super.caseOrExpr(v);
	}

	@Override
	public void caseRemExpr(RemExpr v) {
		// TODO Auto-generated method stub
		super.caseRemExpr(v);
	}

	@Override
	public void caseShlExpr(ShlExpr v) {
		// TODO Auto-generated method stub
		super.caseShlExpr(v);
	}

	@Override
	public void caseShrExpr(ShrExpr v) {
		// TODO Auto-generated method stub
		super.caseShrExpr(v);
	}

	@Override
	public void caseSpecialInvokeExpr(SpecialInvokeExpr v) {
		// TODO Auto-generated method stub
		Local base = (Local) v.getBase();
		String sign = v.getMethod().getSignature();
		List<Value> args = v.getArgs();
		handleExpr(v, base, sign, args);
	}

	@Override
	public void caseStaticInvokeExpr(StaticInvokeExpr v) {
		// TODO Auto-generated method stub
		Local base = null;
		String sign = v.getMethod().getSignature();
		List<Value> args = v.getArgs();
		handleExpr(v, base, sign, args);
	}

	@Override
	public void caseSubExpr(SubExpr v) {
		// TODO Auto-generated method stub
		super.caseSubExpr(v);
	}

	@Override
	public void caseUshrExpr(UshrExpr v) {
		// TODO Auto-generated method stub
		super.caseUshrExpr(v);
	}

	@Override
	public void caseVirtualInvokeExpr(VirtualInvokeExpr v) {
		Local base = (Local) v.getBase();
		String sign = v.getMethod().getSignature();
		List<Value> args = v.getArgs();
		handleExpr(v, base, sign, args);
	}

	@Override
	public void caseXorExpr(XorExpr v) {
		// TODO Auto-generated method stub
		super.caseXorExpr(v);
	}

	@Override
	public void defaultCase(Object obj) {
		// TODO Auto-generated method stub
		super.defaultCase(obj);
	}

	public void handleExpr(InvokeExpr invokeExpr, Local base, String sign, List<Value> args) {

		if (JavaMethodsHandler.contains(sign)) {
			JavaMethodsHandler.getHandler(sign).handle(rtm, this, invokeExpr, local, base, sign, args);
		} else {
			boolean should = MethodBoundaryChecker.shoulProcessInvokeExpr(rttd, invokeExpr, args);
			if (should) {
				// MyLog.info(invokeExpr);
				SClassAndMethod scm = new SClassAndMethod(invokeExpr.getMethod());
				Local tmpl;
				List<TaintedArg> targs = new ArrayList<TaintedArg>();
				int len = args.size();
				for (int i = 0; i < len; i++) {
					if (args.get(i) instanceof Local) {
						tmpl = (Local) args.get(i);
						if (rttd.contains(tmpl)) {
							targs.add(new TaintedArg(i, tmpl));
						}
					}
				}
				// MyLog.info("Go In To Function");
				List<RunTimeTaintData> tmprts = ProcessFuction.process(rttd, scm, targs);
				// MyLog.info("Done" + tmprts.size());
				if (tmprts == null) {
					// loop detected
					this.setSeparateRtts(Collections.singletonList(rttd));
				} else {

					for (RunTimeTaintData tmprttd : tmprts) {
						tmprttd.getReturnValueHandler().handle(tmprttd, local);
						// MyLog.info("??????????????i" +
						// tmprttd.getimmValues().get(local));
					}
					setSeparateRtts(tmprts);
				}
			}
		}
	}
}
