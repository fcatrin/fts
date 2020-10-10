package fts.linux;

import java.io.BufferedReader;
import java.io.InputStreamReader;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class LinuxUtils {

	private LinuxUtils() {}
	
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
            	result.append(s);
            	result.append("\n");
            }
            p.waitFor();
            p.destroy();
        } catch (Exception e) {}
        return result.toString();
	}

}
