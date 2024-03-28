package com.company.analysis.metaInfo;

import com.company.config.Envir;
import com.company.processAndroid.ProcessManifest;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Processing manifest information of the APK.
 */
public class Info {

    /**
     * Writes manifest information to the allAPKInfo.json file.
     * @return the package name for the current APK.
     */
    public static String writeManifestInfo(String path){

        //Process manifest information
        ProcessManifest manifest = new ProcessManifest();
        try {
            manifest.loadManifestFile(Envir.APK_PATH);
            System.out.println("Loaded manifest");
        } catch (Exception e) {
            e.printStackTrace();
        }

        JSONParser jsonParser = new JSONParser();

        try (FileReader reader = new FileReader(path))
        {
            //Read JSON file
            Object obj = jsonParser.parse(reader);
            JSONObject PackageNameDict = (JSONObject) obj;
            if (!PackageNameDict.containsKey(manifest.getPackageName())) {
                //Current app's all info
                JSONObject APKInfo = new JSONObject();

                //entrypointClasses
                JSONArray entryPointsClasses = new JSONArray();
                for (String classname : manifest.getEntryPointClasses()) {
                    entryPointsClasses.add(classname);
                }

                //permissions
                JSONArray permissions = new JSONArray();
                for (String permission : manifest.getPermissions()) {
                    permissions.add(permission);
                }

                //Gather All information
//                APKInfo.put("entryPointsClasses", entryPointsClasses);
//                APKInfo.put("permissions", permissions);
                APKInfo.put("applicationName", manifest.getApplicationName());
                APKInfo.put("versionCode", manifest.getVersionCode());
                APKInfo.put("versionName", manifest.getVersionName());
                APKInfo.put("minSdkVersion", manifest.getMinSdkVersion());
                APKInfo.put("targetSdkVersion", manifest.targetSdkVersion());

                //Add to ALLAPKINFO dict
                PackageNameDict.put(manifest.getPackageName(), APKInfo);

                //Write to ALLAPKINFO.JSON
                FileWriter file = new FileWriter(path);
                //We can write any JSONArray or JSONObject instance to the file
                file.write(PackageNameDict.toJSONString());
                file.flush();

            }
            return manifest.getPackageName();

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (ParseException e) {
            e.printStackTrace();
        }

        return manifest.getPackageName();
    }
}
