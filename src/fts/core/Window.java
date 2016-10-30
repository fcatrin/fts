package fts.core;

import java.io.File;

import fts.views.View;

public abstract class Window {
	public abstract void setTitle(String title);
	public abstract void open();
	public abstract void mainLoop();
	
	public void setContentView(String name) {
		View v = Application.loadView(name);
		
	}
}
