package fts.widgets;

import fts.core.Widget;
import fts.core.Window;
import fts.events.PaintEvent;
import fts.graphics.Align;
import fts.graphics.Canvas;
import fts.graphics.Color;
import fts.graphics.Font;
import fts.graphics.Point;
import fts.graphics.Rectangle;
import fts.graphics.TextDrawable;

public class TextWidget extends Widget {
	TextDrawable textDrawable;
	
	public TextWidget(Window w) {
		super(w);
		textDrawable = new TextDrawable();
		textDrawable.setFont(new Font("default", "16pt"));
		setProperty("color", "@color/text");
		setPadding(4);
	}
	
	public String getText() {
		return textDrawable.getText();
	}

	public void setText(String text) {
		textDrawable.setText(text);
	}
	
	public void setAlign(Align align) {
		textDrawable.setAlign(align);
	}
	
	public void setColor(Color color) {
		textDrawable.setColor(color);
	}

	public void setMaxLines(int lines) {
		textDrawable.setMaxLines(lines);
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
		textDrawable.draw(canvas);
	}

	@Override
	public Point getContentSize(int width, int height) {
		Canvas canvas = getWindow().getCanvas();
		
		Rectangle textBounds = getInternalBounds(width, height);
		textDrawable.setBounds(textBounds);
		
		
		Point paddingSize = getPaddingSize();
		Point textSize = textDrawable.getSize(canvas, textDrawable.getText(), width);

		Point contentSize = new Point(
			textSize.x + paddingSize.x,
			textSize.y + paddingSize.y);
		
		return contentSize;
	}

	@Override
	public void onMeasure(int parentWidth, int parentHeight) {
		super.onMeasure(parentWidth, parentHeight);
	}
	
}
