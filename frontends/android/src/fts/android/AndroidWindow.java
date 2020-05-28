package fts.android;

import fts.gl.GLWindow;
import fts.graphics.Point;

public class AndroidWindow extends GLWindow {
	Point bounds = new Point();

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
		layout();
	}

}
