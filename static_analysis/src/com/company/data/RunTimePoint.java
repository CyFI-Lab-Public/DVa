package com.company.data;

import soot.Body;
import soot.Unit;

public class RunTimePoint {
	private RunTimeTaintData rttd;
	private Body body;
	private Unit unit;

	public RunTimePoint(RunTimeTaintData rttd, Body body, Unit unit) {
		setRttd(rttd);
		setBody(body);
		setUnit(unit);
	}

	public RunTimeTaintData getRttd() {
		return rttd;
	}

	public void setRttd(RunTimeTaintData rttd) {
		this.rttd = rttd;
	}

	public Body getBody() {
		return body;
	}

	public void setBody(Body body) {
		this.body = body;
	}

	public Unit getUnit() {
		return unit;
	}

	public void setUnit(Unit unit) {
		this.unit = unit;
	}

}
