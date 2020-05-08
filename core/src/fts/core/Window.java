package fts.core;

import fts.graphics.Point;

public abstract class Window {
	private Widget view;

	public abstract void setTitle(String title);
	public abstract void open();
	public abstract void mainLoop();
	public abstract Point getBounds();
	
	public void setContentView(Widget view) {
		this.view = view;
	}
	
	public void layout() {
		Point bounds = getBounds();
		view.setBounds(0,  0,  bounds.x, bounds.y);
		view.onMeasure(bounds.x, bounds.y);
		if (view instanceof Container) {
			Container container = (Container)view;
			container.layout();
		}
	}
}
