package com.company.data;

import soot.tagkit.AttributeValueException;
import soot.tagkit.Tag;

public class JumpTag implements Tag {

    public static String name() {
        return "Jump";
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