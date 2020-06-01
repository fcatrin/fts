package fts.core;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import fts.events.PaintEvent;
import fts.events.TouchEvent;
import fts.graphics.Point;
import fts.graphics.Rectangle;

public abstract class Container extends Widget {
	List<Widget> children = new ArrayList<Widget>();
	Map<String, Widget> knownWidgets = new HashMap<String, Widget>();

	public Container(Window w) {
		super(w);
	}

	@Override
	public void redraw() {
	}

	@Override
	protected void onPaint(PaintEvent e) {
		if (background != null) {
			background.setBounds(bounds);
			background.draw(e.canvas);
		}
		for(Widget child : children) {
			Rectangle childBounds = child.getBounds();
			e.canvas.viewStart(childBounds.x, childBounds.y, 
					childBounds.width, childBounds.height);
			child.onPaint(e);
			e.canvas.viewEnd();
		}
	}
	
	public abstract void layout();
	
	@Override
	public void setLayoutDone() {
		super.setLayoutDone();
		for(Widget child : children) {
			child.setLayoutDone();
		}
	}

	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		return super.resolvePropertyValue(propertyName, value);
	}

	public void add(Widget view) {
		children.add(view);
		String id = view.getId();
		if (id!=null && !knownWidgets.containsKey(id)) {
			knownWidgets.put(id,  view);
		}
	}
	
	public void removeAllChildren() {
		for(Widget child : children) {
			child.onDispose();
		}
		children.clear();
	}
	
	@Override
	public Widget findWidget(String id) {
		Widget w = super.findWidget(id);
		if (w!=null) return w;
		
		w = knownWidgets.get(id);
		if (w!=null) return w;
		
		for(Widget child : children) {
			w = child.findWidget(id);
			if (w!=null) return w;
		}
		
		return null;
	}

	public List<Widget> getChildren() {
		return children;
	}

	@Override
	public String toString(String s) {
		return super.toString(String.format(", children;%s%s", children.toString(), s));
	}
	
	public abstract void onMeasureChildren(MeasureSpec w, MeasureSpec h);
	
	@Override
	public void onMeasure(MeasureSpec w, MeasureSpec h) {
		super.onMeasure(w, h);
		onMeasureChildren(w, h);
	}

	@Override
	public boolean needsLayout() {
		if (super.needsLayout()) return true;
		for(Widget child : children) {
			if (child.needsLayout()) return true;
		}
		return false;
	}
	
	@Override
	public boolean dispatchTouchEvent(TouchEvent touchEvent) {
		for(Widget child : children) {
			Rectangle childBounds = child.getBounds();
			if (childBounds.contains(touchEvent.x, touchEvent.y)) {
				if (child.dispatchTouchEvent(touchEvent)) return true;
			}
		}
		return super.dispatchTouchEvent(touchEvent);
	}
	
}
