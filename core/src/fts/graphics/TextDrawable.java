package fts.graphics;

import java.util.List;

import org.w3c.dom.Element;

import fts.core.Utils;
import fts.core.xml.SimpleXML;
import fts.graphics.Align.HAlign;
import fts.graphics.Align.VAlign;

public class TextDrawable extends Drawable {
	Align align = new Align();
	Font font;
	String text;
	ColorListSelector color;
	
	int maxLines = -1;
	
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
		canvas.setColor(color.getSelectedItem());
		
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
			top += metrics.height - metrics.descent + lineSeparator;
		}
		
	}
	
	public Point getSize(Canvas canvas, String text, int width) {
		canvas.setFont(font);
		
		String textToMeasure = Utils.isEmptyString(text) ? "A" : text;
		textWrapper = canvas.getTextWrap(textToMeasure, width, maxLines);
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
		textWrapper = null; // force text re-layout
	}
	
	public void setFontSize(int size) {
		if (font == null) return;
		font.size = size;
		textWrapper = null;
	}

	public void setFontName(String name) {
		if (font == null) return;
		font.name = name;
		textWrapper = null;
	}

	public ColorListSelector getColor() {
		return color;
	}

	public void setColor(ColorListSelector color) {
		this.color = color;
	}
	
	public void setMaxLines(int lines) {
		this.maxLines = lines;
	}

}
