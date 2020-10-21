package fts.core;

import java.io.File;

import fts.events.KeyEvent;
import fts.graphics.Point;

public interface NativeWindowListener {
	public void onWindowCreate();
	public void onWindowStart();
	public void onWindowStop();
	public void onWindowDestroy();
	
	public void onFrame();
	
	public void setTitle(String title);
	public void open();
	public Point getBounds();
	
	public boolean dispatchKeyEvent(KeyEvent keyEvent);
	public boolean onKeyDown(KeyEvent event);
	public boolean onKeyUp(KeyEvent event);
	
	public File getDataDir();
}
