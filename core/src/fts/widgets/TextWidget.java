package fts.widgets;

import fts.core.Widget;
import fts.core.Widget.Visibility;
import fts.core.LayoutInfo;
import fts.core.NativeWindow;
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
	
	public TextWidget(NativeWindow w) {
		super(w);
		textDrawable = new TextDrawable();
		textDrawable.setFont(new Font("default", "16pt"));
		setProperty("color", "@color/text");
	}
	
	public String getText() {
		return textDrawable.getText();
	}

	public void setText(String text) {
		textDrawable.setText(text);
		invalidate();
	}
	
	public void setAlign(Align align) {
		textDrawable.setAlign(align);
		invalidate();
	}
	
	public void setColor(Color color) {
		textDrawable.setColor(color);
		invalidate();
	}
	
	public void setFontSize(int size) {
		textDrawable.setFontSize(size);
	}

	public void setMaxLines(int lines) {
		textDrawable.setMaxLines(lines);
		invalidate();
	}
	
	public void setFontName(String name) {
		textDrawable.setFontName(name);
	}
	
	@Override
	protected void onPaint(PaintEvent e) {
		super.onPaint(e);
		Rectangle textBounds = getInternalPaintBounds(bounds.width, bounds.height);

		Canvas canvas = e.canvas;
		textDrawable.setBounds(textBounds);
		textDrawable.draw(canvas);
	}

	@Override
	public Point getContentSize(int width, int height) {
		Canvas canvas = getWindow().getCanvas();
		
		Rectangle textBounds = getInternalPaintBounds(width, height);
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
	
	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (propertyName.equals("fontSize")) {
			return resolvePropertyValueDimen(propertyName, value);
		} else if (propertyName.equals("fontName")) {
			return value;
		}
		return super.resolvePropertyValue(propertyName, value);
	}
}
