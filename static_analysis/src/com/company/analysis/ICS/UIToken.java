package com.company.analysis.ICS;

import com.company.analysis.ICS.ProtocolIdentifiers;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import java.io.FileReader;

public class UIToken {

    public static byte[] retrieveUIStrings(String apkPath) {
        File apkF = new File(apkPath);
        if (!apkF.exists())
            throw new RuntimeException("file '" + apkPath + "' does not exist!");

        byte[] targetArray = new byte[]{};

        boolean found = false;
        try {
            ZipFile archive = null;
            try {
                archive = new ZipFile(apkF);
                Enumeration<?> entries = archive.entries();
                while (entries.hasMoreElements()) {
                    ZipEntry entry = (ZipEntry) entries.nextElement();
                    String entryName = entry.getName();
                    // We are dealing with the Android manifest
                    if (entryName.equals("resources.arsc")) {
                        found = true;
                        InputStream is = archive.getInputStream(entry);
                        targetArray = new byte[is.available()];
                        is.read(targetArray);
                        break;
                    }
                }
            }
            finally {
                if (archive != null)
                    archive.close();
            }
        }
        catch (Exception e) {
            throw new RuntimeException(
                    "Error when looking for strings.xml in apk: " + e);
        }
        if (!found)
            throw new RuntimeException("No strings.xml file found in apk");
        return targetArray;
    }

    public static boolean containsIdentifier(byte[] resourcesBytes, String identifier) {
        byte[] searchArray = identifier.getBytes(StandardCharsets.UTF_8);
        for(int i = 0; i < resourcesBytes.length - searchArray.length+1; ++i) {
            boolean found = true;
            for(int j = 0; j < searchArray.length; ++j) {
                if (resourcesBytes[i+j] != searchArray[j]) {
                    found = false;
                    break;
                }
            }
            if (found) return true;
        }
        return false;
    }

    public static void findUITokens(String packageName, String apkPath, String outputJsonPath) {

        byte[] resourcesBytes = retrieveUIStrings(apkPath);
        ProtocolIdentifiers pi = new ProtocolIdentifiers();
        HashMap<String, HashMap<String, String[]>> identifiers = pi.getProtocolIdentifiers();
        JSONParser jsonParser = new JSONParser();
        JSONObject protocolDict = new JSONObject();

        try (FileReader reader = new FileReader(outputJsonPath))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject PackageNameDict = (JSONObject) obj;

            JSONObject thisPackage = (JSONObject) PackageNameDict.get(packageName);
            protocolDict = (JSONObject) thisPackage.get("Protocols");
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        for (String protocol : identifiers.keySet()) {
            int count = 0;
            for (String UIToken : identifiers.get(protocol).get("Tokens")) {
                if (containsIdentifier(resourcesBytes, UIToken)) {
                    count += 1;
                }
            }
            int percentage = Math.round(count / identifiers.get(protocol).get("Tokens").length * 100);
            JSONObject curProtocolDict = (JSONObject) protocolDict.get(protocol);
            curProtocolDict.replace("Tokens", percentage);
        }

        try (FileReader reader = new FileReader(outputJsonPath))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject PackageNameDict = (JSONObject) obj;

            JSONObject thisPackage = (JSONObject) PackageNameDict.get(packageName);
            thisPackage.put("Protocols", protocolDict);
            PackageNameDict.put(packageName, thisPackage);

            //Write to ALLAPKINFO.JSON
            FileWriter file = new FileWriter(outputJsonPath);
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
}
