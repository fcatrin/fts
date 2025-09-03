package fts.android;

import android.content.Context;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

import fts.core.TextProvider;

public class AndroidTextProvider extends TextProvider {
    private static final Map<String, Integer> stringResourceIds = new HashMap<>();
    private final Context context;

    public AndroidTextProvider(Context context) {
        this.context = context;
    }

    protected void set(String key, int stringResourceId) {
        stringResourceIds.put(key, stringResourceId);
    }

    @Override
    protected String getNativeString(String key) {
        Integer resourceId = stringResourceIds.get(key.toLowerCase(Locale.ROOT));
        if (resourceId == null) return key;
        return context.getString(resourceId);
    }
}
