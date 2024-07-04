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
//        output.put("Persistence Mechanisms", new HashMap<String, List<String>>());
//        output.put("Abuse Vectors", new HashMap<String, List<List<String>>>());
        output.put("Persistence Mechanisms", new HashMap<>());
        output.put("Abuse Vectors", new HashMap<>());
    }

    public static void addError(String error) {
        List<String> errors = (List<String>) output.get("Errors");
        errors.add(error);
    }

    public static void addSinkToConcreteInvokes(String sink, List<String> triggers) {
        String label = "";
        String victim = "generic";
        for (String trigger : triggers) {
            if (trigger.toLowerCase().contains("inputtext")) {
                label = "prevent a11y permission revocation";
                victim = "com.android.settings";
                break;
            }
            else if (trigger.toLowerCase().contains("onclick")) {
                label = "prevent info lookup or uninstall";
                victim = "com.android.settings";
                break;
            }
            else if (trigger.toLowerCase().contains("home")) {
                label = "prevent info lookup or uninstall";
                victim = "com.android.settings";
                break;
            }
            else if (trigger.toLowerCase().contains("google")) {
                label = "disable device protection";
                victim = "com.android.settings";
                break;
            }
            else if (trigger.toLowerCase().contains("admin")) {
                label = "escalating privileges";
                victim = "com.android.settings";
                break;
            }
            else if (trigger.toLowerCase().contains("battery")) {
                label = "disable power options";
                victim = "com.android.settings";
                break;
            }
            else if (trigger.toLowerCase().contains("launchintent")) {
                label = "disable device protection";
                victim = "com.android.settings";
                break;
            }

        }
        Map<String, Object> sinkToConcreteInvokes = new HashMap<>();
        sinkToConcreteInvokes.put("label", label);
        sinkToConcreteInvokes.put("victim", victim);
        sinkToConcreteInvokes.put("triggers", triggers);
        Map<String, Map<String, Object>> persistenceMechanisms = (Map<String, Map<String, Object>>) output.get("Persistence Mechanisms");
        persistenceMechanisms.put(sink, sinkToConcreteInvokes);
    }

    public static void addSinkToCallChains(String sink, List<List<String>> callChains) {
        String label = "";
        List<String> victims = new ArrayList<>();
        if (sink.contains("performAction(int,android.os.Bundle)")) {
            label = "Steals credentials";
            if (Envir.apkName.contains("pixstealer")) {
                victims.add("com.nu.production");
                victims.add("br.com.intermedium");
            }
        }
        else if (sink.contains("performAction(int)")) {
            label = "automatic transaction";
            if (Envir.apkName.contains("pixstealer")) {
                victims.add("com.nu.production");
                victims.add("br.com.intermedium");
            }
        }
        else {
            boolean shouldBreak = false;
            for (List<String> callChain : callChains) {
                if (shouldBreak) {
                    break;
                }
                for (String call : callChain) {
                    if (call.toLowerCase().contains("auth")) {
                        shouldBreak = true;
                        label = "steal authentication code";
                        if (Envir.apkName.contains("pixstealer")) {
                            victims.add("com.nu.production");
                            victims.add("br.com.intermedium");
                        }
                        break;
                    } else if (call.toLowerCase().contains("notification")) {
                        shouldBreak = true;
                        label = "hide or delete notification";
                        if (Envir.apkName.contains("pixstealer")) {
                            victims.add("com.nu.production");
                            victims.add("br.com.intermedium");
                        }
                        break;
                    } else if (call.toLowerCase().contains("ussd")) {
                        shouldBreak = true;
                        label = "ussd code";
                        if (Envir.apkName.contains("pixstealer")) {
                            victims.add("com.nu.production");
                            victims.add("br.com.intermedium");
                        }
                        break;
                    } else if (call.toLowerCase().contains("phone")) {
                        shouldBreak = true;
                        label = "fake calls";
                        if (Envir.apkName.contains("pixstealer")) {
                            victims.add("com.nu.production");
                            victims.add("br.com.intermedium");
                        }
                        break;
                    }
                }
            }
        }
        Map<String, Object> sinkToCallChains = new HashMap<>();
        sinkToCallChains.put("label", label);
        sinkToCallChains.put("victims", victims);
        sinkToCallChains.put("callChains", callChains);
        Map<String, Map<String, Object>> abuseVectors = (Map<String, Map<String, Object>>) output.get("Abuse Vectors");
        abuseVectors.put(sink, sinkToCallChains);
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
