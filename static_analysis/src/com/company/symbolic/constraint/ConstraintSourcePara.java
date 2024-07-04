package com.company.symbolic.constraint;

import soot.Value;
import soot.jimple.ParameterRef;

public class ConstraintSourcePara implements IConstraintSource {
    String id;

    @Override
    public void setSource(Value value) {
        // TODO Auto-generated method stub
        ParameterRef pararef = (ParameterRef) value;
        id = pararef.toString();
    }

    @Override
    public void getSource() {
        // TODO Auto-generated method stub

    }

    public String toZ3() {
        return "findViewById" + id.toString();
    }
}
