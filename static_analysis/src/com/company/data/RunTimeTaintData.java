package com.company.data;

import com.company.config.Envir;
import com.company.symbolic.constraint.Constraint;
import com.company.taint.ITag;
import com.company.taint.returnvalue.IReturnValueHandler;
import soot.Local;
import soot.SootMethod;
import soot.Unit;
import soot.jimple.Constant;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Stack;
//import sun.jvm.hotspot.oops.Instance;

public class RunTimeTaintData {
	int ID = 0;
	public boolean hasSource = false;

	public RunTimeTaintData() {
		ID = ++Envir.ID;
	}


	/*
	 * map of button ID to class name of OnclickListener it binds to
	 */
	HashMap<String, String> buttonToClass = new HashMap<String, String>();

	/*
	 * tainted locals and information in this RunTimeTaintData
	 */
	HashMap<Local, Constraint> tLocals = new HashMap<Local, Constraint>();

	/*
	 * tainted InstanceFieldRef and information in this RunTimeTaintData
	 */
	HashMap<String, Constraint> tInstanceFieldRefStr = new HashMap<String, Constraint>();

	/*
	 * constrains in this RunTimeTaintData
	 */
	List<Constraint> constrains = new ArrayList<Constraint>();

	/*
	 * all the units in this RunTimeTaintData
	 */
	List<SMethodUnit> allUnits = new ArrayList<SMethodUnit>();

	/*
	 * units of current method
	 */
	Stack<List<SMethodUnit>> tmpUnits = new Stack<List<SMethodUnit>>();

	/*
	 * Stack of methods
	 */
	Stack<SClassAndMethod> methods = new Stack<SClassAndMethod>();

	/*
	 * Stack of taintedarg
	 */
	Stack<List<TaintedArg>> taintedargs = new Stack<List<TaintedArg>>();

	/*
	 * the return value of last method
	 */
	IReturnValueHandler returnValueHandler;

	/*
	 * immediate value of local
	 */
	HashMap<Local, Constant> immValues = new HashMap<Local, Constant>();

	/*
	 * information
	 */
	HashMap<Local, HashMap<String, ITag>> tags = new HashMap<Local, HashMap<String, ITag>>();

	/*
	 * information
	 */
	HashMap<Local, Boolean> hasTransfered = new HashMap<Local, Boolean>();

	/*
	 * Track number of times a statement has been "goto", avoid infinite loops and while true loops
	 */
	HashMap<String, Integer> timesVisited = new HashMap<String, Integer>();

	/*
	 * immediate Value of some Local
	 *
	 * HashMap<Local, Object> immediateValueLocal = new HashMap<Local,
	 * Object>();
	 *
	 * //////////////// immediateValueLocal//////////////////////////// public
	 * boolean hasImmediateValue(Local local) { return
	 * immediateValueLocal.containsKey(local); }
	 *
	 * public Object getImmediateValue(Local local) { return
	 * immediateValueLocal.get(local); }
	 *
	 * public void deleteImmediateValue(Local local) { if
	 * (immediateValueLocal.containsKey(local))
	 * immediateValueLocal.remove(local); }
	 *
	 * public void setImmediateLocalValue(Local local, Object value) {
	 * immediateValueLocal.put(local, value); }
	 *
	 * public void setImmediateValueLocal(HashMap<Local, Object>
	 * immediateValueLocal) { this.immediateValueLocal = immediateValueLocal; }
	 */
	//////////////// hasTransfered////////////////////////////

	public void setHasTransfered(Local local, boolean bool) {
		hasTransfered.put(local, bool);
	}

	public boolean hasTransfered(Local local) {
		if (hasTransfered.containsKey(local))
			return hasTransfered.get(local);
		return false;
	}

	//////////////// tags////////////////////////////
	public ITag getTag(Local local, String name) {
		if (tags.containsKey(local))
			if (tags.get(local).containsKey(name)) {
				return tags.get(local).get(name);
			}
		return null;
	}

