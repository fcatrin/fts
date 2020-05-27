package fts.core;

import fts.events.PaintEvent;
import fts.events.TouchEvent;
import fts.graphics.Canvas;
import fts.graphics.Point;
import fts.widgets.AbsoluteContainer;

public abstract class Window {
	private Widget view;
	private Canvas canvas;
	private SimpleCallback onFrameCallback;

	public abstract void setTitle(String title);
	public abstract void open();
	public abstract void mainLoop();
	public abstract Point getBounds();
	
	public void setContentView(Widget view) {
		Container container = new AbsoluteContainer(this);
		container.add(view);
		
		LayoutInfo layoutInfo = container.getLayoutInfo();
		layoutInfo.width = LayoutInfo.MATCH_PARENT;
		layoutInfo.height = LayoutInfo.MATCH_PARENT;
		
		this.view = container;
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

	protected boolean dispatchTouchEvent(TouchEvent touchEvent) {
		if (view.dispatchTouchEvent(touchEvent)) return true;
		onTouchEvent(touchEvent);
		return true;
	}
	
	protected void onTouchEvent(TouchEvent touchEvent) {}
	
	public void setOnFrameCallback(SimpleCallback onFrameCallback) {
		this.onFrameCallback = onFrameCallback;
	}
	
	public SimpleCallback getOnFrameCallback() {
		return onFrameCallback;
	}
	
}
