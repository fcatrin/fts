package fts.core;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DesktopResourceLocator extends ResourceLocator {

	@Override
	public InputStream getResource(String location) throws IOException {
		return DesktopResourceLocator.class.getResourceAsStream("/fts.res/" + location);
	}

	@Override
	public File extract(String location) throws IOException {
		InputStream is = getResource(location);
		File tmpFile = new File(System.getProperty("java.io.tmpdir"), location);
		tmpFile.getParentFile().mkdirs();
		
		if (tmpFile.exists()) tmpFile.delete();
		FileOutputStream fos = new FileOutputStream(tmpFile);
		Utils.copyFile(is, fos);
		return tmpFile;
	}

}
