package fts.core.json;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import fts.core.net.NetworkUtils;

public abstract class JSONProcessor<T> {
	public static String MIME = "application/json";
	
	public T download(String url) throws Exception {
		byte[] json = NetworkUtils.httpGet(url);
		String js = new String(json).trim();
		JSONObject o = new JSONObject(js);
		T item = build(o);
		return item;
	}
	
	public List<T> downloadList(String url) throws Exception {
		List<T> list = new ArrayList<T>();
		byte[] json = NetworkUtils.httpGet(url);
		String js = new String(json).trim();
		
		JSONArray a = null;
		if (js.startsWith("{")) a = extractList(new JSONObject(js));
		else a = new JSONArray(js);		
		
		for(int i=0; a!=null && i<a.length(); i++) {
			JSONObject ao = a.getJSONObject(i);
			T item = build(ao);
			list.add(item);
		}
		return list;
	}

	public T post(String url, JSONObject data) throws Exception {
		byte[] json = NetworkUtils.httpPost(url, null, MIME, data.toString());
		String js = new String(json).trim();
		JSONObject o = new JSONObject(js);
		T item = build(o);
		return item;
	}


	protected JSONArray extractList(JSONObject o) throws Exception {
		throw new Exception("Result is an object, but list was expected");
	}
	
	public abstract T build(JSONObject o) throws Exception;
}