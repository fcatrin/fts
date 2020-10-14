package fts.android;

import android.content.Context;
import fts.core.Application;
import fts.gl.GLWindow;
import fts.graphics.Point;

public class AndroidWindow extends GLWindow {
	
	GLWindow nativeWindow;
	
	Point bounds = new Point();
	
	public AndroidWindow() {
		nativeWindow = (GLWindow)Application.createNativeWindow("", 0, 0, 0);
	}

	@Override
	protected boolean sync() {
		return true;
	}

	@Override
	public void setTitle(String title) {
	}

	@Override
	public void open() {
	}

	@Override
	public Point getBounds() {
		return bounds;
	}
	
	public void setSize(int width, int height) {
		bounds.x = width;
		bounds.y = height;
		requestLayout();
	}

}
