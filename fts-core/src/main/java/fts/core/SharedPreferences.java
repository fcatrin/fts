package fts.core;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;

public class SharedPreferences {
	private static final String ENCODING = "UTF-8";
	File prefsFile;
	JSONObject prefs = null;
	
	public SharedPreferences(File dataDir, String name) {
		prefsFile = new File(dataDir, "prefs_" + name + ".json");
		File parent = prefsFile.getParentFile();
		if (!parent.exists()) parent.mkdirs();
		
		load();
	}

	public boolean has(String key) {
		return prefs.has(key);
	}

	public JSONArray getJSONArray(String key) {
		return prefs.optJSONArray(key);
	}

	public void putJSONArray(String key, JSONArray array) {
		prefs.put(key, array);
	}

	public String getString(String key) {
		return getString(key, null);
	}

	public String getString(String key, String defaultValue) {
		return prefs.optString(key, defaultValue);
	}

	public boolean getBoolean(String key) {
		return getBoolean(key, false);
	}

	public boolean getBoolean(String key, boolean defaultValue) {
		return prefs.optBoolean(key, defaultValue);
	}

	public int getInt(String key) {
		return getInt(key, 0);
	}

	public int getInt(String key, int defaultValue) {
		return prefs.optInt(key, defaultValue);
	}

	public long getLong(String key) {
		return getLong(key, 0l);
	}

	public long getLong(String key, long defaultValue) {
		return prefs.optLong(key, defaultValue);
	}

	public void setString(String key, String value) {
		prefs.put(key, value);
	}
	
	public void setInt(String key, int value) {
		prefs.put(key, value);
	}

	public void setLong(String key, long value) {
		prefs.put(key, value);
	}

	public void setBoolean(String key, boolean value) {
		prefs.put(key, value);
	}

	public void remove(String key) {
		prefs.remove(key);
	}
	
	private void load() {
		prefs = new JSONObject();
		if (prefsFile.exists()) {
			try {
				String sPrefs = CoreUtils.loadString(prefsFile, ENCODING);
				prefs = new JSONObject(sPrefs);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
	}
	
	public void commit() {
		try {
			CoreUtils.saveBytes(prefsFile, prefs.toString().getBytes(ENCODING));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
