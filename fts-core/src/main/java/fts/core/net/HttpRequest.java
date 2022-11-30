package fts.core.net;

import java.net.HttpURLConnection;
import java.net.URLConnection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fts.core.ProgressListener;

@SuppressWarnings("CanBeFinal")
public class HttpRequest {
    public static final String METHOD_GET    = "GET";
    public static final String METHOD_POST   = "POST";
    public static final String METHOD_PUT    = "PUT";
    public static final String METHOD_DELETE = "DELETE";

    private static final int HTTP_CONNECT_TIMEOUT = 5000;
    private static final int HTTP_READ_TIMEOUT = 8000;

    private static final Map<String, String> persistentHeaders = new HashMap<>();

    String url;
    public Map<String, String> requestHeaders;
    ProgressListener listener;
    public Map<String, List<String>> responseHeaders = new HashMap<>();
    public byte[] response;
    public boolean usePersistentHeaders = true;
    public int readTimeout = HTTP_READ_TIMEOUT;
    public int connectTimeout = HTTP_CONNECT_TIMEOUT;
    public String method = METHOD_GET;
    long maxSize = 0;

    HttpRequest(String url) {
        this.url = url;
    }

    void updateProgress(int progress, int total) {
        if (listener != null) listener.onProgress(progress, total);
    }

    void setResponseHeaders(HttpURLConnection urlConnection) {
        responseHeaders.putAll(urlConnection.getHeaderFields());
    }

    public static void addHeader(String key, String value) {
        HttpRequest.persistentHeaders.put(key, value);
    }

    public static void removeHeader(String key) {
        HttpRequest.persistentHeaders.remove(key);
    }

    public void fillHeaders(URLConnection urlConnection) {
        if (usePersistentHeaders) {
            for(Map.Entry<String, String> entry : persistentHeaders.entrySet()) {
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
        if (requestHeaders!=null) {
            for (Map.Entry<String, String> entry : requestHeaders.entrySet()) {
                urlConnection.setRequestProperty(entry.getKey(), entry.getValue());
            }
        }
    }

}
