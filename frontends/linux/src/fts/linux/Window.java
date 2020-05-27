package fts.linux;

import fts.events.TouchEvent;
import fts.gl.GLWindow;
import fts.graphics.Point;

public class Window extends GLWindow {

	private int height;
	private int width;
	private String title;

	public Window(String title, int width, int height) {
		this.width = width;
		this.height = height;
		this.title = title;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void open() {
		NativeInterface.windowOpen(title, width, height);
	}

	@Override
	public Point getBounds() {
		return new Point(width, height);
	}

	@Override
	protected boolean sync() {
		NativeInterface.windowSwapBuffers();
		return processEvents(NativeInterface.windowGetEvents());
	}

	private boolean processEvents(int[] nativeEvents) {
		if (nativeEvents == null) return true;
		
		for(int i=0; i<nativeEvents.length; i+=5) {
			int family = nativeEvents[i];
			int type   = nativeEvents[i+1];
			switch (family) {
			case NativeInterface.FTS_WINDOW_EVENT:
				if (type == NativeInterface.FTS_WINDOW_CLOSE) {
					return false;
				}
				break;
			case NativeInterface.FTS_TOUCH_EVENT:
				fireTouchEvent(type, nativeEvents[i+2], nativeEvents[i+3], nativeEvents[i+4]);
				break;
			}
		}
		return true;
	}

	private void fireTouchEvent(int type, int button, int x, int y) {
		TouchEvent event = new TouchEvent();
		switch (type) {
		case NativeInterface.FTS_MOUSE_DOWN : event.action = TouchEvent.Action.DOWN; break;
		case NativeInterface.FTS_MOUSE_UP : event.action = TouchEvent.Action.UP; break;
		case NativeInterface.FTS_MOUSE_MOVE : event.action = TouchEvent.Action.MOVE; break;
		}

		event.x = x;
		event.y = y;
		event.timestamp = System.currentTimeMillis();
		dispatchTouchEvent(event);
	}

}
