package fts.widgets;

import java.util.ArrayList;
import java.util.List;

import fts.core.Window;
import fts.events.PaintEvent;
import fts.graphics.Point;

public abstract class ViewGroup extends View {
	List<View> children = new ArrayList<View>();

	public ViewGroup(Window w) {
		super(w);
	}

	@Override
	public void redraw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPaint(PaintEvent e) {
		// TODO Auto-generated method stub
		
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

	public void add(View view) {
		children.add(view);
	}

	public List<View> getChildren() {
		return children;
	}

	@Override
	public String toString(String s) {
		return super.toString(String.format(", children;%s%s", children.toString(), s));
	}
	
	protected void onMeasureChildren() {
		for(View child : children) {
			child.onMeasure(measuredWidth, measuredHeight);
		}
	}
	
	@Override
	public void onMeasure(MeasureSpec w, MeasureSpec h) {
		super.onMeasure(w, h);
		onMeasureChildren();
	}
}
