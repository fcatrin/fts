package fts.android;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import fts.core.Log;

public class GLRenderer implements Renderer {
	AndroidWindow window;

	public GLRenderer(AndroidWindow window) {
		this.window = window;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		window.init();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		window.setSize(width, height);
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		window.render();
	}

}
