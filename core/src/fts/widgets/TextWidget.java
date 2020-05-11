package fts.widgets;

import fts.core.Widget;
import fts.core.Window;
import fts.events.PaintEvent;
import fts.graphics.Canvas;
import fts.graphics.Color;
import fts.graphics.Font;
import fts.graphics.Point;
import fts.graphics.Rectangle;
import fts.graphics.TextDrawable;

public class TextWidget extends Widget {
	Font font;
	String text;
	
	TextDrawable textDrawable;

	public TextWidget(Window w) {
		super(w);
		font = new Font("default", 20);
		textDrawable = new TextDrawable();
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

		Rectangle textBounds = getInternalBounds(bounds.width, bounds.height);
		
		textDrawable.setBounds(textBounds);
		textDrawable.setFont(font);
		textDrawable.setColor(new Color("#FFFFFFFF"));
		textDrawable.setText(text);
		textDrawable.draw(canvas);
	}

	@Override
	public Point getContentSize(int width, int height) {
		Canvas canvas = getWindow().getCanvas();
		
		Rectangle textBounds = getInternalBounds(width, height);
		textDrawable.setBounds(textBounds);
		textDrawable.setFont(font);
		
		Point textSize = textDrawable.getSize(canvas, text);
		
		Point paddingSize = getPaddingSize();
		textSize.x += paddingSize.x;
		textSize.y += paddingSize.y;
		
		return textSize;
	}
	
}
