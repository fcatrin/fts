package fts.core;

import java.util.ArrayList;
import java.util.List;

import fts.events.PaintEvent;
import fts.graphics.Point;

public abstract class Container extends Widget {
	List<Widget> children = new ArrayList<Widget>();

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
			child.onPaint(e);
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
	
}
