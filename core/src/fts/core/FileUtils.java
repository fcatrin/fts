package fts.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import fts.vfile.VirtualFileOperationProgressListener;

public class FileUtils {
	private static final int STREAM_BUF_SIZE = 0x10000;
	
	public static void copyFile(String progressInfo, InputStream is, OutputStream os, VirtualFileOperationProgressListener progressListener, long max) throws IOException {
	    byte buffer[] = new byte[STREAM_BUF_SIZE ];
		int bufferLength = 0;

		if (progressListener!=null) progressListener.updateProgress(progressInfo, 0, (int)max);
		try {
			int pos = 0;
			while ((bufferLength = is.read(buffer)) > 0) {
				os.write(buffer, 0, bufferLength);
				pos += bufferLength;
				if (progressListener!=null) {
					progressListener.updateProgress(progressInfo, pos, (int)max);
					if (progressListener.isCancelled()) break;
				}
			}
		} finally {
			try {
				is.close();
			} catch (Exception e) {}
			try {
				os.close();
			} catch (Exception e) {}
		}
	}
	
	public static int copyTree(File initialRoot, File root, File dstDir, VirtualFileOperationProgressListener listener, int count, int max) throws IOException {
		File files[] = root.listFiles();
		if (files == null) return count;
		
		File newDir = new File(dstDir, root.getName());
		newDir.mkdir();
		
		String info = "Copying files and folders {count}";
		for(File file: files) {
			listener.updateProgress(info, count, max);

			if (file.isDirectory()) count = copyTree(initialRoot, file, newDir, listener, count, max);
			else {
				String fileName = file.getAbsolutePath().substring(initialRoot.getAbsolutePath().length()+1);
				File newFile = new File(newDir, file.getName());
				copyFile(fileName, file, newFile, listener);
			}
			
		}
		return count;
	}
	
	public static int delTree(File initialRoot, File root, VirtualFileOperationProgressListener listener, int count, int max) throws IOException {
		File files[] = root.listFiles();
		
		if (files == null) return count;
		
		for(File file: files) {
			String fileName = file.getAbsolutePath().substring(initialRoot.getAbsolutePath().length()+1);
			String info = "Deleting " + fileName + " {count}";
			listener.updateProgress(info, count, max);

			if (file.isDirectory()) count = delTree(initialRoot, file, listener, count, max);
			else {
				file.delete();
				count ++;
			}
		}
		return count;
	}
	
	public static void copyFile(String fileName, File srcPhysicalFile, File dstPhysicalFile, VirtualFileOperationProgressListener progressListener) throws IOException {
		long max = srcPhysicalFile.length();
		FileInputStream is = new FileInputStream(srcPhysicalFile);
		FileOutputStream os = new FileOutputStream(dstPhysicalFile);
		String info = "Copying file " + fileName;
		copyFile(info, is, os, progressListener, max);		
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
