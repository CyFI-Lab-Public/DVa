package com.company;

import java.io.*;
import java.lang.System;

import com.company.analysis.generateConstraints.A11yConstraints;
import com.company.analysis.metaInfo.Info;
import com.company.analysis.ICS.Protocols;
import com.company.analysis.metaInfo.SearchClassTest;
import com.company.config.Envir;
import com.company.config.InitSoot;
import com.company.output.OutputDict;
//import com.sun.static_analysis.internal.ws.wsdl.document.Output;

/**
 * Statically determine properties of an APK and writes to allAPKInfo.json.
 */
public class Main {

    /**
     * Get info of single APK file
     */
    public static void processSingleAPK() {
        System.out.println("APK: " + Envir.INPUT_PATH);
        Envir.APK_PATH = Envir.INPUT_PATH;
        Envir.APK_NAME = Envir.INPUT_PATH.substring(Envir.INPUT_PATH.lastIndexOf("/") + 1);

        //Init Soot
        try {
            InitSoot.initOptions();
        } catch (Exception e) {
            e.printStackTrace();
            OutputDict.addError(e.getMessage());
        }

        A11yConstraints a11yConstraints = new A11yConstraints();
        a11yConstraints.generateA11yConstraints();

        OutputDict.writeOutput();

//        //Get manifest information
//        String packageName = Info.writeManifestInfo(Envir.APKINFO_JSON);
//
//        //Protocol identification
//        Protocols.findProtocols(packageName, Envir.APKINFO_JSON);
//
//        // Output UI tokens
//        UIToken.findUITokens(packageName, path, Envir.APKINFO_JSON);
    }

    /**
     * Operation modes:
     * @param args operation mode("single" or "all"), path
     */
    public static void main(String[] args) {

//        // Constant paths
//        Envir.ANDROID_JAR_DIR = System.getenv("ANDROID_JAR");

        // Parse operation mode arguments
        if (args.length != 2) {
            System.out.println("Incorrect arguments, input path, and output report path");
            System.exit(1);
        }

        String inputPath = args[0];
        String outputPath = args[1];

        if (!inputPath.endsWith(".apk")) {
            System.out.println("Input path is not an APK file");
            System.exit(1);
        }

        Envir.apkName = inputPath;
        Envir.INPUT_PATH = inputPath;
        Envir.OUTPUT_PATH = outputPath;

        processSingleAPK();

        System.exit(0);
    }



}
