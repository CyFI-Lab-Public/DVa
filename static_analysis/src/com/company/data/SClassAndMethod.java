package com.company.data;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

public class SClassAndMethod {
    SootClass sc = null;
    SootMethod sm = null;
    String classS;
    String methodS;

    public SClassAndMethod(String sclass, String method) {
        this.classS = sclass;
        this.methodS = method;
    }

    public SClassAndMethod(SootMethod sm) {
        this.sm = sm;
        this.sc = sm.getDeclaringClass();
        this.classS = sc.getName();
        this.methodS = sm.getSubSignature();
    }

    public SootClass getSc() {
        if (sc == null) {
            sc = Scene.v().getSootClass(getClassName());
        }
        return sc;
    }

    public SootMethod getSm() {
        if (sm == null) {
            sm = getSc().getMethod(getMethodName());
        }
        // System.out.println(sm);
        return sm;
    }

    public String getClassName() {
        return classS;
    }

    public void setClassName(String sclass) {
        this.classS = sclass;
    }

    public String getMethodName() {
        return methodS;
    }

    public void setMethodName(String method) {
        this.methodS = method;
    }

    public String toString() {
        return this.getSm().getSignature();
    }

    public boolean eqTo(SClassAndMethod scm) {
        return this.getSm().getSignature().equals(scm.getSm().getSignature());
    }
}