package fts.core;

import fts.events.KeyEvent;
import fts.graphics.Point;

public interface NativeWindowListener {
	public void onCreate();
	public void onStart();
	public void onStop();
	public void onDestroy();
	
	public void onFrame();
	
	public void setTitle(String title);
	public void open();
	public Point getBounds();
	
	public boolean dispatchKeyEvent(KeyEvent keyEvent);
	public boolean onKeyDown(KeyEvent event);
	public boolean onKeyUp(KeyEvent event);

}
