package com.company.data;

import soot.Body;

import java.util.List;
import java.util.Stack;

public interface IRunTimeManager {

	public RunTimeTaintData getRttd();

	public List<RunTimeTaintData> getRts();

	public Body getBody();

	public Stack<RunTimePoint> getWatingQueue();

	public RunTimePoint getTrt();

}
