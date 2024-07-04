package com.company.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class MyLog {
	private static final Logger logger = LoggerFactory.getLogger("YBolic");

	public static void resrult(Object args) {
		logger.info(args.toString());
	}

	public static void info(Object args) {
		// logger.info(args.toString());
	}

	public static void info(Object tag, Object args) {
		// logger.info(tag.toString() + "\t" + args.toString());
	}

}
