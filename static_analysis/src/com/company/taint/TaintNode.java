package com.company.taint;

import soot.Local;

import java.util.ArrayList;
import java.util.List;

public class TaintNode {

	TaintNode fromNode;
	/**
	 * tainted variable
	 */
	Local local;

	List<ITag> tags = new ArrayList<ITag>();

	public TaintNode(Local local) {
		this.local = local;
	}

	public List<ITag> getTags() {
		return tags;
	}
	
	public void addTag(ITag tag) {
		tags.add(tag);
	}

	public ITag getTag(String str) {
		for (ITag tag : tags) {
			if (tag.getName().equals(str)) {
				return tag;
			}
		}
		return null;
	}

	public TaintNode getFromNode() {
		return fromNode;
	}

	public void setFromNode(TaintNode fromNode) {
		this.fromNode = fromNode;
	}

	public Local getLocal() {
		return local;
	}

	public void setLocal(Local local) {
		this.local = local;
	}

	public TaintNode clone(Local tlocal) {
		TaintNode tn = new TaintNode(tlocal);
		tn.setFromNode(this);
		for (ITag tag : tags)
			tn.addTag(tag);
		return tn;
	}
	
	public TaintNode clone() {
		TaintNode tn = new TaintNode(local);
		tn.setFromNode(fromNode);
		for (ITag tag : tags)
			tn.addTag(tag);
		return tn;
	}

	public String toString() {
		String str = local.toString() + "\n";
		for (ITag tag : tags) {
			str += "(" + tag.getName() + "," + tag.getData().toString() + ")";
		}
		return str;
	}
}
