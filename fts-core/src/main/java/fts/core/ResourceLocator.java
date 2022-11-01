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
}
