package fts.graphics;

import java.util.List;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;
import fts.graphics.Align.HAlign;
import fts.graphics.Align.VAlign;

public class TextDrawable extends Drawable {
	Align align;
	Font font;
	String text;
	Color color;
	
	int maxLines;
	
	TextWrapper textWrapper;

	public TextDrawable() {}

	public TextDrawable(Element element) {
		load(element);
	}

	@Override
	public void load(Element element) {
		setProperty("text", SimpleXML.getAttribute(element, "text"));
		font = resolvePropertyValueFont(element);
		setProperty("color", SimpleXML.getAttribute(element, "color"));
		setProperty("align", SimpleXML.getAttribute(element, "align"));
		setProperty("maxLines", SimpleXML.getAttribute(element, "maxLines"));
	}

	@Override
	public void draw(Canvas canvas) {
		if (text == null || text.trim().isEmpty()) return;
		
		canvas.setFont(font);
		canvas.setForeground(color);
		
		if (textWrapper == null) {
			getSize(canvas, text, bounds.width);
		}
		
		Point size = textWrapper.getSize();
		int left = bounds.x;
		int top = bounds.y;
		
		if (align.v == VAlign.Center) {
			top += (bounds.height - size.y) / 2;
		} else if (align.v == VAlign.Bottom) {
			top += bounds.height - size.y;
		}

		int lineSeparator = textWrapper.getLineSeparator();
		
		List<String> lines = textWrapper.getLines();
		List<TextMetrics> lineMetrics = textWrapper.getLineMetrics();
		
		for(int i=0; i<lines.size(); i++) {
			String line = lines.get(i);
			TextMetrics metrics = lineMetrics.get(i);
			
			int lineLeft = left;
			if (align.h == HAlign.Center) {
				lineLeft += (bounds.width - metrics.width) / 2;
			} else if (align.h == HAlign.Right) {
				lineLeft += bounds.width - metrics.width;
			}
			
			canvas.drawText(lineLeft, top + metrics.ascent, bounds.width, bounds.height, line);
			top += metrics.height + lineSeparator;
		}
		
	}
	
	public Point getSize(Canvas canvas, String text, int width) {
		canvas.setFont(font);
		
		textWrapper = canvas.getTextWrap(text, width, maxLines);
		return textWrapper.getSize();
	}

	public Align getAlign() {
		return align;
	}

	public void setAlign(Align align) {
		this.align = align;
	}

	public Font getFont() {
		return font;
	}

	public void setFont(Font font) {
		this.font = font;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public Color getColor() {
		return color;
	}

	public void setColor(Color color) {
		this.color = color;
	}
	
	public void setMaxLines(int lines) {
		this.maxLines = lines;
	}
}
