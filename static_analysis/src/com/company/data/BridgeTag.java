package com.company.data;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class BridgeTag implements Tag {

	public static String name() {
		return "Bridge";
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return name();
	}

	@Override
	public byte[] getValue() throws AttributeValueException {
		// TODO Auto-generated method stub
		return null;
	}

}
