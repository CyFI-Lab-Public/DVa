package com.company.util;

public class LogFileUtil {

	static Object obj = new Object();
	static String LOGDIR = "log/tmp";
	static String CONSDIR = "log/constraint";
	static String BUTTONTOCLASSDIR = "log/buttonToClass";
	static String ALLCANDIR = "log/allcan";

	public static void log(String msg) {
		synchronized (obj) {
			checkLogDir();
			FileUtil.write("errorLog.txt", (msg + "\n").getBytes(), true);
		}
	}

	public static void json(String msg) {
		synchronized (obj) {
			checkLogDir();
			FileUtil.write("cons.txt", (msg + "\n").getBytes(), true);
		}
	}


	public static void http(String msg) {
		synchronized (obj) {
			checkLogDir();
			FileUtil.write("http.txt", (msg + "\n").getBytes(), true);
		}
	}

	public static void helpInfo(String msg) {
		synchronized (obj) {
			checkLogDir();
			FileUtil.write("log/helpinfo.txt", (msg + "\n").getBytes(), true);
		}
	}

	public static void logFile(String fname, String msg) {
		synchronized (obj) {
			checkLogDir();
			FileUtil.write(LOGDIR + "/" + fname, (msg + "\n").getBytes(), true);
		}
	}

	public static void consFile(String fname, String msg) {
		synchronized (obj) {
			checkLogDir();
			FileUtil.write(CONSDIR + "/" + fname, (msg + "\n").getBytes(), true);
		}
	}

	public static void buttonToClassFile(String fname, String msg) {
		synchronized (obj) {
			checkLogDir();
			FileUtil.write(BUTTONTOCLASSDIR + "/" + fname, (msg + "\n").getBytes(), true);
		}
	}

	public static void canLoadURLFile(String fname, String msg) {
		synchronized (obj) {
			checkLogDir();
			FileUtil.write(ALLCANDIR + "/" + fname, (msg + "\n").getBytes(), true);
		}
	}

	public static void canJumpFile(String fname, String msg) {
		synchronized (obj) {
			checkLogDir();
			FileUtil.write(ALLCANDIR + "/" + fname, (msg + "\n").getBytes(), true);
		}
	}

	public static void checkLogDir() {
		FileUtil.newDir(LOGDIR);
		FileUtil.newDir(CONSDIR);
		FileUtil.newDir(BUTTONTOCLASSDIR);
		FileUtil.newDir(ALLCANDIR);
	}

}
