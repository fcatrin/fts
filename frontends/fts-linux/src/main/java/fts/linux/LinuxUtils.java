package fts.linux;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;

import fts.core.CoreUtils;
import fts.core.FileUtils;

public class LinuxUtils {

	private static Map<String, String> osReleaseValues = new HashMap<>();

	static {
		try {
			loadOsReleaseValues();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private LinuxUtils() {}

	public static String getUserName() {
		String cmd[] = {"id", "-nu"};
		return exec(cmd);
	}
	
	public static String getIPAddress() throws JSONException {
		String cmd[] = {"ip", "-j", "-o", "-f", "inet", "addr"};
		String json = exec(cmd);
		JSONArray addresses = new JSONArray(json);
		for(int i=0; i<addresses.length(); i++) {
			JSONObject group = addresses.getJSONObject(i);
			JSONArray subgroup = group.getJSONArray("addr_info");
			for(int s=0; s<subgroup.length(); s++) {
				JSONObject net = subgroup.getJSONObject(s);
				if ("lo".equals(net.optString("dev"))) continue;
				String addr = net.getString("local");
				return addr;
			}
		}
		return "Unknown";
	}
	
	public static String exec(String args[]) {
		StringBuffer result = new StringBuffer();
		String s;
        Process p;
        try {
            p = Runtime.getRuntime().exec(args);
            BufferedReader br = new BufferedReader(new InputStreamReader(p.getInputStream()));
            while ((s = br.readLine()) != null)  {
            	if (result.length()>0) result.append("\n");
            	result.append(s);
            }
            p.waitFor();
            p.destroy();
        } catch (Exception e) {}
        return result.toString();
	}

	private static void loadOsReleaseValues() throws IOException {
		String osReleaseText = CoreUtils.loadString(new File("/etc/os-release"));
		String[] lines = osReleaseText.split("\n");
		for(String line : lines) {
			String[] parts = line.split("=");
			if (parts.length != 2) continue;
			String key = parts[0];
			String value = CoreUtils.removeStartEnd(parts[1], "\"");
			osReleaseValues.put(key, value);
		}
	}

	public static String getOSReleaseValue(String key) {
		return osReleaseValues.get(key);
	}

}
