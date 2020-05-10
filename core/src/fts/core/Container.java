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
	}
	
	public abstract void layout();

	@Override
	public Point computeSize(int x, int y) {
		// TODO Auto-generated method stub
		return null;
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
	
	protected void onMeasureChildren() {
		for(Widget child : children) {
			child.onMeasure(layoutInfo.measuredWidth, layoutInfo.measuredHeight);
		}
	}
	
	@Override
	public void onMeasure(MeasureSpec w, MeasureSpec h) {
		super.onMeasure(w, h);
		onMeasureChildren();
	}
}
