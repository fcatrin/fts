package fts.android;

import java.io.File;

import android.app.Activity;
import android.opengl.GLSurfaceView;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.MotionEvent;
import fts.core.Application;
import fts.core.Context;
import fts.core.Widget;
import fts.events.KeyEvent;
import fts.events.TouchEvent;
import fts.gl.GLWindow;
import fts.gl.GLWindowListener;
import fts.graphics.Point;

public class FtsActivity extends Activity implements GLWindowListener {
	private static final String LOGTAG = FtsActivity.class.getSimpleName();
	
	private GLWindow nativeWindow;
	private Point bounds = new Point();
	private boolean started = false;
	
	@Override
	final protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		
		Display display = getWindowManager().getDefaultDisplay();
		android.graphics.Point size = new android.graphics.Point();
		display.getSize(size);
		bounds.x = size.x;
		bounds.y = size.y;
		
		configureScreen();
		setContentView(R.layout.main);
		
		Application.init(new ComponentFactory(), new AndroidResourceLocator(this), new AndroidLogger(), new fts.core.Context());

		nativeWindow = (GLWindow)Application.createNativeWindow("", 0, 0, 0);
		nativeWindow.setWindowListener(this);
		started = false;
	}
	
	protected void configureScreen() {
		AndroidUtils.configureAsFullscreen(this);
	}
	
	public GLWindow getNativeWindow() {
		return nativeWindow;
	}
	
	public void setContentView(Widget view) {
		nativeWindow.setContentView(view);
	}
	
	public Widget inflate(String layoutName) {
		return Application.inflate(nativeWindow, layoutName);
	}
	
	public Widget findWidget(String id) {
		return nativeWindow.findWidget(id);
	}

	@Override
	public void setTitle(String title) {
		// this is weird, the compiler complains if not defined in some way
		super.setTitle(title);
	}
	
	@Override
	final protected void onDestroy() {
		super.onDestroy();
		Context.backgroundProcessor.shutdown();		
		onWindowDestroy();
	}

	@Override
	final protected void onStart() {
		super.onStart();
		
		if (started) return;
		GLRenderer renderer = new GLRenderer(this, nativeWindow);
		GLSurface surface = (GLSurface)findViewById(R.id.fts_view);
		surface.setRenderer(renderer);
		surface.setRenderMode(GLSurfaceView.RENDERMODE_CONTINUOUSLY);
		started = true;
	}

	@Override
	final protected void onStop() {
		super.onStop();
		onWindowStop();
	}
	
	@Override
	public boolean dispatchTouchEvent(MotionEvent ev) {
		TouchEvent event = new TouchEvent();
		switch(ev.getAction()) {
		case MotionEvent.ACTION_DOWN : event.action = TouchEvent.Action.DOWN; break;
		case MotionEvent.ACTION_MOVE : event.action = TouchEvent.Action.MOVE; break;
		case MotionEvent.ACTION_UP : event.action = TouchEvent.Action.UP; break;
		}
		event.button = TouchEvent.Button.LEFT;
		event.x = (int)ev.getX();
		event.y = (int)ev.getY();
		return nativeWindow.dispatchTouchEvent(event);
	}

	@Override
	public boolean dispatchKeyEvent(android.view.KeyEvent event) {
		Log.d(LOGTAG, "dispatchKeyEvent keyCode " + event.getKeyCode() + ", mod:" + event.getModifiers());
		if (event.getKeyCode() == 59 && event.getModifiers() == 65) {
			// SHIFT+1 dumps the layout
			nativeWindow.dumpLayout();
		}
		KeyEvent ftsEvent = AndroidKeyMap.translate(event);
		if (ftsEvent == null) return super.dispatchKeyEvent(event);
		return dispatchKeyEvent(ftsEvent);
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
		Log.d(LOGTAG, "onKeyUp keyCode " + keyEvent.keyCode + ", mod:" + keyEvent.modifiers);
		if (keyEvent.keyCode == KeyEvent.KEY_BACKSPACE) {
			super.onBackPressed();
			return true;
		}
		return nativeWindow.onKeyUp(keyEvent);
	}
	
	@Override
	public Point getBounds() {
		return bounds;
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

	@Override
	public void open() {}

	@Override
	public boolean sync() {
		return false;
	}
	
	@Override
	public File getDataDir() {
		return this.getFilesDir();
	}
}
