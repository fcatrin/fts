package fts.core.json;

import org.json.JSONObject;

public class EmptyJSONProcessor {
    public static void post(String url, JSONObject o) throws Exception {
        SimpleJSONProcessor p = new SimpleJSONProcessor() {
            @Override
            public void process(JSONObject o) throws Exception {
                if (o.has("error")) throw new Exception(o.getString("error"));
            }
        };
        p.post(url, o);
    }
}
