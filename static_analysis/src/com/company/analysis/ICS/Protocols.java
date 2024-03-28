package com.company.analysis.ICS;

import com.company.analysis.ICS.ProtocolIdentifiers;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import soot.Scene;
import soot.SootClass;
import soot.SootMethod;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

/**
 * Identifies the industrial protocol usage of an APK.
 */
public class Protocols {

    /**
     * Initialize the protocol dict with zero counts of APIs and Tokens.
     * @param protocolDict Dict to be initialized.
     */
    private static void initProtocolDict(JSONObject protocolDict) {
        protocolDict.put("Modbus TCP", new JSONObject());
        protocolDict.put("Https", new JSONObject());
        protocolDict.put("Http", new JSONObject());
        protocolDict.put("Ethernet/IP", new JSONObject());
        protocolDict.put("OPC UA", new JSONObject());
        protocolDict.put("Fins", new JSONObject());
        protocolDict.put("MQTT", new JSONObject());
        protocolDict.put("S7", new JSONObject());

        for (Object key : protocolDict.keySet()) {
            JSONObject protocolCounts = (JSONObject) protocolDict.get(key);
            protocolCounts.put("APIs", 0);
            protocolCounts.put("Tokens", 0);
        }
    }

    /**
     * Finds the occurrences of unique predefined APIs in methods
     * Unique occurrences, not cumulated number of appearances
     * @param protocolDict output dict
     * @param protocolIdentifiers predefined identifiers
     */
    private static void findProtocolAPIs(JSONObject protocolDict, HashMap<String, HashMap<String, String[]>> protocolIdentifiers) {
        boolean APIFound = false;

        for (Object key : protocolDict.keySet()) {
            String[] curProtocolAPIs = protocolIdentifiers.get(key).get("APIs");
            JSONObject protocolCounts = (JSONObject) protocolDict.get(key);
            if (curProtocolAPIs.length == 0) {
                protocolCounts.replace("APIs", 0);
                continue;
            }
            int count = 0;
            for (String API : curProtocolAPIs) {
                for (SootClass sc : Scene.v().getClasses()) {
                    List<SootMethod> sootMethodList = sc.getMethods();
                    for (SootMethod sm : sootMethodList) {
                        if (sm.toString().toLowerCase().contains(API.toLowerCase())) {
                            count++;
                            APIFound = true;
                            break;
                        }
                    }
                    if (APIFound) {
                        APIFound = false;
                        break;
                    }
                }
            }
            int percentage = Math.round(count / curProtocolAPIs.length * 100);
            protocolCounts.replace("APIs", percentage);
        }


    }

    /**
     * Writes the occurrences of APIs to json
     * @param packageName current processing APK package
     * @param path path to the output json
     * @param protocolDict the filled APIs count dict
     */
    private static void writeDictToJson(String packageName, String path, JSONObject protocolDict) {
        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(path))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject PackageNameDict = (JSONObject) obj;

            JSONObject thisPackage = (JSONObject) PackageNameDict.get(packageName);
            thisPackage.put("Protocols", protocolDict);
            PackageNameDict.put(packageName, thisPackage);

            //Write to ALLAPKINFO.JSON
            FileWriter file = new FileWriter(path);
            //We can write any JSONArray or JSONObject instance to the file
            file.write(PackageNameDict.toJSONString());
            file.flush();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }
    }


    /**
     * Find the protocol usage by keyword match for methods in the APK and writes protocol usage to allAPKInfo.json.
     * @param packageName current processing APK package
     */
    public static void findProtocols(String packageName, String path) {

        // Get protocol identifiers
        ProtocolIdentifiers pi = new ProtocolIdentifiers();
        HashMap<String, HashMap<String, String[]>> protocolIdentifiers = pi.getProtocolIdentifiers();

        // Dict that contain all protocols identified and the number of instances of APIs and UI tokens found for that each protocol
        JSONObject protocolDict = new JSONObject();

        // Initialize counts to zero
        initProtocolDict(protocolDict);

        // Fill in occurrences of unique API identifiers
        findProtocolAPIs(protocolDict, protocolIdentifiers);

        // Writes the found occurrences of APIs to json
        writeDictToJson(packageName, path, protocolDict);
    }

}
