package fts.widgets;

import fts.core.Widget;
import fts.core.Window;
import fts.events.PaintEvent;
import fts.graphics.Canvas;
import fts.graphics.Point;

public class TextWidget extends Widget {
	String text;

	public TextWidget(Window w) {
		super(w);
	}
	
	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public void redraw() {
	}

	@Override
	protected void onPaint(PaintEvent e) {
		Canvas canvas = e.canvas;
		if (background!=null) {
			background.setBounds(bounds);
			background.draw(canvas);
		}
	}

	@Override
	public Point getContentSize(int width, int height) {
		// measure required width and height of text
		// width is used for text wrapping limit
		return new Point(width, 20);
	}

}
