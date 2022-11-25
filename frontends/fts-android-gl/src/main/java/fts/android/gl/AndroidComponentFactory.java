package fts.android.gl;

import android.app.Activity;

import fts.gl.GLFactory;
import fts.ui.Window;
import fts.ui.graphics.BackBuffer;

public class AndroidComponentFactory extends GLFactory {
	
	Activity activity;
	
	public AndroidComponentFactory(Activity activity) {
		this.activity = activity;
	}

	@Override
	public Window createNativeWindow(String title, int width, int height, int flags) {
		return new AndroidNativeWindow(activity);
	}

	@Override
	public BackBuffer createBackBuffer(String id, int width, int height) {
		return super.createBackBuffer(id, width, height);
	}

}
