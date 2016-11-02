package fts.core;

import fts.graphics.Point;
import fts.views.View;

public abstract class Window {
	private View view;

	public abstract void setTitle(String title);
	public abstract void open();
	public abstract void mainLoop();
	public abstract Point getBounds();
	
	public void setContentView(View view) {
		this.view = view;
	}
	
	public void layout() {
		Point bounds = getBounds();
		view.onMeasure(bounds.x, bounds.y);
	}
}
