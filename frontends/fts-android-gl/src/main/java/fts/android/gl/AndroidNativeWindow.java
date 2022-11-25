package fts.android.gl;

import android.app.Activity;

import fts.gl.GLWindow;

public class AndroidNativeWindow extends GLWindow {
    private final Activity activity;

    public AndroidNativeWindow(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}
