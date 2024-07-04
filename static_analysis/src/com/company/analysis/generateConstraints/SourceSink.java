package com.company.analysis.generateConstraints;

import java.util.ArrayList;
import java.util.List;

public class SourceSink {
    private List<String> sourceFunctions = new ArrayList<>();
    private List<String> sinkFunctions = new ArrayList<>();

    public void addSourceFunctions(List<String> toAdd) {
        for (String function : toAdd) {
            if (!sourceFunctions.contains(function)) sourceFunctions.add(function);
        }
    }

    public void addSinkFunctions(List<String> toAdd) {
        for (String function: toAdd) {
            if (!sinkFunctions.contains(function)) sinkFunctions.add(function);
        }
    }

    public List<String> getSourceFunctions() {
        return sourceFunctions;
    }

    public List<String> getSinkFunctions() {
        return sinkFunctions;
    }
}
