package fts.android.gl;

import android.app.Activity;

import fts.gl.GLWindow;

public class AndroidGLWindow extends GLWindow {
    private final Activity activity;

    public AndroidGLWindow(Activity activity) {
        this.activity = activity;
    }

    public Activity getActivity() {
        return activity;
    }
}
