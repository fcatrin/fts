package fts.android;

import android.app.Activity;
import fts.gl.GLWindow;

public class AndroidWindow extends GLWindow {

	final Activity activity;
	
	public AndroidWindow(Activity activity) {
		this.activity = activity;
	}

	public Activity getActivity() {
		return activity;
	}

}
