package com.company.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class FileUtil {

	public static void write(String path, byte[] data, boolean append) {
		try {
			FileOutputStream out = new FileOutputStream(path, append);
			out.write(data);
			out.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void newDir(String dir) {
		File f = new File(dir);
		if (!f.exists())
			f.mkdirs();
	}
}
