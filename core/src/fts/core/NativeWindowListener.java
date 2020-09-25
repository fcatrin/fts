package fts.core;

import fts.graphics.Point;

public interface NativeWindowListener {
	public void onCreate();
	public void onStart();
	public void onStop();
	public void onDestroy();
	
	public void setTitle(String title);
	public void open();
	public Point getBounds();
}
