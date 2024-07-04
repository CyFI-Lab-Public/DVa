package com.company.symbolic.constraint;

import soot.Value;

public interface IConstraintSource {
	public void setSource(Value value);

	public void getSource();

	public String toZ3();
}
