package fts.core.net;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;

public class HttpPostRequest extends HttpRequest {

    InputStream body;
    int knownSize;
    public String mime;

    public HttpPostRequest(String url) {
        super(url);
        this.method = METHOD_POST;
    }

    public HttpPostRequest(String url, byte[] data) {
        this(url);
        body = new ByteArrayInputStream(data);
        knownSize = data.length;
    }

    public HttpPostRequest(String url, String data) {
        this(url, data.getBytes(StandardCharsets.UTF_8));
    }

    public HttpPostRequest(String url, InputStream is, int knownSize) {
        this(url);
        this.body = is;
        this.knownSize = knownSize;
    }

    public HttpPostRequest(String url, Map<String, String> params) {
        this(url);

        StringBuilder sData = new StringBuilder();
        for (Map.Entry<String, String> entry : params.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue(); // URLEncoder.encode(entry.getValue());
            if (sData.length() != 0) sData.append("&");
            sData.append(key).append("=").append(value);
        }

        byte[] data = sData.toString().getBytes(StandardCharsets.UTF_8);
        body = new ByteArrayInputStream(data);
        knownSize = data.length;
        mime = "application/x-www-form-urlencoded";
    }
}
