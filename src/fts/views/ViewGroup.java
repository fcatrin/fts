package fts.views;

import java.util.ArrayList;
import java.util.List;

import fts.core.Component;
import fts.core.Layout;
import fts.events.PaintEvent;
import fts.graphics.Point;

public class ViewGroup extends View {
	List<View> children = new ArrayList<View>();
	Layout layout;

	public ViewGroup() {
		// TODO Auto-generated constructor stub
	}

	@Override
	public void redraw() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected void onPaint(PaintEvent e) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public Point computeSize(int x, int y) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		// TODO Auto-generated method stub
		return null;
	}

	public void add(View view) {
		children.add(view);
	}

	public Layout getLayout() {
		return layout;
	}

	public void setLayout(Layout layout) {
		this.layout = layout;
	}

	public List<View> getChildren() {
		return children;
	}

	
}
