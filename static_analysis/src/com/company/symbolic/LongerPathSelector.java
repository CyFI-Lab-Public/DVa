package com.company.symbolic;

import com.company.data.RunTimeTaintData;

import java.util.List;

public class LongerPathSelector implements IPathSelector{

	@Override
	public RunTimeTaintData select(List<RunTimeTaintData> rtts) {
		// TODO Auto-generated method stub
		RunTimeTaintData target = null;
		for (RunTimeTaintData tmp : rtts) {
			if (target == null || target.getAllUnitCount() < tmp.getAllUnitCount()) {
				target = tmp;
			}
		}
		return target;
	}

}
