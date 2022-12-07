package fts.linux;

import java.io.File;

import fts.gl.GLWindow;
import fts.gl.GLWindowListener;
import fts.ui.Resources;
import fts.ui.Widget;
import fts.ui.events.KeyEvent;
import fts.ui.events.TouchEvent;
import fts.ui.graphics.Point;

public class DesktopWindow extends GLWindow implements GLWindowListener {

	private int height;
	private int width;
	private int flags;
	private int x;
	private int y;
	
	private String title;
	
	public DesktopWindow(String title, int width, int height) {
		super();
		this.width = width;
		this.height = height;
		this.title = title;
		setWindowListener(this);
	}

	public void setWindowFlags(int flags) {
		this.flags = flags;
	}
	
	public void setX(int x) {
		this.x = x;
	}

	public void setY(int y) {
		this.y = y;
	}

	@Override
	public void setTitle(String title) {
		this.title = title;
	}

	@Override
	public void open() {
		NativeInterface.windowOpen(title, x, y, width, height, flags);
		init();
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
		event.keyCode   = KeyMap.translate(keyCode);
		event.modifiers = modifiers;
		dispatchKeyEvent(event);
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

	@Override
	public void onWindowCreate() {}

	@Override
	public void onWindowStart() {}

	@Override
	public void onWindowStop() {}

	@Override
	public void onWindowDestroy() {}

	@Override
	public void onFrame() {}

	public void run() {
		open();
		onWindowCreate();
		onWindowStart();
		mainLoop();
		onWindowStop();
		onWindowDestroy();
	}

	public File getDataDir() {
		return new File(System.getProperty("user.home"), ".ftsapp");
	}

}
