package com.company.data;

import soot.Local;

public class TaintedArg {
	/*
	 * the index of this arg
	 */
	private int argIndex;

	/*
	 * the source variable of this arg
	 */
	private Local sourceLocal;

	public TaintedArg(int argIndex, Local sourceLocal) {
		setArgIndex(argIndex);
		setSourceLocal(sourceLocal);
	}

	public int getArgIndex() {
		return argIndex;
	}

	public void setArgIndex(int argIndex) {
		this.argIndex = argIndex;
	}

	public Local getSourceLocal() {
		return sourceLocal;
	}

	public void setSourceLocal(Local sourceLocal) {
		this.sourceLocal = sourceLocal;
	}
}
