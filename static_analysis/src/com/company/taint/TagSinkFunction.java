package com.company.taint;

import com.company.data.InternetTag;
import com.company.data.JumpTag;
import com.company.util.IteratorUtil;
import com.company.util.MyLog;
import com.company.util.RegexUtil;
import org.json.JSONArray;
import org.json.JSONObject;
import soot.*;
import soot.jimple.*;
import soot.jimple.internal.*;
import soot.jimple.toolkits.callgraph.CallGraph;
import soot.jimple.toolkits.callgraph.Edge;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;

class Pair extends Object{
	public int key;
	public List<Value> value;

	Pair(int key, List<Value> value) {
		this.key = key;
		this.value = value;
	}
}

class CallNode extends Object {
	SootMethod sourceMethod;
	ArrayList<CallNode> children;
	CallNode parent;

	CallNode() {
		this.sourceMethod = null;
		this.children = new ArrayList<CallNode>();
		this.parent = null;
	}

	CallNode(SootMethod method) {
		this.sourceMethod = method;
		this.children = new ArrayList<CallNode>();
		this.parent = null;
	}

	CallNode(SootMethod method, CallNode parent) {
		this.sourceMethod = method;
		this.children = new ArrayList<CallNode>();
		this.parent = parent;
	}

	public void addChild(SootMethod method) {
		CallNode newNode = new CallNode(method, this);
		this.children.add(newNode);
	}

	public SootMethod getSource() {
		return this.sourceMethod;
	}

	public CallNode getNewestChild() {
		int n = this.children.size();
		return this.children.get(n-1);
	}

	public CallNode getParent() {
		return this.parent;
	}

	public void setSource(SootMethod method) {
		this.sourceMethod = method;
	}
}

public class TagSinkFunction {

	HashMap<SootMethod, List<SootMethod>> internetMethod = new HashMap<SootMethod, List<SootMethod>>();
	HashMap<SootMethod, List<SootMethod>> jumpMethod = new HashMap<SootMethod, List<SootMethod>>();
	List<SootMethod> leafMethods = new ArrayList<SootMethod>();
	List<SootMethod> jumpLeafMethods = new ArrayList<SootMethod>();

	HashMap<SootMethod, List<SootMethod>> allXActMethod = new HashMap<SootMethod, List<SootMethod>>();
	List<SootMethod> currentLeafMethods = new ArrayList<SootMethod>();
	List<SootMethod> nextLeafMethods = new ArrayList<SootMethod>();
	List<SootClass> allClasses = new ArrayList<SootClass>();
	List<SootClass> currentLeafClasses = new ArrayList<SootClass>();

	void initXActMethod(List<String> endFunctions) {
		for (String func : endFunctions) {
			try {
				allXActMethod.put(Scene.v().getMethod(func), new ArrayList<SootMethod>());
			} catch (Exception e) {
				MyLog.info("Internet Function not used " + func);
			}
		}

		System.out.println("All sink functions");
		Set<SootMethod> keys = allXActMethod.keySet();
		for (SootMethod key : keys) {
			currentLeafMethods.add(key);
			nextLeafMethods.add(key);
			allClasses.add(key.getDeclaringClass());
			currentLeafClasses.add(key.getDeclaringClass());
			System.out.println(key);
		}
		System.out.println("Finished all sink functions\n\n");
	}

	void initInternetMethod(String operationMode, List<String> endFunctions) {
		List<String> imsig = new ArrayList<String>();
		List<String> jumpSig = new ArrayList<>();

		// List of sink methods to track

		for (String endFunction : endFunctions)
		{
			imsig.add(endFunction);
		}


		for (String msig : imsig) {
			try {
				internetMethod.put(Scene.v().getMethod(msig), new ArrayList<SootMethod>());
			} catch (Exception e) {
				MyLog.info("Internet Function not used " + msig);
			}
		}

		for (String jumpEnd : jumpSig) {
			try {
				jumpMethod.put(Scene.v().getMethod(jumpEnd), new ArrayList<SootMethod>());
			} catch	(Exception e) {
				MyLog.info("Jump Method not used " + jumpEnd);
			}
		}

		// Outputting all ending functions
		System.out.println("Getting all keys of sink functions");
		Set<SootMethod> keys = internetMethod.keySet();
		for (SootMethod key : keys) {
			System.out.println(key);
		}
		System.out.println("Finished getting all keys of sink functions\n\n");
		System.out.println("Getting all keys of jump to sink functions");
		Set<SootMethod> jumpKeys = jumpMethod.keySet();
		for (SootMethod jumpKey : jumpKeys) {
			System.out.println(jumpKey);
		}
		System.out.println("Finished getting all keys of jump to sink functions\n\n");
	}

