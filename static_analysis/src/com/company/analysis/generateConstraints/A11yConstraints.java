package com.company.analysis.generateConstraints;

import com.company.data.RunTimeTaintData;
import com.company.data.SClassAndMethod;
import com.company.data.SMethodUnit;
import com.company.output.OutputDict;
import com.company.symbolic.constraint.Constraint;
import com.company.taint.ProcessFuction;
import com.company.taint.TagSinkFunction;
import com.company.util.*;
//import com.sun.tools.internal.ws.wsdl.document.Output;
import org.json.JSONException;
import soot.*;
import soot.jimple.InvokeExpr;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import javax.xml.transform.Source;
import java.util.*;

public class A11yConstraints {

    /**
     * Generate the constraints for invoking sink functions from accessibility service event handler
     */
    public void generateA11yConstraints() {
        SourceSink sourceSink = new SourceSink();
        addSourceSink(sourceSink);

        setEntryPoints(sourceSink.getSourceFunctions());
        patchCallBacks();
        initCFG();

        // callgraph
        System.out.printf("\n\nStarting to get CallGraph:\n");
        CallGraph cg = Scene.v().getCallGraph();
        System.out.println("Callgraph size:" + cg.size());
        System.out.printf("Finished Getting CallGraph\n\n");

        // Set sink functions
        HashMap<SootMethod, List<SootMethod>> sinkToConcreteInvokes = new HashMap<>();
        for (String sink : sourceSink.getSinkFunctions()) {
            sinkToConcreteInvokes.put(Scene.v().getMethod(sink), new ArrayList<>());
        }

        // Init leaf methods, find the concrete calls in body to the sink functions
        initLeafMethods(sinkToConcreteInvokes);

        // Add to OUTPUT
        for (SootMethod sm : sinkToConcreteInvokes.keySet()) {
            List<String> concreteInvokes = new ArrayList<>();
            for (SootMethod concreteInvoke : sinkToConcreteInvokes.get(sm)) {
                concreteInvokes.add(concreteInvoke.toString());
            }
            OutputDict.addSinkToConcreteInvokes(sm.toString(), concreteInvokes);
        }

        // Test print method body
//        for (SootMethod sm: sinkToConcreteInvokes.keySet()) {
//            for (SootMethod concreteInvoke: sinkToConcreteInvokes.get(sm)) {
//                if (concreteInvoke.isConcrete() && concreteInvoke.getSignature().contains("onAccessibilityEvent")) {
//                    concreteInvoke.retrieveActiveBody();
//                    Body methodBody = concreteInvoke.getActiveBody();
//                    System.out.println("Method body of: " + concreteInvoke.toString());
//                    for (Unit unit: methodBody.getUnits()) {
//                        System.out.println(unit.toString());
//                    }
//                }
//                System.out.println("");
//            }
//        }

        // Parent search from edges, print the call chain  when encountering entry points
        HashMap<SootMethod, List<List<SootMethod>>> sinkToCallChains = resolveSourceToSinkPaths(sourceSink, cg, sinkToConcreteInvokes);

        // Add to OUTPUT
        for (SootMethod sm : sinkToCallChains.keySet()) {
            List<List<String>> callChains = new ArrayList<>();
            for (List<SootMethod> callChain : sinkToCallChains.get(sm)) {
                List<String> callChainStr = new ArrayList<>();
                for (SootMethod call : callChain) {
                    callChainStr.add(call.toString());
                    try {
                        if (sourceSink.getSinkFunctions().contains(call.toString())) {
                            continue;
                        }
                        SClassAndMethod scm = new SClassAndMethod(call);
                        test(scm, sourceSink.getSinkFunctions());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                callChains.add(callChainStr);
            }
            OutputDict.addSinkToCallChains(sm.toString(), callChains);
        }

//        // tag and taintg all sink function
//        TagSinkFunction tif = new TagSinkFunction();
//        tif.work(cg, "custom", sourceSink.getSinkFunctions());
//
//        // Get Constraints for the invocation of sink functions
//        try {
//            for (String startFunction : sourceSink.getSourceFunctions()) {
//                SootMethod sm = Scene.v().getMethod(startFunction);
//                System.out.println("Source Function:" + sm);
//                SClassAndMethod scm = new SClassAndMethod(sm);
//                test(scm, sourceSink.getSinkFunctions());
//
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
        return;
    }

    public static void test(SClassAndMethod scm, List<String> endFunctions) throws JSONException {

        List<RunTimeTaintData> rts = ProcessFuction.process(scm);

        MyLog.info(rts.size());
        MyLog.resrult(rts.size());
        boolean toFile = true;

        for (RunTimeTaintData tmp : rts) {
            boolean containEndFunction = false;
            List<SMethodUnit> tmpAllUnits = tmp.getAllUnits();
            for (String endfunction : endFunctions) {
                for (SMethodUnit tmpUnit : tmpAllUnits) {
                    if (tmpUnit.getUnit().toString().contains(endfunction) && !(tmpUnit.getUnit().toString().contains("if"))) {
                        containEndFunction = true;
                        break;
                    }
                }
                if (containEndFunction) {
                    break;
                }
            }
            if (containEndFunction) {
                MyLog.resrult("**************************************************************");
                for (SMethodUnit smu : tmp.getAllUnits()) {
                    MyLog.resrult(smu);
                }
                MyLog.resrult("**************************");
                for (Constraint cons : tmp.getConstrains()) {
                    // MyLog.info(cons.getCondition());
                    MyLog.resrult(cons.toZ3(tmp));
                }
                toFile = toFile || tmp.hasSource;

                if (toFile) {
                    MyLog.resrult("!!!!!!!!!!!!!!!!!!!!");
                    toLog(tmp, scm);
                }
            }

        }

    }

    public static void toLog(RunTimeTaintData rttd, SClassAndMethod scm) throws JSONException {
        String conStr = "";
        String log = "";
        String buttonToClassStr = "";
        String fname = scm.toString().replace(" ", "").replace("<", "").replace(">", "") + ".txt";
        log += "**************************************************************" + "\n";
        for (SMethodUnit smu : rttd.getAllUnits()) {
            log += smu + "\n";
        }

        conStr += "\n";
        for (Constraint cons : rttd.getConstrains()) {
            conStr += cons.toZ3(rttd) + "\n";
        }

        Set<Map.Entry<String, String>> mapSet = rttd.getButtonToClass().entrySet();
        for (Map.Entry<String, String> pair : mapSet) {
            buttonToClassStr += pair.getKey() + "--->" + pair.getValue() + "\n";
        }

        if (log.length() > 0)
            LogFileUtil.logFile(fname, log);


        if (conStr.trim().length() > 0)
            LogFileUtil.consFile(fname, conStr);

        if (buttonToClassStr.trim().length() > 0)
            LogFileUtil.buttonToClassFile(fname, buttonToClassStr);
    }

    /**
     * Track the callers of cur function, return if meets the entrypoints
     * @param callChains valid non-recursive call chains are stored here
     */
    public void trackCaller(SourceSink sourceSink, CallGraph cg, MethodCallNode cur, List<List<SootMethod>> callChains, int level) {
        if (level > 10) {
            return;
        }

        // Do not handle runnable.run()
        if (cur.sm.getSignature().equals("<android.os.Handler: boolean post(java.lang.Runnable)>")) {
            return;
        }

        // Add the cur call chain to call chain the if backtracked to onAccessibility event handler
        if (cur.sm.getName().contains("onAccessibilityEvent")) {
            List<SootMethod> curCallChain = new ArrayList<>();
            curCallChain.add(cur.sm);
            while (cur.callee != null) {
                cur = cur.callee;
                // if the call chain is recursive, discard.
                if (curCallChain.contains(cur.sm)) {
                    return;
                }
                curCallChain.add(cur.sm);
            }

            if (!callChains.contains(curCallChain)) {
                callChains.add(curCallChain);
            }

            System.out.println("Found Call Path:");
            for (SootMethod sm: curCallChain) {
                System.out.println(sm.toString());
            }
            System.out.println("");
            return;
        }

        // Else keep backtracking the caller edges
        List<Edge> ite = IteratorUtil.iterator2List(cg.edgesInto(cur.sm));
        for (Edge edge: ite) {
            SootMethod caller = edge.src();
            cur.callers.add(new MethodCallNode(caller, cur));
        }
        for (MethodCallNode caller: cur.callers) {
            trackCaller(sourceSink, cg, caller, callChains, level + 1);
        }
        return;
    }

    /**
     * Resolve the source to sink paths
     * @param sourceSink
     * @param cg
     * @param sinkToConcreteInvokes
     * @return map from each sink function to all the call chains that start with the source functions
     */
    public HashMap<SootMethod, List<List<SootMethod>>> resolveSourceToSinkPaths(SourceSink sourceSink, CallGraph cg, HashMap<SootMethod, List<SootMethod>> sinkToConcreteInvokes) {
        HashMap<SootMethod, List<List<SootMethod>>> sinkToCallChains = new HashMap<>();

        for (SootMethod sink : sinkToConcreteInvokes.keySet()) {
            MethodCallNode sinkNode = new MethodCallNode(sink);
            for (SootMethod concreteInvoke : sinkToConcreteInvokes.get(sink)) {
                sinkNode.callers.add(new MethodCallNode(concreteInvoke, sinkNode));
            }

            List<List<SootMethod>> callChains = new ArrayList<>();
            for (MethodCallNode caller: sinkNode.callers) {
//                if (caller.sm.getName().contains("32pasteText")) {
//                    System.out.println("test");
//                }
                trackCaller(sourceSink, cg, caller, callChains, 0);
            }
            sinkToCallChains.put(sink, callChains);
        }
        return sinkToCallChains;
    }

    /**
     * Init leaf methods, find the concrete calls in body to the sink functions
     * @param sinkToConcreteInvokes
     */
    public void initLeafMethods(HashMap<SootMethod, List<SootMethod>> sinkToConcreteInvokes) {
        SootMethod tm;
        Body tb;
        Value tv;

        System.out.println("Concrete calls to sink functions:");
        for (SootClass sc : Scene.v().getClasses()) {
            List<SootMethod> scList = sc.getMethods();
            //for (SootMethod sm : sc.getMethods())
            for (SootMethod sootMethod : scList) {
                // isConcrete, not phantom, abstract or native
                if (sootMethod.isConcrete()) {
                    sootMethod.retrieveActiveBody();
                    tb = sootMethod.getActiveBody();
                    for (ValueBox ubox : tb.getUseBoxes()) {
                        tv = ubox.getValue();
                        if (tv instanceof InvokeExpr) {
                            InvokeExpr invokexp = (InvokeExpr) tv;
                            tm = invokexp.getMethodRef().resolve();
                            if (!tm.toString().startsWith("<android.support") && sinkToConcreteInvokes.containsKey(tm) && !sinkToConcreteInvokes.get(tm).contains(sootMethod)) {
                                // Adding the leaf methods to the internet method's hashmap
                                sinkToConcreteInvokes.get(tm).add(sootMethod);
                                System.out.println(sootMethod + " ==> " + tm);
                            }
                        }
                    }
                }
            }
        }
        System.out.println("End concrete calls to sink functions");
    }

    public static void initCFG() {
        // ##################cfg###########################
        // PackManager.v().runPacks();

        if (!Scene.v().hasCallGraph()) {
            PackManager.v().getPack("wjpp").apply();
            PackManager.v().getPack("cg").apply();
        }
    }

    public static void patchCallBacks() {
        LibraryClassPatcher libcp = new LibraryClassPatcher();
        libcp.patchLibraries();
    }

    public void setEntryPoints(List<String> entryPoints) {
        List<SootMethod> et = new ArrayList<>();

        System.out.printf("\nFound entryPoints:\n");
        for (int i = entryPoints.size() - 1; i >= 0; i--)
        {
            try
            {
                SootMethod sm = Scene.v().getMethod(entryPoints.get(i));
                et.add(sm);
                System.out.println(sm);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                continue;
            }
        }
        System.out.printf("End of entryPoints\n");

        Scene.v().setEntryPoints(et);
    }

    public void addSourceSink(SourceSink sourceSink) {
        List<String> sourceFunctions = new ArrayList<>();
        List<String> sinkFunctions = new ArrayList<>();

        for (SootClass sc : Scene.v().getClasses()) {
            List<SootMethod> scList = sc.getMethods();
            for (SootMethod sootMethod : scList) {
                if (sootMethod.toString().contains("onAccessibilityEvent(android.view.accessibility.AccessibilityEvent") && !sootMethod.toString().contains("android.accessibilityservice.AccessibilityService:")) {
                    sourceFunctions.add(sootMethod.toString());
                }
            }
        }

        if (sourceFunctions.size() == 0) {
            OutputDict.addError("No a11y handler source function found");
            OutputDict.writeOutput();
            System.out.println("No a11y handler source function found");
            System.exit(1);
        }

        // Actions and Global Actions
        sinkFunctions.add("<android.accessibilityservice.AccessibilityService: boolean performGlobalAction(int)>");
        sinkFunctions.add("<android.view.accessibility.AccessibilityNodeInfo: boolean performAction(int)>");
        sinkFunctions.add("<android.view.accessibility.AccessibilityNodeInfo: boolean performAction(int,android.os.Bundle)>");

        // Victim app initiation
        sinkFunctions.add("<android.content.Context: void startActivity(android.content.Intent)>");
        sinkFunctions.add("<android.content.Context: void startActivity(android.content.Intent,android.os.Bundle)>");
        sinkFunctions.add("<android.content.ContextWrapper: void startActivity(android.content.Intent)>");
        sinkFunctions.add("<android.content.ContextWrapper: void startActivity(android.content.Intent,android.os.Bundle)>");
        sinkFunctions.add("<android.app.Activity: void startActivity(android.content.Intent)>");
        sinkFunctions.add("<android.app.Activity: void startActivity(android.content.Intent,android.os.Bundle)>");
        sinkFunctions.add("<android.app.Fragment: void startActivity(android.content.Intent)>");
        sinkFunctions.add("<android.app.Fragment: void startActivity(android.content.Intent,android.os.Bundle)>");


        sourceSink.addSourceFunctions(sourceFunctions);
        sourceSink.addSinkFunctions(sinkFunctions);
    }
}
