package com.company.symbolic;

import com.company.data.RunTimeTaintData;

import java.util.List;

public interface IPathSelector {

	public RunTimeTaintData select(List<RunTimeTaintData> rtts);

}