	void leafsearch() {
		SootMethod tm;
		Body tb;
		Value tv;

		while (true) {
			for (SootClass sc : Scene.v().getClasses()) {
				if (!currentLeafClasses.contains(sc)) {
					continue;
				}
				List<SootMethod> scList = sc.getMethods();
				//for (SootMethod sm : sc.getMethods())
				for (int i = 0; i < scList.size(); i++) {
					// isConcrete, not phantom, abstract or native
					if (scList.get(i).isConcrete()) {
						scList.get(i).retrieveActiveBody();
						tb = scList.get(i).getActiveBody();
						for (ValueBox ubox : tb.getUseBoxes()) {
							tv = ubox.getValue();
							if (tv instanceof InvokeExpr) {
								InvokeExpr invokexp = (InvokeExpr) tv;
								tm = invokexp.getMethodRef().resolve();
								if (currentLeafMethods.contains(tm)) {
									// Adding the leaf methods to the same class method's hashmap
									allXActMethod.get(tm).add(scList.get(i));
									System.out.printf("\n\nSameClassLeafMethod to sinkMethod:\n");
									System.out.println(scList.get(i) + " ==> " + tm);
									System.out.printf("End of SameClassLeafMethod to sinkMethod\n\n\n");
									nextLeafMethods.add(scList.get(i));
								}
							}
						}
					}
				}
			}
			currentLeafMethods = new ArrayList<SootMethod>(nextLeafMethods);
			nextLeafMethods.clear();
			currentLeafClasses.clear();
			for (SootMethod m : currentLeafMethods) {
				if (!allXActMethod.containsKey(m)) {
					allXActMethod.put(m, new ArrayList<SootMethod>());
				}
			}

			for (SootClass sc : Scene.v().getClasses()) {
				if (allClasses.contains(sc) || sc.toString().contains("java") || sc.toString().contains("android")) {
					continue;
				}
				List<SootMethod> scList = sc.getMethods();
				//for (SootMethod sm : sc.getMethods())
				for (int i = 0; i < scList.size(); i++) {
					// isConcrete, not phantom, abstract or native
					if (scList.get(i).isConcrete()) {
						scList.get(i).retrieveActiveBody();
						tb = scList.get(i).getActiveBody();
						for (ValueBox ubox : tb.getUseBoxes()) {
							tv = ubox.getValue();
							if (tv instanceof InvokeExpr) {
								InvokeExpr invokexp = (InvokeExpr) tv;
								tm = invokexp.getMethodRef().resolve();
								if (currentLeafMethods.contains(tm)) {
									// Adding the leaf methods to the same class method's hashmap
									allXActMethod.get(tm).add(scList.get(i));
									System.out.printf("\n\nXClassLeafMethod to sinkMethods:\n");
									System.out.println(scList.get(i) + " ==> " + tm);
									System.out.printf("End of XClassLeafMethod to sinkMethods\n\n\n");
									nextLeafMethods.add(scList.get(i));
								}
							}
						}
					}
				}
			}
			if (nextLeafMethods.isEmpty()) {
				break;
			}
			currentLeafMethods = new ArrayList<SootMethod>(nextLeafMethods);
			for (SootMethod m : currentLeafMethods) {
				allClasses.add(m.getDeclaringClass());
				currentLeafClasses.add(m.getDeclaringClass());
				if (!allXActMethod.containsKey(m)) {
					allXActMethod.put(m, new ArrayList<SootMethod>());
				}
			}
		}
	}

