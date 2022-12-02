package fts.android;

import android.content.SharedPreferences;
import android.view.View;

public abstract class Snippet<T extends AndroidWindow> {
    protected T activity;

    public Snippet(T activity) {
        this.activity = activity;
    }

    protected String getString(int resourceId) {
        return activity.getString(resourceId);
    }

    protected <V extends View> V findViewById(int resourceId) {
        return activity.findViewById(resourceId);
    }

    public abstract View getRootPanel();

    public void open() {
        View rootPanel = getRootPanel();
        if (rootPanel != null) {
            rootPanel.setVisibility(View.VISIBLE);
        }
    }

    public void close() {
        View rootPanel = getRootPanel();
        if (rootPanel != null) {
            rootPanel.setVisibility(View.GONE);
        }
    }

    public boolean isVisible() {
        View rootPanel = getRootPanel();
        if (rootPanel != null) return rootPanel.getVisibility() == View.VISIBLE;
        return false;
    }

    public void savePreferences(SharedPreferences.Editor editor) {}
    public void loadPreferences(SharedPreferences preferences) {}
}