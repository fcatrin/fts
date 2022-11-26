package fts.android.gl;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import fts.core.ResourceLocator;
import fts.core.Utils;

public class AndroidGLResourceLocator extends ResourceLocator {

	private Context context;

	public AndroidGLResourceLocator(Context context) {
		this.context = context;
	}
	
	@Override
	public InputStream getResource(String location) throws IOException {
		String path = "fts/" + location;
		return context.getAssets().open(path);
	}

	@Override
	public File extract(String location) throws IOException {
		InputStream is = getResource(location);
		if (is == null) return null;
		
		File localResDir = new File(context.getFilesDir(), "fts");
		File localResFile = new File(localResDir, location);
		localResFile.getParentFile().mkdirs();
		localResFile.delete();
		
		FileOutputStream fos = new FileOutputStream(localResFile);
		Utils.copyFile(is, fos);
		return localResFile;
	}

}