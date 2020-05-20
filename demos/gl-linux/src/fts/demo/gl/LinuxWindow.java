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
		LinuxNativeInterface.windowSwapBuffers();
		return processEvents(LinuxNativeInterface.windowGetEvents());
	}

	private boolean processEvents(int[] nativeEvents) {
		if (nativeEvents == null) return true;
		
		for(int i=0; i<nativeEvents.length; i+=5) {
			int family = nativeEvents[i];
			int type   = nativeEvents[i+1];
			switch (family) {
			case LinuxNativeInterface.FTS_WINDOW_EVENT:
				if (type == LinuxNativeInterface.FTS_WINDOW_CLOSE) {
					return false;
				}
				
			}

		}
		return true;
	}

}
