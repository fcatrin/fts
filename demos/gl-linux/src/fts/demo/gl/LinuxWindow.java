package fts.demo.gl;

import fts.gl.GLWindow;
import fts.graphics.Point;

public class LinuxWindow extends GLWindow {
	
	private int height;
	private int width;

	public LinuxWindow(int width, int height) {
		this.width = width;
		this.height = height;
	}

	@Override
	public void setTitle(String title) {
	}

	@Override
	public void open() {
		LinuxNativeInterface.windowOpen(width, height);
	}

	@Override
	public Point getBounds() {
		return new Point(width, height);
	}

	@Override
	protected boolean sync() {
		return LinuxNativeInterface.windowSwapBuffers();
	}

}
