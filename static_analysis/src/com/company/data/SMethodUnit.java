package com.company.data;

import soot.SootMethod;
import soot.Unit;

public class SMethodUnit {
	SootMethod sm;
	Unit unit;

	public SMethodUnit(SootMethod sm, Unit unit) {
		setSm(sm);
		setUnit(unit);
	}

	public SootMethod getSm() {
		return sm;
	}

	private void setSm(SootMethod sm) {
		this.sm = sm;
	}

	public Unit getUnit() {
		return unit;
	}

	private void setUnit(Unit unit) {
		this.unit = unit;
	}

	public String toString() {
		return sm.getSignature() + " --> " + unit.toString();
	}
}
