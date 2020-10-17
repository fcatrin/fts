package fts.android;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

import android.opengl.GLSurfaceView.Renderer;
import fts.gl.GLWindow;
import fts.graphics.Point;

public class GLRenderer implements Renderer {
	GLWindow window;
	private FtsActivity activity;

	public GLRenderer(FtsActivity activity, GLWindow nativeWindow) {
		this.activity = activity;
		this.window = nativeWindow;
	}
	
	@Override
	public void onSurfaceCreated(GL10 gl, EGLConfig config) {
		window.init();
		activity.onWindowCreate();
		activity.onWindowStart();
	}

	@Override
	public void onSurfaceChanged(GL10 gl, int width, int height) {
		Point bounds = window.getBounds();
		bounds.x = width;
		bounds.y = height;
		window.requestLayout();
	}

	@Override
	public void onDrawFrame(GL10 gl) {
		window.render();
	}

}
