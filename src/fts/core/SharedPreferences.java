package fts.core;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.json.JSONObject;

public class SharedPreferences {
	private static final String ENCODING = "UTF-8";
	File prefsFile;
	JSONObject prefs = null;
	
	public SharedPreferences(String name) {
		prefsFile = new File(System.getProperty("user.home") + "/." + name + "/conf.json");
		File parent = prefsFile.getParentFile();
		if (!parent.exists()) parent.mkdirs();
	}
	
	public JSONObject load() {
		if (prefs!=null) return prefs;
		
		prefs = new JSONObject();
		if (prefsFile.exists()) {
			try {
				String sPrefs = Utils.loadString(prefsFile, ENCODING);
				prefs = new JSONObject(sPrefs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return prefs;
	}
	
	public void commit() {
		try {
			Utils.saveBytes(prefsFile, prefs.toString().getBytes(ENCODING));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
