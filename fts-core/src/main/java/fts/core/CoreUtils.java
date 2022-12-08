package fts.core;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLConnection;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Provider;
import java.security.Security;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import fts.core.json.EmptyJSONProcessor;

public final class CoreUtils {
	private static final String LOGTAG = CoreUtils.class.getSimpleName();
	
	public enum Compact {Start, Middle, End}
	
	private static final int HTTP_CONNECT_TIMEOUT = 15000;
	private static final int HTTP_READ_TIMEOUT = 20000;
	private static final int BYTE_PAD = 256;
	private static final int BYTE_MASK = 0xFF;
	private static final int BUF_SIZE = 65536;
	private static final int HEX_BASE = 16;
	private static final int SECONDS_PER_HOUR = 3600;
	private static final int MINUTES_PER_HOUR = 60;
	private static final int SECONDS_PER_MINUTE = 60;
	private static final String HTTP_RANGE_HEADER = "Range";
	private static final long SIZE_GIGABYTE = 1024*1024*1024;
	private static final long SIZE_MEGABYTE = 1024*1024;
	private static final long SIZE_KILOBYTE = 1024;
	
	public static int httpReadTimeout = HTTP_READ_TIMEOUT;
	public static int httpConnectTimeout = HTTP_CONNECT_TIMEOUT;
	
	public static int httpConnectTimeoutOnce = 0;
	public static int httpReadTimeoutOnce = 0;
	
	private CoreUtils() {}

	public static int str2i(String value) {
		return str2i(value, 0);
	}

