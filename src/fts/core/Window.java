package fts.core;

import fts.views.View;

public abstract class Window {
	private View view;

	public abstract void setTitle(String title);
	public abstract void open();
	public abstract void mainLoop();
	
	public void setContentView(View view) {
		this.view = view;
	}
}
