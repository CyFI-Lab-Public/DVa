package com.company.analysis.metaInfo;

import soot.Scene;
import soot.SootClass;
import soot.SootMethod;
import soot.options.Options;

import java.util.List;

public class SearchClassTest {

    public static void searchClasses () {
        for (SootClass sc : Scene.v().getClasses()) {
            for (SootMethod sm : sc.getMethods()) {
                if (sm.toString().contains("performAction") || sm.toString().contains("performGlobalAction")) {
                    System.out.println(sm);
                }
            }

            if (sc.toString().contains("com.boda.thanksnexzintentecachehunablevgaryzdaybdivorceastructuresdpersonyconsecutivezcouncilybudapestq32")) {
                System.out.println("Entered AccessibilityService Implementation");
                List<SootMethod> sootMethodList = sc.getMethods();
                for (SootMethod sm: sootMethodList) {
                    System.out.println(sm.toString());
                }
            }
        }
    }

    public static void searchBasicClasses() {
        System.out.println("Basic Classes:");
        for (String str: Scene.v().getBasicClasses()) {
            System.out.println("Basic Class: " + str);
        }
        System.out.print("END Basic Classes");
    }

    public static void searchApplicationClasses() {
        System.out.println("Application Classes:");
        for (SootClass sc: Scene.v().getApplicationClasses()) {
            System.out.println("Application Class: " + sc);
        }
        System.out.print("END Application Classes");
    }

    public static void searchLibraryClasses() {
        System.out.println("Library Classes:");
        for (SootClass sc: Scene.v().getLibraryClasses()) {
            System.out.println("Library Class: " + sc);
        }
        System.out.print("END Library Classes");
    }

    public static void searchPhantomClasses() {
        System.out.println("Phantom Classes:");
        for (SootClass sc: Scene.v().getPhantomClasses()) {
            System.out.println("Phantom Class: " + sc);
        }
        System.out.print("END Phantom Classes");
    }

    public static void getMainClass() {
        System.out.println("Main Class: " + Scene.v().getMainClass());
    }
}