	public static int str2i(String value, int defaultValue) {
		try {
			if (value == null) return defaultValue;
			return Integer.parseInt(value.trim());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static long str2l(String value) {
		return str2l(value, 0);
	}
	
	public static long str2l(String value, long defaultValue) {
		try {
			if (value == null) return defaultValue;
			return Long.parseLong(value.trim());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	
	public static float str2f(String value) {
		return str2f(value, 0);
	}

	public static float str2f(String value, float defaultValue) {
		try {
			if (value == null) return defaultValue;
			return Float.parseFloat(value.trim());
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}

	public static int strHex2i(String value) {
		return strHex2i(value, 0);
	}
	
	public static int strHex2i(String value, int defaultValue) {
		try {
			if (value == null) return defaultValue;
			return (int) Long.parseLong(value.trim(), HEX_BASE);
		} catch (NumberFormatException e) {
			return defaultValue;
		}
	}
	
	public static String size2human(long size) {
		if (size > SIZE_GIGABYTE) {
			DecimalFormat df = new DecimalFormat("#.00");
			return df.format(((size + SIZE_GIGABYTE/2) / SIZE_GIGABYTE)) + " GB";
		}
		if (size > SIZE_MEGABYTE) {
			return ((size + SIZE_MEGABYTE/2) / SIZE_MEGABYTE) + " MB";
		}
		return ((size + SIZE_KILOBYTE/2) / SIZE_KILOBYTE) + " KB";
	}

	
	public static String md5(String s) {
		try {
			return md5(s.getBytes("UTF-8"));
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public static String md5(byte[] data) {
		try {
			// Create MD5 Hash
			MessageDigest digest = java.security.MessageDigest.getInstance("MD5");
			digest.update(data);
			byte messageDigest[] = digest.digest();

			// Create Hex String
			StringBuffer hexString = new StringBuffer();
			for (int i = 0; i < messageDigest.length; i++) {
				String s = Integer.toHexString(BYTE_MASK & messageDigest[i]);
				if (s.length()<2) s = "0" + s;
				hexString.append(s);
			}
			return hexString.toString();
		} catch (NoSuchAlgorithmException e) {
			e.printStackTrace();
		}
		return "";
	}
	
	public static String[] buildStringArray(String s) {
		try {
			JSONArray a = new JSONArray(s);
			String values[] = new String[a.length()];
			for (int i = 0; i < a.length(); i++) {
				values[i] = a.getString(i);
			}
			return values;
		} catch (Exception e) {
			return null;
		}
	}
	
	public static String buildArrayString(String[] values) {
		JSONArray a = new JSONArray();
		for (String value : values) {
			a.put(value);
		}
		return a.toString();
	}

	public static String b2hex(byte []bytes) {
		StringBuffer str = new StringBuffer();
		for(byte b : bytes) {
			int i = b>=0 ? b : BYTE_PAD + b;
			String s = "0" + Integer.toHexString(i);
			str.append(s.substring(s.length()-2));
		}
		return str.toString();
	}

	public static byte[] hex2bytes(String hex) {
		byte[] bytes = new byte[hex.length() / 2];
		for(int i=0; i+2<=hex.length(); i+=2) {
			bytes[i / 2] = (byte)Integer.parseInt(hex.substring(i, i+2), HEX_BASE);
		}
		return bytes;
	}
	
	public static String findValue(Map<String, String> map, String value) {
		for (Entry<String, String> entry : map.entrySet()) {
			if (entry.getValue().equals(value))
				return entry.getKey();
		}
		return null;
	}
	
	public static int findArrayValue(String a[], String value) {
		for (int i = 0; i < a.length; i++)
			if (a[i].equals(value))
				return i;

		return -1;
	}

	public static String padz(int v, int n) {
		return padz(String.valueOf(v), n);
	}

	public static String padz(String s, int n) {
		String ns = "00000000" + s;
		return ns.substring(ns.length()-n);
	}

	public static String pads(String s, int n) {
		String ns = "        " + s;
		return ns.substring(ns.length()-n);
	}

	public static String formatTime(int time) {
		int hours = time / SECONDS_PER_HOUR;
		int minutes = (time - hours * SECONDS_PER_HOUR) / MINUTES_PER_HOUR;
		int seconds = time % SECONDS_PER_MINUTE;
		
		if (hours == 0) return padz(minutes + "", 2) + ":" + padz(seconds + "", 2);
		return hours + "h" + padz(minutes + "", 2);
	}
	
	public static String getStackTrace(Exception e) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		e.printStackTrace(pw);
		return sw.toString();
	}
	
	public static String getSecurityInfo() {
		StringBuffer sb = new StringBuffer();
		Provider[] providers = Security.getProviders();
		for (Provider provider : providers) {
		    sb.append("provider: ").append(provider.getName()).append("\n");
		    Set<Provider.Service> services = provider.getServices();
		    for (Provider.Service service : services) {
		    	sb.append("  algorithm: ").append(service.getAlgorithm()).append("\n");
		    }
		}
		return sb.toString();
	}
	
	public static byte[] httpGet(String sUrl) throws IOException {
		return httpGet(sUrl, null, null);
	}

	public static byte[] httpGet(String sUrl, ProgressListener progressListener) throws IOException {
		return httpGet(sUrl, null, progressListener);
	}

	public static byte[] httpGet(String sUrl, Map<String, String>headers) throws IOException {
		return httpGet(sUrl, headers, null);
	}
	
	public static byte[] httpGet(String sUrl, Map<String, String>headers, ProgressListener progressListener) throws IOException {
		ByteArrayOutputStream bout = new ByteArrayOutputStream();
		httpGetStream(sUrl, headers, bout, progressListener);
		return bout.toByteArray();
	}
	
	public static void httpGetFile(String sUrl, Map<String, String>headers, File file) throws IOException {
		httpGetFile(sUrl, headers, file, null);
	}

	public static boolean httpGetFile(String sUrl, Map<String, String>headers, File file, ProgressListener progressListener) throws IOException {
		FileOutputStream fos = new FileOutputStream(file, headers.containsKey(HTTP_RANGE_HEADER));
		return httpGetStream(sUrl, headers, fos, progressListener);
	}

	public static boolean httpGetStream(String sUrl, Map<String, String>headers, OutputStream stream, ProgressListener progressListener) throws IOException {
		InputStream in = null;
		URLConnection connection = null;
		try {
			long t0 = System.currentTimeMillis();
			// Log.d(LOGTAG, "Download start " + sUrl + " headers " + headers);
			URL url = new URL(sUrl);
			connection = url.openConnection();
			connection.setConnectTimeout(httpConnectTimeoutOnce > 0 ? httpConnectTimeoutOnce : httpConnectTimeout);
			connection.setReadTimeout(httpReadTimeoutOnce > 0 ? httpReadTimeoutOnce : httpReadTimeout);
			
			httpConnectTimeoutOnce = 0;
			httpReadTimeoutOnce    = 0;
			
			if (headers!=null) for(Entry<String, String> entry : headers.entrySet())
				connection.addRequestProperty(entry.getKey(), entry.getValue());
			
			int code = ((HttpURLConnection)connection).getResponseCode();
			if (code == HttpURLConnection.HTTP_NOT_MODIFIED) return false;
			
			int pos = 0;
			if (headers!=null && headers.containsKey(HTTP_RANGE_HEADER)) {
				String parts[] = headers.get(HTTP_RANGE_HEADER).replace("bytes=","").split("-");
				pos = str2i(parts[0]);
			}
			
			int size = str2i(connection.getHeaderField("Content-Length")) + pos;

			if (progressListener!=null) {
				progressListener.onStart();
				progressListener.onProgress(pos,  size);
			}

			in = connection.getInputStream();

			byte[] buf = new byte[BUF_SIZE];
			boolean cancel = false;
			while (!cancel) {
				int rc = in.read(buf);
				if (rc <= 0) 
					break;
				else
					stream.write(buf, 0, rc);
				pos += rc;
				if (progressListener!=null) cancel = progressListener.onProgress(pos,  size);
			}
			if (progressListener!=null) progressListener.onEnd();
			long t = System.currentTimeMillis() - t0;
			// Log.d(LOGTAG, "Download end " + t + "[ms] " + sUrl);
			return true;
		} finally {
			if (in != null)	in.close();
			stream.close();
		}
	}
	
	public static byte[] post(String resource, Map<String, String>headers, String data) throws IOException {
		URL url;
		ByteArrayOutputStream baos = null;
		OutputStreamWriter wr = null;
		try {
			url = new URL(resource);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(httpConnectTimeout);
			urlConnection.setReadTimeout(httpReadTimeout);

			fillHeaders(urlConnection, headers);
			urlConnection.setRequestMethod("POST");
			urlConnection.setDoOutput(true);
			urlConnection.connect();
			
		    wr = new OutputStreamWriter(urlConnection.getOutputStream());
		    wr.write(data);
		    wr.flush();

			baos = new ByteArrayOutputStream();
			InputStream inputStream = urlConnection.getInputStream();

			byte[] buffer = new byte[BUF_SIZE];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				baos.write(buffer, 0, bufferLength);
			}
			return baos.toByteArray();
		} finally {
			if (baos != null) baos.close();
			if (wr!=null) wr.close();
		}
	}

	public static byte[] put(String resource, Map<String, String>headers, String data) throws IOException {
		URL url;
		ByteArrayOutputStream baos = null;
		OutputStreamWriter wr = null;
		try {
			url = new URL(resource);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(httpConnectTimeout);
			urlConnection.setReadTimeout(httpReadTimeout);

			fillHeaders(urlConnection, headers);
			urlConnection.setRequestMethod("PUT");
			urlConnection.setDoOutput(true);
			urlConnection.connect();
			
		    wr = new OutputStreamWriter(urlConnection.getOutputStream());
		    wr.write(data);
		    wr.flush();

			baos = new ByteArrayOutputStream();
			InputStream inputStream = urlConnection.getInputStream();

			byte[] buffer = new byte[BUF_SIZE];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				baos.write(buffer, 0, bufferLength);
			}
			return baos.toByteArray();
		} finally {
			if (baos != null) baos.close();
			if (wr != null) wr.close();
		}
	}

	public static byte[] httpDelete(String resource, Map<String, String> headers) throws IOException {
		URL url;
		ByteArrayOutputStream baos = null;
		try {
			url = new URL(resource);
			HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
			fillHeaders(urlConnection, headers);
			urlConnection.setRequestMethod("DELETE");
			urlConnection.setDoOutput(false);
			urlConnection.connect();
			
			baos = new ByteArrayOutputStream();
			InputStream inputStream = urlConnection.getInputStream();

			byte[] buffer = new byte[BUF_SIZE];
			int bufferLength = 0;

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				baos.write(buffer, 0, bufferLength);
			}
			return baos.toByteArray();
		} finally {
			if (baos != null) baos.close();
		}
	}

	
	public static void fillHeaders(HttpURLConnection urlConnection, Map<String, String> headers) {
		if (headers == null) return;
		
		for(Entry<String, String> entry : headers.entrySet()) {
			urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
		}
	}
	
	public static String s(String format, String... args) {
		if (args == null) return format;
		
		String newFormat = format;
		for(int i=0; i<args.length; i++) {
			newFormat = newFormat.replace("{" +i + "}", args[i]);
		}
		return newFormat;
	}

	public static String loadString(File f) throws IOException  {
		return loadString(f, "UTF-8");
	}
	
	public static String loadString(File f, String encoding) throws IOException {
		return loadString(new FileInputStream(f), encoding);
	}
	
	public static String loadString(InputStream is, String encoding) throws IOException {
		BufferedReader reader = null;
		try {
			reader = new BufferedReader(new InputStreamReader(is, encoding));
			
			StringBuffer s = new StringBuffer();
			String line = null;
			while ((line = reader.readLine())!=null) {
				s.append(line).append("\n");
			}
			return s.toString();
		} finally {
			if (reader!=null) reader.close();
		}
	}
	
	public static byte[] loadBytes(File f) throws IOException {
		FileInputStream is = null;
		try {
			is = new FileInputStream(f);
			ByteArrayOutputStream bout = new ByteArrayOutputStream();
			byte[] buf = new byte[BUF_SIZE];
			while (true) {
				int rc = is.read(buf);
				if (rc <= 0)
					break;
				else
					bout.write(buf, 0, rc);
			}
			return bout.toByteArray();
		} finally {
			if (is!=null) is.close();
		}
	}
	
	public static void saveBytes(File f, byte[] content) throws IOException {
		FileOutputStream os = null;
		ByteArrayInputStream bais = null;
		try {
			os = new FileOutputStream(f);
			bais = new ByteArrayInputStream(content);
			byte[] buf = new byte[BUF_SIZE];
			while (true) {
				int rc = bais.read(buf);
				if (rc <= 0)
					break;
				else
					os.write(buf, 0, rc);
			}
		} finally {
			if (os!=null) os.close();
			if (bais!=null) bais.close();
		}
	}

	public static void copyFile(File src, File dst) throws IOException {
		copyFile(src, dst, null);
	}
	
	public static void copyFile(File src, File dst, ProgressListener progressListener) throws IOException {
		FileInputStream is = new FileInputStream(src);
		FileOutputStream os = new FileOutputStream(dst);
		copyFile(is, os, progressListener, src.length());
	}
	
	public static void copyFile(InputStream is, OutputStream os) throws IOException {
		copyFile(is, os, null, 0);
	}

	public static void copyFile(InputStream is, OutputStream os, ProgressListener progressListener) throws IOException {
		copyFile(is, os, progressListener, 0);
	}

	public static void copyFile(InputStream is, OutputStream os, ProgressListener progressListener, long max) throws IOException {
        byte buffer[] = new byte[BUF_SIZE];
        
		int bufferLength = 0;

		if (progressListener!=null) progressListener.onProgress(0, (int)max);
		try {
			int pos = 0;
			while ((bufferLength = is.read(buffer)) > 0) {
				os.write(buffer, 0, bufferLength);
				pos += bufferLength;
				if (progressListener!=null) progressListener.onProgress(pos, (int)max);
			}
		} finally {
			is.close();
			os.close();
		}
	}
	
	public static void delTree(File dir) {
		if (!dir.exists() || !dir.isDirectory()) return;
		if (dir.listFiles() != null) {
			for(File f : dir.listFiles()) {
				if (f.isDirectory()) delTree(f);
				else f.delete();
			}
		}
		dir.delete();
	}
	
	private static void buildTreeList(List<File> files, File srcDir, File dstDir) {
		for(File file : srcDir.listFiles()) {
			if (file.isDirectory()) {
				files.add(file);
				buildTreeList(files, file, dstDir);
			} else files.add(file);
		}
	}
	
	public static List<File> moveTree(File srcDir, File dstDir) throws IOException {
		List<File> files = new ArrayList<File>();
		buildTreeList(files, srcDir, dstDir);

		dstDir.mkdirs();
		
		String base = srcDir.getCanonicalPath();
		for(File file : files) {
			String relative = file.getCanonicalPath().substring(base.length()+1);
			File dstFile = new File(dstDir, relative);
			if (file.isDirectory()) {
				dstFile.mkdirs();
			} else {
				file.renameTo(dstFile);
			}
		}
		return files;
	}
	
	public static long getTreeSize(File root) {
		long size = 0;
		File files[] = root.listFiles();
		
		if (files == null) return size;
		
		for(File file: files) {
			if (file.isDirectory()) size += getTreeSize(file);
			else size += file.length();
		}
		return size;
	}
	
	public static boolean unzip(File file, File dstPath) throws IOException {
		return unzip(file, dstPath);
	}
	
	public static boolean unzip(File file, File dstPath, ProgressListener listener) throws IOException {
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
			long max = 0;
			long pos = 0;
			
			boolean hasSizeInfo = true;
			if (listener!=null) {
				Enumeration<? extends ZipEntry> e = zipFile.entries();
		        while (e.hasMoreElements()) {
		        	ZipEntry entry = e.nextElement();
		        	long size = entry.getSize();
		        	max += size>0?size:0;
		        }
		        zipFile.close();
		        
		        if (max == 0) {
		        	hasSizeInfo = false;
		        	max = zipFile.size();
		        }
		        
		        zipFile = new ZipFile(file);
				listener.onProgress(0, (int)max);
			}
			
	        byte buffer[] = new byte[BUF_SIZE];
	        boolean cancel = false;
			Enumeration<? extends ZipEntry> e = zipFile.entries();
	          while (e.hasMoreElements()) {
	              ZipEntry entry = e.nextElement();
	              if (entry.getName().startsWith("_")) continue;
	              
	              File destinationPath = new File(dstPath, entry.getName());
	              destinationPath.getParentFile().mkdirs();
	              if (entry.isDirectory()) continue;
	              
	              BufferedInputStream bis = new BufferedInputStream(zipFile.getInputStream(entry));
	              int b;
	              FileOutputStream fos = new FileOutputStream(destinationPath);
	              BufferedOutputStream bos = new BufferedOutputStream(fos, BUF_SIZE);
	              bos.flush();
	              while ((b = bis.read(buffer, 0, BUF_SIZE)) != -1) {
	                  bos.write(buffer, 0, b);
	                  
	                  if (hasSizeInfo && listener!=null) {
	                      pos+=b;
	                	  cancel = listener.onProgress((int)pos, (int)max);
	                	  if (cancel) break;
	                  }
	              }
	              bos.close();
	              bis.close();
	              
	              if (listener!=null && !hasSizeInfo) {
	            	  pos++;
	            	  cancel = listener.onProgress((int)pos, (int)max);
	              }
	              if (cancel) return false;
	          }
	          return true;
		} finally {
			if (zipFile!=null) zipFile.close();
		}
	}

	public static boolean isEmptyString(String text) {
		return text==null || text.trim().length()==0;
	}
	
	public static String compact(String text, Compact compact, int length) {
		if (CoreUtils.isEmptyString(text) || text.length() <= length) return text;
		switch (compact) {
		case Start : return "..." + text.substring(text.length() - length);
		case End : return text.substring(0, length) + "...";
		default : return text.substring(0, length/2) + "..." + text.substring(text.length() - length/2);
		}
	}
	
	public static String genOid() {
		return "oid:" + System.currentTimeMillis() + Math.round(Math.random()*1000);
	}
	
	public static JSONArray asJSONArray(List<String> list) {
		JSONArray a = new JSONArray();
		for(String s : list) a.put(s);
		return a;
	}
	
	public static String capitals(String s) {
		if (s == null) return null;
		if (s.length()==0) return s;
		
		String result = s.substring(0, 1).toUpperCase();
		if (s.length()>1) result = result + s.substring(1).toLowerCase();
		return result;
	}

	public static byte[] loadResourceData(String path) throws IOException {
		InputStream inputStream = CoreUtils.class.getClassLoader().getResourceAsStream(path);
		if (inputStream == null) return null;
		
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		byte[] buffer = new byte[BUF_SIZE];
		int bufferLength = 0;

		while ((bufferLength = inputStream.read(buffer)) > 0) {
			baos.write(buffer, 0, bufferLength);
		}
		return baos.toByteArray();
	}
	
	public static String loadResourceString(String path) {
		try {
			byte[] data = loadResourceData(path);
			if (data == null) return null;
			return new String(data, "UTF-8");
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}

	public static Properties loadProperties(String path) {
		Properties properties = new Properties();
		InputStream is = CoreUtils.class.getClassLoader().getResourceAsStream(path);
		if (is == null) return properties;
		
		try {
			properties.load(is);
		} catch (IOException e) {
			e.printStackTrace();
		}
		return properties;
	}

	public static int ubyte(byte b) {
		return b & 255;
	}
	
	@SuppressWarnings("rawtypes")
	public static File getLoadingDir(Class clazz) {
		try {
			return new File(clazz.getProtectionDomain().getCodeSource().getLocation().toURI().getPath());
		} catch (URISyntaxException e) {
			return null;
		}
	}
	
	public static boolean isAndroid() {
		return System.getenv("ANDROID_DATA") != null;
	}
	
	public static void sleep(long msec) {
		try {
			Thread.sleep(msec);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static void postTrace(String url, Throwable t, JSONObject data) {
		StringWriter sw = new StringWriter();
		PrintWriter pw = new PrintWriter(sw);
		try {
			t.printStackTrace(pw);
			data.put("trace", sw.toString());
			EmptyJSONProcessor.post(url, data);
		} catch (Exception e) {
			// ignore exceptions at this point
			e.printStackTrace();
		} finally {
			pw.close();
		}
	}

	public static String removeStart(String text, String s) {
		return text.toLowerCase(Locale.ROOT).startsWith(s) ? text.substring(s.length()) : text;
	}

	public static String removeEnd(String text, String s) {
		return text.toLowerCase(Locale.ROOT).endsWith(s) ? text.substring(0, text.length() - s.length()) : text;
	}

	public static String removeStartEnd(String text, String s) {
		if (!text.toLowerCase(Locale.ROOT).startsWith(s) || !text.toLowerCase(Locale.ROOT).endsWith(s)) return text;
		text = removeStart(text, s);
		return removeEnd(text, s);
	}

}
