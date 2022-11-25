package fts.ui;

import java.io.File;

import fts.ui.events.KeyEvent;
import fts.ui.graphics.Point;

public interface WindowListener {
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
