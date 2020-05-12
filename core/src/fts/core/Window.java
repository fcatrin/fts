package fts.core;

import fts.events.PaintEvent;
import fts.graphics.Canvas;
import fts.graphics.Point;

public abstract class Window {
	private Widget view;
	private Canvas canvas;

	public abstract void setTitle(String title);
	public abstract void open();
	public abstract void mainLoop();
	public abstract Point getBounds();
	
	public void setContentView(Widget view) {
		this.view = view;
	}
	
	public void layout() {
		Point bounds = getBounds();
		view.onMeasure(bounds.x, bounds.y);
		view.requestLayout();
	}
	
	public void onPaint(PaintEvent e) {
		if (view.needsLayout()) {
			view.layout();
			view.setLayoutDone();
		}
		view.onPaint(e);
	}
	
	public Canvas getCanvas() {
		return canvas;
	}
	
	public void setCanvas(Canvas canvas) {
		this.canvas = canvas;
	}
	
	
}
