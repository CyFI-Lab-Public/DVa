package com.company.util;

import soot.SootMethod;

import java.util.ArrayList;
import java.util.List;

public class MethodCallNode {
    public SootMethod sm;
    public List<MethodCallNode> callers;
    public MethodCallNode callee;

    public MethodCallNode(SootMethod cur) {
        this.sm = cur;
        callers = new ArrayList<>();
    }

    public MethodCallNode(SootMethod cur, MethodCallNode callee) {
        this.sm = cur;
        this.callee = callee;
        callers = new ArrayList<>();
    }
}
