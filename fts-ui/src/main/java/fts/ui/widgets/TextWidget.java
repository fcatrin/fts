package fts.ui.widgets;

import org.w3c.dom.Text;

import fts.ui.Widget;
import fts.ui.Window;
import fts.ui.events.PaintEvent;
import fts.ui.graphics.Align;
import fts.ui.graphics.Canvas;
import fts.ui.graphics.ColorListSelector;
import fts.ui.graphics.Font;
import fts.ui.graphics.Point;
import fts.ui.graphics.Rectangle;
import fts.ui.graphics.TextDrawable;

public class TextWidget extends Widget {
	TextDrawable textDrawable;

	static {
		registerIntProperty(TextWidget.class, "lines");
		registerIntProperty(TextWidget.class, "maxLines");
		registerDimensionProperty(TextWidget.class, "fontSize");
		registerStringProperty(TextWidget.class, "fontName");
	}

	public TextWidget(Window w) {
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

	public void setColor(ColorListSelector color) {
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

	public void setLines(int lines) {
		textDrawable.setLines(lines);
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
		textDrawable.setState(getStateFlags());
		textDrawable.draw(canvas);
	}

	@Override
	public Point getContentSize(int width, int height) {
		Canvas canvas = getWindow().getCanvas();
		
		Point paddingSize = getPaddingSize();
		Point textSize = textDrawable.getSize(canvas, textDrawable.getText(), width - paddingSize.x);

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
