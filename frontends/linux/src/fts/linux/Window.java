package fts.linux;

import fts.core.Application;
import fts.core.Context;
import fts.core.DesktopLogger;
import fts.core.DesktopResourceLocator;
import fts.core.Widget;
import fts.events.KeyEvent;
import fts.events.TouchEvent;
import fts.gl.GLWindow;
import fts.gl.GLWindowListener;
import fts.graphics.Point;

public class Window implements GLWindowListener {

	GLWindow nativeWindow;
	
	private int height;
	private int width;
	private String title;
	
	static {
		Application.init(new ComponentFactory(), new DesktopResourceLocator(), new DesktopLogger(), new Context());
	}

	public Window(String title, int width, int height) {
		this.width = width;
		this.height = height;
		this.title = title;
		
		nativeWindow = (GLWindow)Application.createNativeWindow(title, width, height);
		nativeWindow.setWindowListener(this);
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void open() {
		NativeInterface.windowOpen(title, width, height);
		nativeWindow.init();
	}

	@Override
	public Point getBounds() {
		return new Point(width, height);
	}

	@Override
	public boolean sync() {
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
			case NativeInterface.FTS_KEY_EVENT:
				fireKeyEvent(type, nativeEvents[i+2], nativeEvents[i+3]);
			}
				
		}
		return true;
	}

	private void fireKeyEvent(int type, int keyCode, int modifiers) {
		KeyEvent event = new KeyEvent();
		event.down      = type == NativeInterface.FTS_KEY_DOWN;
		event.keyCode   = keyCode;
		event.modifiers = modifiers;
		nativeWindow.dispatchKeyEvent(event);
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
		nativeWindow.dispatchTouchEvent(event);
	}

	public Widget inflate(String layoutName) {
		return Application.inflate(nativeWindow, layoutName);
	}
	
	public void setContentView(Widget view) {
		nativeWindow.setContentView(view);
	}
	
	public Widget findWidget(String id) {
		return nativeWindow.findWidget(id);
	}
	
	@Override
	public void onCreate() {}

	@Override
	public void onStart() {}

	@Override
	public void onStop() {}

	@Override
	public void onDestroy() {}

	@Override
	public void onFrame() {}

	public void run() {
		onCreate();
		open();
		onStart();
		nativeWindow.mainLoop();
		onStop();
		onDestroy();
	}


}
