package fts.core;

import fts.graphics.Point;
import fts.views.View;
import fts.views.ViewGroup;

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
		view.setBounds(0,  0,  bounds.x, bounds.y);
		view.onMeasure(bounds.x, bounds.y);
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup)view;
			viewGroup.layout();
		}
	}
}
