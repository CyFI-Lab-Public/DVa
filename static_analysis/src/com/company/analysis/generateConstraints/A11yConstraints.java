package com.company.analysis.generateConstraints;

import com.company.output.OutputDict;
import com.company.util.IteratorUtil;
import com.company.util.LibraryClassPatcher;
import com.company.util.MethodCallNode;
//import com.sun.tools.internal.ws.wsdl.document.Output;
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
        System.out.printf("\n\n Starting to get CallGraph:\n");
        CallGraph cg = Scene.v().getCallGraph();
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
                }
                callChains.add(callChainStr);
            }
            OutputDict.addSinkToCallChains(sm.toString(), callChains);
        }

        return;
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
                                System.out.println("\n\nConcrete calls to sink functions:" + sootMethod + " ==> " + tm);
                            }
                        }
                    }
                }
            }
        }
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

        System.out.printf("\n\n\nFound entryPoints:\n");
        for (int i = entryPoints.size() - 1; i >= 0; i--)
        {
            try
            {
                SootMethod sm = Scene.v().getMethod(entryPoints.get(i));
                et.add(sm);
                System.out.println("Added EntryPoint: " + sm);
            }
            catch (Exception e)
            {
                e.printStackTrace();
                continue;
            }
        }
        System.out.printf("End of entryPoints\n\n\n");

        Scene.v().setEntryPoints(et);
    }

    public void addSourceSink(SourceSink sourceSink) {
        List<String> sourceFunctions = new ArrayList<>();
        List<String> sinkFunctions = new ArrayList<>();

        for (SootClass sc : Scene.v().getClasses()) {
            List<SootMethod> scList = sc.getMethods();
            for (SootMethod sootMethod : scList) {
                if (sootMethod.toString().contains("onAccessibilityEvent(android.view.accessibility.AccessibilityEvent")) {
                    sourceFunctions.add(sootMethod.toString());
                }
            }
        }

        if (sourceFunctions.size() == 0) {
            OutputDict.addError("No source function found");
            OutputDict.writeOutput();
            System.out.println("No source function found");
            System.exit(1);
        }


//        sourceFunctions.add("<com.boda.thanksnexzintentecachehunablevgaryzdaybdivorceastructuresdpersonyconsecutivezcouncilybudapestq32: void onAccessibilityEvent(android.view.accessibility.AccessibilityEvent)>");

        sinkFunctions.add("<android.accessibilityservice.AccessibilityService: boolean performGlobalAction(int)>");
        sinkFunctions.add("<android.view.accessibility.AccessibilityNodeInfo: boolean performAction(int)>");
        sinkFunctions.add("<android.view.accessibility.AccessibilityNodeInfo: boolean performAction(int,android.os.Bundle)>");

        sourceSink.addSourceFunctions(sourceFunctions);
        sourceSink.addSinkFunctions(sinkFunctions);
    }
}
