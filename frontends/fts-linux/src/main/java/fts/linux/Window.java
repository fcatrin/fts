package fts.linux;

import java.io.File;

import fts.gl.GLWindow;
import fts.gl.GLWindowListener;
import fts.ui.Application;
import fts.ui.NativeWindow;
import fts.ui.Widget;
import fts.ui.events.KeyEvent;
import fts.ui.events.TouchEvent;
import fts.ui.graphics.Point;

public class Window implements GLWindowListener {

	GLWindow nativeWindow;
	
	private int height;
	private int width;
	private int flags;
	private int x;
	private int y;
	
	private String title;
	
	public Window(String title, int width, int height) {
		this.width = width;
		this.height = height;
		this.title = title;
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
		nativeWindow = (GLWindow)Application.createNativeWindow(title, width, height, flags); // TODO are those parameters needed really?
		nativeWindow.setWindowListener(this);
		
		NativeInterface.windowOpen(title, x, y, width, height, flags);
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
		nativeWindow.dispatchTouchEvent(event);
	}

	public Widget inflate(String layoutName) {
		return Application.inflate(nativeWindow, layoutName);
	}
	
	public void setContentView(Widget view) {
		nativeWindow.setContentView(view);
	}
	
	public Widget getContentView() {
		return nativeWindow.getContentView();
	}
	
	public Widget findWidget(String id) {
		return nativeWindow.findWidget(id);
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
		nativeWindow.mainLoop();
		onWindowStop();
		onWindowDestroy();
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent keyEvent) {
		return nativeWindow.dispatchKeyEvent(keyEvent);
	}

	@Override
	public boolean onKeyDown(KeyEvent keyEvent) {
		return nativeWindow.onKeyDown(keyEvent);
	}

	@Override
	public boolean onKeyUp(KeyEvent keyEvent) {
		return nativeWindow.onKeyUp(keyEvent);
	}

	public NativeWindow getNativeWindow() {
		return nativeWindow;
	}
	
	public File getDataDir() {
		return new File(System.getProperty("user.home"), ".ftsapp");
	}

}
