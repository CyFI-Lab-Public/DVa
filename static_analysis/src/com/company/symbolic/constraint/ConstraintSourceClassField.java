package com.company.symbolic.constraint;

import soot.SootField;
import soot.Value;
import soot.jimple.InstanceFieldRef;

public class ConstraintSourceClassField implements IConstraintSource {
	SootField field;

	@Override
	public void setSource(Value value) {
		// TODO Auto-generated method stub
		InstanceFieldRef ifr = (InstanceFieldRef) value;
		field = ifr.getField();
	}

	@Override
	public void getSource() {
		// TODO Auto-generated method stub

	}

	public String toZ3() {
		return field.toString();
	}
}