	boolean targetIsTainted(SootMethod entryPoint, SootMethod tgt) {
		List<Type> a = tgt.getParameterTypes();
		if (tgt.getParameterTypes() == null || tgt.getParameterTypes().size() == 0) return false;
		Body body = entryPoint.retrieveActiveBody();
		PatchingChain<Unit> units = body.getUnits();

		List<Value> taintArray = new ArrayList<Value>();
		LinkedList<Pair> branchList = new LinkedList<>();// save if stmt jump status
		HashMap<Integer, Integer> ifstmtMap = new HashMap<>(); //save if stmt pass time, handle infinite loop
		List<Object> unitList = Arrays.asList(units.toArray());
		int lineNumber = 0;
		int LOOP_THRESHOLD = 10;

		while (lineNumber < unitList.size()) {
			if(ifstmtMap.containsKey(lineNumber)) {
				if( ifstmtMap.get(lineNumber) < LOOP_THRESHOLD)ifstmtMap.put(lineNumber, ifstmtMap.get(lineNumber) + 1);
				else {
					if(branchList.isEmpty()) break;
					Pair p = branchList.pop();
					lineNumber = p.key;
					taintArray = p.value;
					continue;
				}
			}

			Unit unit = (Unit) unitList.get(lineNumber);
			if (unit instanceof JIdentityStmt) {
				if (((JIdentityStmt) unit).getRightOpBox().getValue() instanceof ParameterRef) {
					taintArray.add( ((JIdentityStmt) unit).getLeftOp());
				}
			}
			else if (unit instanceof JInvokeStmt) {
				String invokeMethod = ((JInvokeStmt) unit).getInvokeExpr().getMethodRef().toString();
				if (invokeMethod.equals(tgt.toString())) {
					List<Value> args = ((JInvokeStmt) unit).getInvokeExpr().getArgs();
					for (Value arg : args) {
						if ( arg instanceof Value && taintArray.contains(arg)) {
							return true;
						}
					}
				}
			}
			// Assign can be both taint assignment and function invocation, handle two cases
			else if (unit instanceof JAssignStmt) {
				Value rightValue = ((JAssignStmt) unit).getRightOp();
				// Check if invoked method is tgt, and tainted by parameters
				if (rightValue instanceof InvokeExpr && ((InvokeExpr) rightValue).getMethodRef().toString().equals(tgt.toString())){
					List<Value> args = ((InvokeExpr) rightValue).getArgs();
					for(Value arg : args){
						if(taintArray.contains(arg)) return true;
					}
				}
				//not tgt or not tainted, perform taint
				List<Value> rightLocals = handleAssignStmt((JAssignStmt)unit);
				Value leftVal = ((JAssignStmt) unit).getLeftOp();

				Value leftLocal;




				if(leftVal instanceof JInstanceFieldRef) leftLocal = ((JInstanceFieldRef) leftVal).getBase();
				else leftLocal = (leftVal instanceof JArrayRef) ?  ((JArrayRef) leftVal).getBase():  leftVal;

				for(Value rightLocal: rightLocals){
					if(taintArray.contains(rightLocal)) {
						taintArray.add(leftLocal);
						break;
					}
				}
			}
			else if(unit instanceof JIfStmt){
				Unit tgtStmt = ((JIfStmt) unit).getTarget();
				//Find unit in array
				int index = unitList.indexOf(tgtStmt);
				if (index != -1){
					// make a deep copy of taintArray
					List<Value> newList = new ArrayList<>(taintArray);
					branchList.add(new Pair(index, newList));
					if(!ifstmtMap.containsKey(index)) ifstmtMap.put(index, 0);
				}
			}
			else if(unit instanceof JReturnStmt || unit instanceof JReturnVoidStmt){
				if(branchList.isEmpty()) break;
				Pair p = branchList.pop();
				lineNumber = p.key;
				taintArray = p.value;
				continue;
			}

			else if (unit instanceof JGotoStmt){
				Unit tgtStmt = ((JGotoStmt) unit).getTarget();
				int index = unitList.indexOf(tgtStmt);
				if(index != -1) {
					lineNumber = index;
					if(!ifstmtMap.containsKey(index)) ifstmtMap.put(index, 0);
					continue;
				}
			}

			lineNumber += 1;
			if(lineNumber >= unitList.size()) {
				if(branchList.isEmpty()) break;
				Pair p = branchList.pop();
				lineNumber = p.key;
				taintArray = p.value;
			}
		}
		return false;
	}

