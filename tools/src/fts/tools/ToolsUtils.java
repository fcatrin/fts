package fts.tools;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class ToolsUtils {
	private static final int BUF_SIZE = 16384;

	public static void copyFile(File src, File dst) throws IOException {
		FileInputStream is = new FileInputStream(src);
		FileOutputStream os = new FileOutputStream(dst);
		copyFile(is, os);
	}
	
	public static void copyFile(InputStream is, OutputStream os) throws IOException {
        byte buffer[] = new byte[BUF_SIZE];
        
		int bufferLength = 0;

		try {
			while ((bufferLength = is.read(buffer)) > 0) {
				os.write(buffer, 0, bufferLength);
			}
		} finally {
			is.close();
			os.close();
		}
	}
	
	public static void delTree(File dir) {
		if (!dir.exists() || !dir.isDirectory()) return;
		if (dir.listFiles() != null) {
			for(File f : dir.listFiles()) {
				if (f.isDirectory()) delTree(f);
				else f.delete();
			}
		}
		dir.delete();
	}

}
