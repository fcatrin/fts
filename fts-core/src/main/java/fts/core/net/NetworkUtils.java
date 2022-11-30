package fts.core.net;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

import fts.core.CoreUtils;
import fts.core.Log;
import fts.core.NotEnoughStorageException;
import fts.core.ProgressListener;

public class NetworkUtils {
	public static final String LOGTAG = NetworkUtils.class.getSimpleName();
	private static final int BUF_SIZE = 65536;
	private static final String HTTP_RANGE_HEADER = "Range";

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
		httpGetStream(sUrl, -1, headers, bout, progressListener);
		return bout.toByteArray();
	}

	public static void httpGetFile(String sUrl, Map<String, String>headers, File file) throws IOException {
		httpGetFile(sUrl, headers, file, null);
	}

	public static void httpGetFile(String sUrl, Map<String, String>headers, File file, ProgressListener progressListener) throws IOException {
		boolean append = headers != null && headers.containsKey(HTTP_RANGE_HEADER);
		FileOutputStream fos = new FileOutputStream(file, append);
		long maxFileSize = file.getFreeSpace();
		httpGetStream(sUrl, maxFileSize, headers, fos, progressListener);
	}

	public static HttpURLConnection httpOpenConnection(String sUrl, Map<String, String>headers) throws IOException {
		HttpRequest request = new HttpRequest(sUrl);
		request.requestHeaders = headers;
		return httpOpenConnection(request);
	}

	public static HttpURLConnection httpOpenConnection(HttpRequest request) throws IOException {
		HttpURLConnection connection = null;
		boolean redirect;

		String sUrl = request.url;
		try {
			do {
				URL url = new URL(sUrl);
				connection = (HttpURLConnection) url.openConnection();
				connection.setConnectTimeout(request.connectTimeout);
				connection.setReadTimeout(request.readTimeout);

				request.fillHeaders(connection);

				int code = connection.getResponseCode();
				if (code == HttpURLConnection.HTTP_NOT_MODIFIED) return null;
				if (code == HttpURLConnection.HTTP_GONE) return null;

				redirect = code == HttpURLConnection.HTTP_MOVED_TEMP
						|| code == HttpURLConnection.HTTP_MOVED_PERM
						|| code == HttpURLConnection.HTTP_SEE_OTHER;
				if (redirect) {
					String newUrl = connection.getHeaderField("Location");
					Log.d(LOGTAG, "Redirect " + sUrl + " -> " + newUrl);
					sUrl = newUrl;
				}

			} while (redirect);
			return connection;
		} catch (IOException e) {
			if (connection!=null) connection.disconnect();
			throw e;
		}
	}
	public static void httpGetStream(String sUrl, long maxFileSize, Map<String, String>headers, OutputStream stream, ProgressListener progressListener) throws IOException {
		HttpRequest request = new HttpRequest(sUrl);
		request.maxSize = maxFileSize;
		request.requestHeaders = headers;
		request.listener = progressListener;
		httpGetStream(request, stream);
	}

	public static void httpGetStream(HttpRequest request, OutputStream stream) throws IOException {
		InputStream in = null;
		HttpURLConnection connection = null;
		try {
			long t0 = System.currentTimeMillis();
			Log.d(LOGTAG, "Download start " + request.url);

			connection = httpOpenConnection(request);
			if (connection == null) return;

			Map<String, String> requestHeaders = request.requestHeaders;
			int pos = 0;
			if (requestHeaders!=null && requestHeaders.containsKey(HTTP_RANGE_HEADER)) {
				String[] parts = requestHeaders.get(HTTP_RANGE_HEADER).replace("bytes=","").split("-");
				pos = CoreUtils.str2i(parts[0]);
			}

			int thisChunkSize = CoreUtils.str2i(connection.getHeaderField("Content-Length"));
			if (request.maxSize >0 && thisChunkSize > request.maxSize) {
				throw new NotEnoughStorageException(request.maxSize, thisChunkSize);
			}
			int size = thisChunkSize + pos;

			if (request.listener!=null) {
				request.listener.onStart();
				request.listener.onProgress(pos,  size);
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
				if (request.listener!=null) cancel = request.listener.onProgress(pos,  size);
			}
			if (request.listener!=null) request.listener.onEnd();

			request.setResponseHeaders(connection);

			long t = System.currentTimeMillis() - t0;
			Log.d(LOGTAG, "Download end " + t + "[ms] " + request.url);
		} finally {
			if (in != null)	in.close();
			if (connection!=null) connection.disconnect();
			stream.close();
		}
	}

	public static byte[] httpPost(String resource, Map<String, String> headers, String mime, InputStream is, int knownSize) throws IOException {
		return httpPost(resource, headers, mime, is, knownSize, null);
	}
	
	public static byte[] httpPost(String resource, Map<String, String> headers, String mime, InputStream is, int knownSize, ProgressListener listener) throws IOException {
		HttpPostRequest request = new HttpPostRequest(resource, is, knownSize);
		request.requestHeaders = headers;
		request.mime = mime;
		request.listener = listener;
		return httpPost(request);
	}

	public static byte[] httpPost(String resource, Map<String, String> headers, Map<String, String> data) throws IOException {
		HttpPostRequest request = new HttpPostRequest(resource, data);
		request.requestHeaders = headers;
		return httpPost(request);
	}

	public static byte[] httpPost(String resource, Map<String, String> requestHeaders, String data) throws IOException {
		return httpPost(resource, requestHeaders, null, data);
	}

	public static byte[] httpPost(String resource, Map<String, String> headers, String mime, String data) throws IOException {
		HttpPostRequest request = new HttpPostRequest(resource, data);
		request.requestHeaders = headers;
		request.mime = mime;
		return httpPost(request);
	}


	public static byte[] httpPost(String resource, Map<String, String> requestHeaders, byte[] data) throws IOException {
		HttpPostRequest postRequest = new HttpPostRequest(resource, data);
		postRequest.requestHeaders = requestHeaders;
		return httpPost(postRequest);
	}

	public static byte[] httpPost(HttpPostRequest request) throws IOException {
		URL url;
		ByteArrayOutputStream baos = null;
		OutputStream os = null;
		HttpURLConnection urlConnection = null;
		try {
			url = new URL(request.url);
			urlConnection = (HttpURLConnection) url.openConnection();
			urlConnection.setConnectTimeout(request.connectTimeout);
			urlConnection.setReadTimeout(request.readTimeout);

			request.fillHeaders(urlConnection);
			urlConnection.setRequestProperty("Content-Type", request.mime);
			urlConnection.setRequestProperty("Content-Length", String.valueOf(request.knownSize));

			urlConnection.setRequestMethod(request.method);
			urlConnection.setDoOutput(true);
			urlConnection.connect();

			int uploadProgress = 0;

			byte[] buffer = new byte[BUF_SIZE];
			int bufferLength;

			// send content
			os = urlConnection.getOutputStream();
			while ((bufferLength = request.body.read(buffer)) > 0) {
				os.write(buffer, 0, bufferLength);
				request.updateProgress(uploadProgress, request.knownSize);
				uploadProgress += bufferLength;
				os.flush();
			}

			baos = new ByteArrayOutputStream();
			InputStream inputStream = urlConnection.getInputStream();

			while ((bufferLength = inputStream.read(buffer)) > 0) {
				baos.write(buffer, 0, bufferLength);
			}

			request.setResponseHeaders(urlConnection);

			request.response = baos.toByteArray();
			return request.response;
		} catch (IOException e) {
			if (urlConnection!=null) {
				Log.d(LOGTAG, "Error response code " + urlConnection.getResponseCode());
				Log.d(LOGTAG, "Error response message " + urlConnection.getResponseMessage());
			}
			if (baos!=null) {
				Log.d(LOGTAG, "Error response body " + baos.toString());
			}
			throw e;
		} finally {
			if (baos != null) baos.close();
			if (os!=null) os.close();
		}
	}

	public static byte[] httpPut(String resource, Map<String, String> headers, String mime, String data) throws IOException {
		HttpPostRequest postRequest = new HttpPostRequest(resource, data);
		postRequest.mime = mime;
		postRequest.method = HttpPostRequest.METHOD_PUT;
		postRequest.requestHeaders = headers;

		return httpPost(postRequest);
	}

	public static byte[] httpDelete(String url) throws IOException {
		HttpPostRequest postRequest = new HttpPostRequest(url);
		postRequest.method = HttpPostRequest.METHOD_DELETE;

		return httpPost(postRequest);
	}


}

