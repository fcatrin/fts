package fts.core;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;


public final class DownloadManager {

	private static Map<String, String> persistentHeaders = new HashMap<String, String>();
	
	private DownloadManager() {}

	public static void addHeader(String key, String value) {
		persistentHeaders.put(key, value);
	}

	public static void removeHeader(String key) {
		persistentHeaders.remove(key);
	}

	public static byte[] download(String url) throws IOException {
		return download(url, null);
	}

	public static byte[] download(String url, ProgressListener listener) throws IOException {
		return CoreUtils.httpGet(url, persistentHeaders, listener);
	}

	public static boolean downloadFile(String url, File file) throws IOException {
		return downloadFile(url, file, null);
	}
	
	public static boolean downloadFile(String url, File file, ProgressListener listener) throws IOException {
		return CoreUtils.httpGetFile(url, persistentHeaders, file, listener);
	}

	public static boolean downloadFile(String url, File file, Map<String, String> headers, ProgressListener listener) throws IOException {
		Map<String, String> tmpHeaders = new HashMap<String, String>();
		tmpHeaders.putAll(headers);
		tmpHeaders.putAll(persistentHeaders);
		return CoreUtils.httpGetFile(url, tmpHeaders, file, listener);
	}

	public static byte[] postContent(String resource, Map<String, String> headers, String mime, String data) throws IOException {
		Map<String, String> mHeaders = headers;
		if (mHeaders == null) mHeaders = new HashMap<String, String>();
		
		mHeaders.putAll(persistentHeaders);
		mHeaders.put("Content-Type", mime);
		mHeaders.put("Content-Length", data.length() + "");
		return CoreUtils.post(resource, mHeaders, data);
	}

	public static byte[] putContent(String resource, Map<String, String> headers, String mime, String data) throws IOException {
		Map<String, String> mHeaders = headers;
		if (mHeaders == null) mHeaders = new HashMap<String, String>();
		
		mHeaders.putAll(persistentHeaders);
		mHeaders.put("Content-Type", mime);
		mHeaders.put("Content-Length", data.length() + "");
		return CoreUtils.put(resource, mHeaders, data);
	}

	public static byte[] delete(String url) throws IOException {
		return CoreUtils.httpDelete(url, persistentHeaders);
	}
	

}
