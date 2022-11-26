package fts.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

public class DesktopResourceLocator extends ResourceLocator {

	@Override
	public InputStream getResource(String location) throws IOException {
		InputStream is = DesktopResourceLocator.class.getResourceAsStream("/" + location);
		if (is!=null) return is;

		String resourcesDir = System.getProperty("fts.resources.dir");
		if (resourcesDir!=null) {
			File resourcesFile = new File(resourcesDir, location);
			System.out.println("load resource from " + resourcesFile);
			if (resourcesFile.exists()) return new FileInputStream(resourcesFile);
		}
		return null;
	}

	@Override
	public File extract(String location) throws IOException {
		InputStream is = getResource(location);
		File tmpFile = new File(System.getProperty("java.io.tmpdir"), location);
		tmpFile.getParentFile().mkdirs();
		
		if (tmpFile.exists()) tmpFile.delete();
		FileOutputStream fos = new FileOutputStream(tmpFile);
		CoreUtils.copyFile(is, fos);
		return tmpFile;
	}

}