	public HashMap<String, ITag> getTags(Local local) {
		if (tags.containsKey(local))
			return tags.get(local);
		return null;
	}

	public void addTag(Local local, ITag tag) {
		if (!tags.containsKey(local))
			tags.put(local, new HashMap<String, ITag>());
		tags.get(local).put(tag.getName(), tag);
	}

	//////////////// immValues////////////////////////////
	public void setimmValues(HashMap<Local, Constant> immValues) {
		this.immValues = immValues;
	}

	public HashMap<Local, Constant> getimmValues() {
		return immValues;
	}

	//////////////// returnValue////////////////////////////
	public IReturnValueHandler getReturnValueHandler() {
		return returnValueHandler;
	}

	public void setReturnValueHandler(IReturnValueHandler returnValueHandler) {
		this.returnValueHandler = returnValueHandler;
	}

	//////////////// methods////////////////////////////
	public Stack<SClassAndMethod> getMethods() {
		return methods;
	}

	public void setMethods(Stack<SClassAndMethod> methods) {
		this.methods = methods;
	}

	public void pushMethods(SClassAndMethod sm) {
		methods.push(sm);
	}

	public SClassAndMethod getCurrentMethod() {
		return methods.peek();
	}

	public SClassAndMethod popMethod() {
		return methods.pop();
	}

	//////////////// taintedargs////////////////////////////
	public Stack<List<TaintedArg>> getTaintedargs() {
		return taintedargs;
	}

	public void setTaintedargs(Stack<List<TaintedArg>> taintedargs) {
		this.taintedargs = taintedargs;
	}

	public void pushTaintedArgs(List<TaintedArg> targs) {
		taintedargs.push(targs);
	}

	public List<TaintedArg> getCurrentTaintedArgs() {
		return taintedargs.peek();
	}

	public List<TaintedArg> popTaintedArgs() {
		return taintedargs.pop();
	}

	//////////////// buttonToCLass //////////////////////////
	public void addButtonToClass(String buttonID, String referClass) {
		buttonToClass.put(buttonID, referClass);
	}

	public HashMap<String, String> getButtonToClass() {
		return buttonToClass;
	}

	//////////////// tLocals////////////////////////////

	public void newTaintLocal(Local local, Constraint cos) {
		this.setHasTransfered(local, true);
		tLocals.put(local, cos);
	}

	public void newTaintInstanceFieldRefStr(String instanceFieldRefStr, Constraint cons) {
		tInstanceFieldRefStr.put(instanceFieldRefStr, cons);
	}

	public Constraint getTaintNode(Local local) {
		return tLocals.get(local);
	}

	public Constraint getTaintInstanceFieldRefStr(String instanceFieldRefStr) { return tInstanceFieldRefStr.get(instanceFieldRefStr); }

	public void transfer(Local to, Local from) {
		this.setHasTransfered(to, true);
		// propogation of tainted arguments by cloning constraints
		if (tLocals.containsKey(from)) {
			tLocals.put(to, tLocals.get(from).clone(to));
		}
		if (tags.containsKey(from)) {
			for (ITag tag : tags.get(from).values()) {
				this.addTag(to, tag);
			}
		}
	}

	public boolean contains(Local local) {
		return tLocals.containsKey(local);
	}

	public boolean contains(String instanceFieldRefStr) { return tInstanceFieldRefStr.containsKey(instanceFieldRefStr); }

	public HashMap<Local, Constraint> getTLs() {
		return tLocals;
	}

	public void removeLocalConstraint(Local local) {
		tLocals.remove(local);
	}

	/////////////////////////////// constrains////////////////
	public void addConstrain(Constraint cons) {
		constrains.add(cons);
	}

	public List<Constraint> getConstrains() {
		return constrains;
	}

	/////////////////////////////// units////////////////

	public void addUnittoAll(SMethodUnit unit) {
		allUnits.add(unit);
	}

	public int getAllUnitCount() {
		return allUnits.size();
	}

