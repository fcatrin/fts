package fts.core;

public abstract class TextProvider {
    static TextProvider instance;
    public TextProvider() {
        instance = this;
    }

    public static String get(String key) {
        return instance.getNativeString(key);
    }

    protected abstract String getNativeString(String key);
}
