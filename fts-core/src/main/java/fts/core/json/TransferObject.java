package fts.core.json;

import org.json.JSONException;
import org.json.JSONObject;

import fts.core.Utils;

public class TransferObject {
	protected String oid;

	public String getOid() {
		return oid;
	}

	public void setOid(String oid) {
		this.oid = oid;
	}
	
	public boolean isNew() {
		return oid == null;
	}
	
	protected JSONObject toJSON() throws JSONException {
		if (oid == null) oid = Utils.genOid();
		JSONObject o = new JSONObject();
		o.put("oid", oid);
		return o;
	}
	
	protected static void initFromJSON(TransferObject to, JSONObject o) {
		String oid = o.optString("oid", null);
		if (oid == null) oid = Utils.genOid();
		to.oid = oid;
	}
}