	List<Value> handleAssignStmt(JAssignStmt stmt){
		Value rightValue = stmt.getRightOp();
		List<Value> res = new ArrayList<>();
		if(rightValue instanceof InvokeExpr) {
			for (Value val:((InvokeExpr) rightValue).getArgs()) {
				res.add(val);
			}
			if (rightValue instanceof InstanceInvokeExpr &&
					((InstanceInvokeExpr) rightValue).getBase() instanceof JimpleLocal) {
				Value base = ((InstanceInvokeExpr) rightValue).getBase();
				res.add(base);
			}
		} else if(rightValue instanceof  InstanceFieldRef) {
			Value base = ((InstanceFieldRef) rightValue).getBase();
			if (base instanceof JimpleLocal) res.add(base);
		}else if (rightValue instanceof JimpleLocal) res.add(rightValue);
		else if(rightValue instanceof StaticFieldRef) res.add(rightValue);
		else if(rightValue instanceof  AbstractBinopExpr) {
			if(((AbstractBinopExpr) rightValue).getOp1() instanceof  JimpleLocal) res.add(((AbstractBinopExpr) rightValue).getOp1());
			if(((AbstractBinopExpr) rightValue).getOp2() instanceof  JimpleLocal) res.add(((AbstractBinopExpr) rightValue).getOp2());
		}else if(rightValue instanceof ArrayRef)
			if(((ArrayRef) rightValue).getBase() instanceof JimpleLocal) res.add(((ArrayRef) rightValue).getBase());

		return res;
	}






