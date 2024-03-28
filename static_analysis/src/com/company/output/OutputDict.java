package com.company.output;

import com.company.config.Envir;
import org.json.simple.JSONObject;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class OutputDict {
    public static Map<String, Object> output;

    static {
        output = new HashMap<>();
        output.put("Errors", new ArrayList<String>());
        output.put("SinkToConcreteInvokes", new HashMap<String, List<String>>());
        output.put("SinkToCallChains", new HashMap<String, List<List<String>>>());
    }

    public static void addError(String error) {
        List<String> errors = (List<String>) output.get("Errors");
        errors.add(error);
    }

    public static void addSinkToConcreteInvokes(String sink, List<String> concreteInvokes) {
        Map<String, List<String>> sinkToConcreteInvokes = (Map<String, List<String>>) output.get("SinkToConcreteInvokes");
        sinkToConcreteInvokes.put(sink, concreteInvokes);
    }

    public static void addSinkToCallChains(String sink, List<List<String>> callChains) {
        Map<String, List<List<String>>> sinkToCallChains = (Map<String, List<List<String>>>) output.get("SinkToCallChains");
        sinkToCallChains.put(sink, callChains);
    }

    public static void writeOutput() {
        String apkName = Envir.APK_NAME.substring(0, Envir.APK_NAME.indexOf(".apk"));
        String outPath = Envir.OUTPUT_PATH + "/" + apkName + ".json";

        JSONObject curOutput = new JSONObject(output);
        try {
            FileWriter file = new FileWriter(outPath);
            file.write(curOutput.toJSONString());
            file.flush();
            file.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
