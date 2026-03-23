package fts.core;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;

public abstract class ResourceLocator {
	
	public abstract InputStream getResource(String location) throws IOException;
	public abstract File extract(String location) throws IOException;
	
	public boolean exists(String location) {
		InputStream is = null;
		try {
			is = getResource(location);
			if (is!=null) is.close();	
		} catch (IOException e) {
			e.printStackTrace();
		}
		return is!=null;
	}

	public static void loadLibrary(String name) {
		try {
			System.loadLibrary(name);
		} catch (Throwable e) {
			loadLibraryFromFilesystem(name);
		}
	}

	private static void loadLibraryFromFilesystem(String name) {
		String fullName = name + getNativeLibraryExtension();
		File jarDir = FileUtils.getJarDir(ResourceLocator.class);
		File path = new File(jarDir, "native/lib" + fullName);
		System.load(path.getAbsolutePath());
	}

	private static String getNativeLibraryExtension() {
		String os = System.getProperty("os.name").toLowerCase();
		if (os.contains("win")) {
			return ".dll";
		} else if (os.contains("mac")) {
			return ".dylib";
		}
		return ".so";
	}
}
