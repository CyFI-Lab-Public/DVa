package com.company.taint;

public class ValueTag implements ITag {

	String name;
	Object data;

	public ValueTag(String str, Object data) {
		setName(str);
		setData(data);
	}

	@Override
	public void setName(String obj) {
		name = obj;
	}

	@Override
	public String getName() {
		return name;
	}

	@Override
	public void setData(Object obj) {
		data = obj;
	}

	@Override
	public Object getData() {
		return data;
	}

}
