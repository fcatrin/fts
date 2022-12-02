package fts.core.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fts.core.net.NetworkUtils;

public abstract class SimpleJSONProcessor extends JSONProcessor<Void> {

	@Override
	final public Void build(JSONObject o) throws Exception {
		process(o);
		return null;
	}

	public abstract void process(JSONObject o) throws Exception;
}