	void generateMethodList(CallGraph cg,
			MethodOrMethodContext entryPoint,
			List<String> systemPatternStrings,
			List<MethodOrMethodContext> resNativeSys,
			List<MethodOrMethodContext> resNativeThirdParty,
			List<MethodOrMethodContext> resSys,
			List<MethodOrMethodContext> resThirdParty, boolean traceCallNode, CallNode sourceNode, BufferedWriter writer){
		Iterator<Edge> edges = cg.edgesOutOf(entryPoint);
		ArrayList<String> sinks = new ArrayList<>();
		sinks.add("<android.content.Context: void startActivity(android.content.Intent)>");
		sinks.add("<android.content.Context: void startActivity(android.content.Intent,android.os.Bundle)>");
		sinks.add("<android.app.Fragment: void startActivity(android.content.Intent)>");
		sinks.add("<android.app.Fragment: void startActivity(android.content.Intent,android.os.Bundle)>");
		sinks.add("<android.app.Activity: void startActivity(android.content.Intent)>");
		sinks.add("<android.app.Activity: void startActivity(android.content.Intent,android.os.Bundle)>");
		sinks.add("<android.content.ContextWrapper: void startActivity(android.content.Intent)>");
		sinks.add("<android.content.ContextWrapper: void startActivity(android.content.Intent,android.os.Bundle)>");
		sinks.add("<android.app.ActivityManager$AppTask: void startActivity(android.content.Context,android.content.Intent,android.os.Bundle)>");
		sinks.add("<android.support.v4.content.ContextCompat: void startActivity(android.content.Context,android.content.Intent,android.os.Bundle)>");
		sinks.add("<android.support.v4.app.ActivityCompat: void startActivity(android.app.Activity,android.content.Intent,android.os.Bundle)>");
		sinks.add("<android.support.v4.app.ActivityCompatJB: void startActivity(android.content.Context,android.content.Intent,android.os.Bundle)>");
		sinks.add("<android.support.v4.app.Fragment: void startActivity(android.content.Intent)>");
		sinks.add("<android.support.v4.app.Fragment: void startActivity(android.content.Intent,android.os.Bundle)>");
		sinks.add("<android.app.LocalActivityManager: android.view.Window startActivity(java.lang.String,android.content.Intent)>");
		sinks.add("<android.support.v4.content.ContextCompatJellybean: void startActivity(android.content.Context,android.content.Intent,android.os.Bundle)>");
		sinks.add("<com.alipay.android.app.IRemoteServiceCallback: void startActivity(java.lang.String,java.lang.String,int,android.os.Bundle)>");
		sinks.add("<com.alipay.android.app.IRemoteServiceCallback$Stub$a: void startActivity(java.lang.String,java.lang.String,int,android.os.Bundle)>");
		sinks.add("<com.alipay.sdk.util.g: void startActivity(java.lang.String,java.lang.String,int,android.os.Bundle)>");
		sinks.add("<com.m4399.support.controllers.BaseActivity: void startActivity(android.content.Intent)>");
		sinks.add("<android.widget.Toast: android.widget.Toast makeText(android.content.Context,java.lang.CharSequence,int)>");
		sinks.add("<android.widget.Toast: android.widget.Toast makeText(android.content.Context,int,int)>");
		sinks.add("<android.content.Intent: android.content.Intent setData(android.net.Uri)>");
		sinks.add("<android.content.Intent: android.content.Intent setClass(android.content.Context,java.lang.Class)>");
		sinks.add("<android.net.Uri: android.net.Uri parse(java.lang.String)>");

		while(edges.hasNext()){
			Edge edge = edges.next();
			MethodOrMethodContext tgt = edge.getTgt();
			if (resNativeSys.contains(tgt) || resNativeThirdParty.contains(tgt) || resSys.contains(tgt) || resThirdParty.contains(tgt)) continue; // pass if results has been in one set
			if(tgt.getClass().equals(SootMethod.class)){
				try{
					if (!targetIsTainted(((SootMethod)entryPoint), ((SootMethod) tgt))) continue;
				} catch (Exception e) {
					e.printStackTrace();
					continue;
				}
				String tgtPkgName = ((SootMethod) tgt).getDeclaringClass().getPackageName();
				if (traceCallNode) sourceNode.addChild((SootMethod) tgt);
				if(RegexUtil.regexMatch(systemPatternStrings, tgtPkgName)) {
					if (((SootMethod) tgt).isNative()) resNativeSys.add(tgt);
					else resSys.add(tgt);
					if (sinks.contains(((SootMethod) tgt).toString())) {
						CallNode currentNode = sourceNode.getNewestChild();
						ArrayList<String> path = new ArrayList<String>();
						ArrayList<List<Object>> pathUnits = new ArrayList<>();
						path.add(currentNode.getSource().toString());
						pathUnits.add(Arrays.asList((currentNode.getSource().getActiveBody().getUnits()).toArray()));
						while (currentNode.getParent() != null) {
							currentNode = currentNode.getParent();
							path.add(currentNode.getSource().toString());
							pathUnits.add(Arrays.asList((currentNode.getSource().getActiveBody().getUnits()).toArray()));
						}
						try{
							System.out.println("Found path: ");
							writer.write("Found Path: "+ "\n");
							for (int i = path.size()-1; i >= 0; i--) {
								System.out.println(path.get(i));
								writer.write(path.get(i)+ "\n");
								System.out.println("    All units for this method:");
								writer.write("    All units for this method:"+ "\n");
								for (int j = 0; j < pathUnits.get(i).size(); j++) {
									System.out.println("    " + pathUnits.get(i).get(j).toString());
									writer.write("    " + pathUnits.get(i).get(j).toString() + "\n");
								}
								System.out.println("    End of all units for this method");
								writer.write("    End of all units for this method" + "\n");
							}
							System.out.println("End of path");
							writer.write("End of path" + "\n");
							writer.flush();
						}catch (Exception e){e.printStackTrace();}
					}
				}
				else {
					if(((SootMethod) tgt).isNative()) resNativeThirdParty.add(tgt);
					else resThirdParty.add(tgt);
					if (traceCallNode) generateMethodList(cg, tgt, systemPatternStrings, resNativeSys, resNativeThirdParty , resSys,resThirdParty, true, sourceNode.getNewestChild(), writer);
					else generateMethodList(cg, tgt, systemPatternStrings, resNativeSys, resNativeThirdParty , resSys,resThirdParty, false, sourceNode, writer);
				}
			}
		}
	}
	// Finds the leaf methods(parent methods) that calls the internet connection methods
	void initLeafMethod() {
		SootMethod tm;
		Body tb;
		Value tv;
		System.out.printf("LeafMethod to Sink Methods:\n");
		for (SootClass sc : Scene.v().getClasses()) {
			List<SootMethod> scList = sc.getMethods();
			//for (SootMethod sm : sc.getMethods())
			for (int i = 0; i < scList.size(); i++)
			{
				// isConcrete, not phantom, abstract or native
				if (scList.get(i).isConcrete()) {
					scList.get(i).retrieveActiveBody();
					tb = scList.get(i).getActiveBody();
					for (ValueBox ubox : tb.getUseBoxes()) {
						tv = ubox.getValue();
						if (tv instanceof InvokeExpr) {
							InvokeExpr invokexp = (InvokeExpr) tv;
							tm = invokexp.getMethodRef().resolve();
							if (internetMethod.containsKey(tm)) {
								// Adding the leaf methods to the internet method's hashmap
								internetMethod.get(tm).add(scList.get(i));

								System.out.println(scList.get(i) + " ==> " + tm);
								leafMethods.add(scList.get(i));
							}
							if (jumpMethod.containsKey(tm)) {
								jumpMethod.get(tm).add(scList.get(i));
								System.out.println(scList.get(i) + " ==> " + tm);
								jumpLeafMethods.add(scList.get(i));
							}
						}
					}
				}
			}
		}
		System.out.println("End of LeafMethod to Sink Methods");
		patchLeaf();
	}