	public List<SMethodUnit> getAllUnits() {
		return allUnits;
	}

	public void addTmptoAllUnitAndClearIt() {
		List<SMethodUnit> t = tmpUnits.pop();
		for (SMethodUnit smu : t) {
			allUnits.add(smu);
		}
	}

	/////////////////////////////// tmpUnits////////////////
	public void newUnitTmp(List<SMethodUnit> units) {
		tmpUnits.push(units);
	}

	public void addUnittoTmp(SMethodUnit unit) {
		tmpUnits.peek().add(unit);
	}

	public int countofUnit(SootMethod sm, Unit u) {
		int count = 0;
		List<SMethodUnit> t = tmpUnits.peek();
		for (SMethodUnit tmp : t) {
			boolean sameSm = tmp.getSm().getSignature().equals(sm.getSignature());
			boolean sameU = tmp.getUnit().equals(u);
			if (sameSm && sameU) {
				count++;
			}
		}
		return count;
	}
	/////////////////////////////////////////////////////

	@SuppressWarnings("unchecked")
	public RunTimeTaintData clone() {
		RunTimeTaintData rttd = new RunTimeTaintData();
		for (Local local : this.tLocals.keySet()) {
			rttd.newTaintLocal(local, this.tLocals.get(local).clone(local));
		}
		for (Constraint cons : this.constrains) {
			rttd.addConstrain(cons);
		}
		for (SMethodUnit unit : this.allUnits) {
			rttd.addUnittoAll(unit);
		}
		for (List<SMethodUnit> units : this.tmpUnits) {

			List<SMethodUnit> tus = new ArrayList<SMethodUnit>();
			for (SMethodUnit unit : units)
				tus.add(unit);

			rttd.newUnitTmp(tus);
		}

		rttd.setMethods((Stack<SClassAndMethod>) this.methods.clone());
		rttd.setTaintedargs((Stack<List<TaintedArg>>) this.taintedargs.clone());
		rttd.setReturnValueHandler(this.getReturnValueHandler());
		rttd.setimmValues((HashMap<Local, Constant>) this.getimmValues().clone());
		for (Local local : tags.keySet()) {
			for (ITag tag : tags.get(local).values()) {
				rttd.addTag(local, tag);
			}
		}
		for (Local local : hasTransfered.keySet()) {
			rttd.setHasTransfered(local, hasTransfered.get(local));
		}

		// rttd.setImmediateValueLocal((HashMap<Local, Object>)
		// this.immediateValueLocal.clone());
		return rttd;
	}

	public String toString() {
		return "ID:" + this.ID;
	}


	// Tries to visit a node, if times visited for the node does not exceed a threshold, add 1 to timesVisited and return true
	// If times visited for the node exceeds a threshold. return false
	public boolean visiting(String unit) {
		if (!timesVisited.containsKey(unit)) {
			timesVisited.put(unit, 1);
			return true;
		}
		if (timesVisited.get(unit) < 10) {
			timesVisited.replace(unit, timesVisited.get(unit) + 1);
			return true;
		}
		return false;
	}

	// public static void main(String[] arg) {
	// Stack<String> aa = new Stack<String>();
	// aa.push("0");
	// aa.push("1");
	// aa.push("2");
	// aa.push("3");
	// for (String str : aa)
	// System.out.print(str);
	// System.out.print(aa.size());
	// }

	// public static void main(String[] args) {
	// HashMap<String, Object> immediateValueLocal = new HashMap<String,
	// Object>();
	// immediateValueLocal.put("1", 1);
	// immediateValueLocal.put("2", 2);
	// immediateValueLocal.put("2", 3);
	//
	// HashMap<String, Object> i2 = (HashMap<String, Object>)
	// immediateValueLocal.clone();
	// immediateValueLocal.clear();
	//
	// System.out.println(immediateValueLocal.size());
	// System.out.println(i2.size());
	// System.out.println(i2.get("2"));
	// }
}
