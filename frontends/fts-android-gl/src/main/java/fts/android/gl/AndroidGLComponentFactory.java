package fts.android.gl;

import android.app.Activity;

import fts.gl.GLFactory;
import fts.ui.Window;
import fts.ui.graphics.BackBuffer;

public class AndroidGLComponentFactory extends GLFactory {
	
	Activity activity;
	
	public AndroidGLComponentFactory(Activity activity) {
		this.activity = activity;
	}

	@Override
	public Window createNativeWindow(String title, int width, int height, int flags) {
		return new AndroidGLWindow(activity);
	}

	@Override
	public BackBuffer createBackBuffer(String id, int width, int height) {
		return super.createBackBuffer(id, width, height);
	}

}