	public void patchLeaf() {
		SootMethod tm;
		List<String> tmp = new ArrayList<String>();
		for (String msig : tmp) {
			if (Scene.v().containsMethod(msig)) {
				tm = Scene.v().getMethod(msig);
				leafMethods.add(tm);
			}
		}

	}

	// Tags all parent edges that calls the leafmethods
	void tagMethod(CallGraph cg, SootMethod sm) {
		if (sm.hasTag(InternetTag.name()))
			return;
		// Adds internet tag here
		sm.addTag(new InternetTag());

		List<Edge> ite = IteratorUtil.iterator2List(cg.edgesInto(sm));
		for (Edge edge : ite) {
			SootMethod caller = edge.src();
			// ////////
			// if
			// (caller.getSignature().equals("<com.vkontakte.android.data.PrivacySetting:
			// java.lang.String getApiValue()>")) {
			// MyLog.resrult(sm);
			// }
			// ////////
			tagMethod(cg, caller);
			// System.out.println(sm + " may be called by " + caller);
		}
	}

	void tagJumpMethod(CallGraph cg, Queue<SootMethod> smQ) {
		if (smQ.size() == 0) {
			return;
		}

		SootMethod sm = smQ.poll();

		if (sm.getSubSignature().equalsIgnoreCase("void onClick(android.view.View)")) {
			if (!sm.hasTag(JumpTag.name())) {
				sm.addTag(new JumpTag());
			}
			return;
		}

		if (sm.hasTag(JumpTag.name())) {
			tagJumpMethod(cg, smQ);
		}
		else {
			sm.addTag(new JumpTag());

			List<Edge> ite = IteratorUtil.iterator2List(cg.edgesInto(sm));
			for (Edge edge : ite) {
				smQ.add(edge.src());
			}
			tagJumpMethod(cg, smQ);
		}
	}



