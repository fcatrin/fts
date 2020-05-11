package fts.widgets;

import fts.core.Widget;
import fts.core.Window;
import fts.events.PaintEvent;
import fts.graphics.Canvas;
import fts.graphics.Font;
import fts.graphics.Point;

public class TextWidget extends Widget {
	Font font;
	String text;

	public TextWidget(Window w) {
		super(w);
		font = new Font("default", 10);
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
		Canvas canvas = getWindow().getCanvas();
		canvas.setFont(font);
		return canvas.getTextSize(text);
	}

}
