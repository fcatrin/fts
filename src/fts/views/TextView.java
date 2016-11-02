package fts.views;

import fts.core.Window;
import fts.events.PaintEvent;
import fts.graphics.Point;

public class TextView extends View {

	String text;
	
	public TextView(Window w) {
		super(w);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public String toString(String s) {
		return super.toString(String.format(",text: %s%s", text, s));
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
		if (propertyName.equals("text")) return value;
		return super.resolvePropertyValue(propertyName, value);
	}

}