	public void work(CallGraph cg, String operationMode, List<String> endFunctions) {
		if (operationMode.equals("xAct")) {
			initXActMethod(endFunctions);
			leafsearch();
			System.out.printf("\n\nAll leafMethods:\n");
			Set<SootMethod> keys = allXActMethod.keySet();
			for (SootMethod key : keys) {
				System.out.println(key);
			}
			System.out.printf("End of leafMethods\n\n\n");
			System.exit(0);
		}

		if(operationMode.equals("jsInterface")){
			// First run jsInterface generally, look at the jsInterface_methods.json, grab the system APIs to be tracked, grab the entrypoint methods, then
			// update the following trackedEntrypoints array and the sinks array in generateMethodList()
			try {
				List<SootMethod> entryPoints = Scene.v().getEntryPoints();
				JSONArray jsonArray = new JSONArray();
				BufferedWriter JSPathUnitsWriter = new BufferedWriter(new FileWriter("JSPathUnits.txt"));
				ArrayList<String> trackedEntrypoints = new ArrayList<String>();
//				trackedEntrypoints.add("<com.tencent.reading.webview.jsapi.CustomWebBrowserForItemActivityInterface: void downloadAppByLocal(java.lang.String,java.lang.String,java.lang.String,java.lang.String,java.lang.String)>");
//				trackedEntrypoints.add("<com.tencent.reading.webview.jsapi.ScriptInterface: void installApp(java.lang.String,java.lang.String,java.lang.String,java.lang.String)>");
//				trackedEntrypoints.add("<com.startapp.android.publish.html.JsInterface: void externalLinks(java.lang.String)>");
				trackedEntrypoints.add("<com.mobile.indiapp.utils.WebViewTools: void share(java.lang.String)>");

				for(MethodOrMethodContext entryPoint: entryPoints){
					List<MethodOrMethodContext> resNativeSys = new ArrayList<MethodOrMethodContext>();
					List<MethodOrMethodContext> resNativeThirdParty = new ArrayList<MethodOrMethodContext>();
					List<MethodOrMethodContext> resSys = new ArrayList<MethodOrMethodContext>();
					List<MethodOrMethodContext> resThirdParty = new ArrayList<MethodOrMethodContext>();
					List<String> goodPatternStrings = Arrays.asList("^android.*$", "^java.*$", "^javax.*$", "^com.google.*$",
							"^androidx.*$", "^com.volley.*$", "^javax.* $");
					// whether to trace taint path from entrypoint to target
					// change to false
					boolean traceCallNode = true;
					CallNode entryNode = new CallNode();
					// delete the following line
					entryNode.setSource((SootMethod) entryPoint);

					if (entryPoint.getClass().equals(SootMethod.class) && trackedEntrypoints.contains(entryPoint.toString())) {
						traceCallNode = true;
						entryNode.setSource((SootMethod) entryPoint);
					}
					generateMethodList(cg, entryPoint, goodPatternStrings, resNativeSys, resNativeThirdParty, resSys, resThirdParty, traceCallNode, entryNode, JSPathUnitsWriter);
					JSONObject obj = new JSONObject();
					JSONArray resJsonNativeSys = new JSONArray();
					JSONArray resJsonNativeThirdParty = new JSONArray();
					JSONArray resJsonSys = new JSONArray();
					JSONArray resJsonThirdParty = new JSONArray();

					for(MethodOrMethodContext tgt: resNativeSys){
						resJsonNativeSys.put(tgt.toString());
					}

					for(MethodOrMethodContext tgt: resNativeThirdParty){
						resJsonNativeThirdParty.put(tgt.toString());
					}

					for(MethodOrMethodContext tgt: resSys){
						resJsonSys.put(tgt.toString());
					}

					for(MethodOrMethodContext tgt: resThirdParty){
						resJsonThirdParty.put(tgt.toString());
					}


					try {
						obj.put("entryPoint", entryPoint.toString());
						obj.put("methods_native_sys", resJsonNativeSys);
						obj.put("methods_native_third_party", resJsonNativeThirdParty);
						obj.put("methods_sys", resJsonSys);
						obj.put("methods_third_party", resJsonThirdParty);
						jsonArray.put(obj);
					}catch(Exception e){
						e.printStackTrace();
					}
				}

				JSPathUnitsWriter.close();
				String jsonString = jsonArray.toString();
				try {
					BufferedWriter writer = new BufferedWriter(new FileWriter("jsInterface_methods.json"));
					writer.write(jsonString);
					writer.newLine();
					writer.close();
				} catch (Exception e){
					e.printStackTrace();
				}
				return;
			} catch (Exception e) {e.printStackTrace();}
		}


		initInternetMethod(operationMode, endFunctions);
		initLeafMethod();
		System.out.println("All leafMethods:");
		for (SootMethod sm : leafMethods)
		{
			System.out.println(sm);
		}
		System.out.println("End of leafMethods");

		System.out.println("All jumpLeafMethods:");
		for (SootMethod sm : jumpLeafMethods)
		{
			System.out.println(sm);
		}
		System.out.println("End of jumpLeafMethods");
		for (SootMethod sm : leafMethods) {
			tagMethod(cg, sm);
		}
		for (SootMethod sm : jumpLeafMethods) {
			Queue<SootMethod> smQ = new LinkedList<SootMethod>();
			smQ.add(sm);
			tagJumpMethod(cg, smQ);
		}
	}

}


