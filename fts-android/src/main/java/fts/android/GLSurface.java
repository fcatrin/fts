package fts.android;

import android.content.Context;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import fts.core.NativeWindow;
import fts.events.TouchEvent;

public class GLSurface extends GLSurfaceView {
	private static final String LOGTAG = GLSurface.class.getSimpleName();
	FtsActivity ftsActivity;

	public GLSurface(Context context, AttributeSet attrs) {
		super(context, attrs);
		setEGLContextClientVersion(2);
	}
	
	public void setFtsActivity(FtsActivity ftsActivity) {
		this.ftsActivity = ftsActivity;
	}

	@Override
	public boolean dispatchKeyEvent(KeyEvent event) {
		Log.d(LOGTAG, "dispatchKeyEvent keyCode " + event.getKeyCode() + ", mod:" + event.getModifiers());
		if (event.getKeyCode() == 59 && event.getModifiers() == 65) {
			// SHIFT+1 dumps the layout
			ftsActivity.getNativeWindow().dumpLayout();
		}
		fts.events.KeyEvent ftsEvent = AndroidKeyMap.translate(event);
		if (ftsEvent == null) return super.dispatchKeyEvent(event);
		return ftsActivity.dispatchKeyEvent(ftsEvent);
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
		return ftsActivity.getNativeWindow().dispatchTouchEvent(event);

	}

}
