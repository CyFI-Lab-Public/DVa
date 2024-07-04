package com.company.symbolic.constraint;

import soot.Value;
import soot.jimple.IntConstant;

public class ConstraintSourceInt implements IConstraintSource {
	int id;

	@Override
	public void setSource(Value value) {
		// TODO Auto-generated method stub
		IntConstant ict = (IntConstant) value;
		id = ict.value;
	}

	@Override
	public void getSource() {
		// TODO Auto-generated method stub

	}

	public String toZ3() {
		return "findViewById" + id;
	}
}
