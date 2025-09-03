package fts.ui;

import fts.core.TextProvider;

public class CoreTextProvider extends TextProvider {
    @Override
    protected String getNativeString(String key) {
        String text = Resources.factory.getString(key);
        return text != null ? text : key;
    }
}
