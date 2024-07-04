package com.company.config;

import soot.Scene;
import soot.SootClass;
import soot.options.Options;

import java.util.Collections;
import java.util.LinkedList;
import java.util.List;

/**
 * Initializing soot with proper parameters and options.
 */
public class InitSoot {

    /**
     * Set soot options.
     */
    public static void initOptions() {
        // ##################my setting###########################
        soot.G.reset();
        setSootOptions();
        Options.v().set_src_prec(Options.src_prec_apk);
        Options.v().set_process_dir(Collections.singletonList(Envir.APK_PATH));
        System.out.println("Processing: " + Envir.APK_PATH);
        Options.v().set_android_jars(Envir.ANDROID_JAR_DIR);
        System.out.println("Android jar directory:" + Envir.ANDROID_JAR_DIR);
        // Options.v().set_soot_classpath(ClassPath.get());
        Options.v().set_whole_program(true);
        Options.v().set_allow_phantom_refs(true);
        Options.v().set_output_format(Options.output_format_none);
        // Options.v().setPhaseOption("cg.spark", "on");
        // Options.v().setPhaseOption("cg.spark", "string-constants:true");
        Options.v().ignore_resolution_errors();
        Options.v().set_process_multiple_dex(true);
//        try{
//            SootClass a11y = Scene.v().loadClassAndSupport("com.tencent.qqpimsecure.p02e5baf0");
//            System.out.println("A11y: " + a11y.toString());
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//        SootClass A11yClass = Scene.v().loadClass("com.tencent.qqpimsecure.pa70532bf", 2);
//        System.out.println("");
        Scene.v().loadNecessaryClasses();


    }

    /**
     * Exclude processing of system libraries.
     */
    public static void setSootOptions() {
        // explicitly exclude packages for shorter runtime:
        List<String> excludeList = new LinkedList<String>();
        excludeList.add("java.*");
        excludeList.add("sun.misc.*");
        excludeList.add("android.*");
        excludeList.add("org.apache.*");
        excludeList.add("soot.*");
        excludeList.add("javax.servlet.*");
        excludeList.add("javax.security.*");
        Options.v().set_exclude(excludeList);

        List<String> includeList = new LinkedList<String>();
        includeList.add("org.apache.http.impl.client.*");
        includeList.add("java.net.HttpURLConnection");
        includeList.add("java.net.URLConnection");
        includeList.add("javax.net.ssl.HttpsURLConnection");
        Options.v().set_include(includeList);
        // Options.v().set_no_bodies_for_excluded(true);
    }

}
