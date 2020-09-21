package fts.gl;

import fts.core.Log;
import fts.graphics.BackBuffer;
import fts.graphics.Canvas;

public class GLBackBuffer extends BackBuffer {
	private static final String LOGTAG = GLBackBuffer.class.getSimpleName();
	
	int handle;
	
	@Override
	public void create(int width, int height) {
		destroy();

		setWidth(width);
		setHeight(height);
		
		if (width == 0 || height == 0) return;
		
		handle = GLNativeInterface.createBackBuffer(width, height);
		Log.d(LOGTAG, "Create backbuffer " + width + "x" + height);
	}

	@Override
	public void bind() {
		if (handle == 0) return;
		GLNativeInterface.bindBackBuffer(handle);
		Log.d(LOGTAG, "bind " + getWidth() + "x" + getHeight());
	}

	@Override
	public void draw(Canvas canvas, int x, int y) {
		if (handle == 0) return;
		// Log.d(LOGTAG, "draw at " + x + "," + y + " " + getWidth() + "x" + getHeight());
		GLNativeInterface.drawBackBuffer(handle, x, y, getWidth(), getHeight());
	}

	@Override
	public void unbind() {
		if (handle == 0) return;
		Log.d(LOGTAG, "unbind " + getWidth() + "x" + getHeight());
		GLNativeInterface.unbindBackBuffer(handle);
	}

	@Override
	public void destroy() {
		if (handle == 0) return;
		Log.d(LOGTAG, "destroy " + getWidth() + "x" + getHeight());
		GLNativeInterface.destroyBackBuffer(handle);
		handle = 0;
	}
}
