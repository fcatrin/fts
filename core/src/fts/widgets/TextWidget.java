package fts.widgets;

import fts.core.Widget;
import fts.core.Window;
import fts.events.PaintEvent;
import fts.graphics.Canvas;
import fts.graphics.Color;
import fts.graphics.Font;
import fts.graphics.Point;

public class TextWidget extends Widget {
	Font font;
	String text;
	int lineHeight;

	public TextWidget(Window w) {
		super(w);
		font = new Font("default", 20);
		setPadding(4);
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
		
		canvas.setForeground(new Color("#FFFFFFFF"));
		canvas.setFont(font);
		canvas.drawText(bounds.x + padding.x, bounds.y + padding.y + lineHeight, bounds.width, bounds.height, text);
	}

	@Override
	public Point getContentSize(int width, int height) {
		Canvas canvas = getWindow().getCanvas();
		canvas.setFont(font);
		Point textSize = canvas.getTextSize(text);
		lineHeight = textSize.y;
		
		textSize.x += padding.x + padding.width;
		textSize.y += padding.y + padding.width;
		
		return textSize;
	}

}
