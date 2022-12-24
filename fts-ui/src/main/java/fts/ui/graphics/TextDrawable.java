package fts.ui.graphics;

import org.w3c.dom.Element;

import java.util.List;

import fts.core.CoreUtils;
import fts.core.xml.SimpleXML;
import fts.ui.graphics.Align.HAlign;
import fts.ui.graphics.Align.VAlign;

public class TextDrawable extends Drawable {
	Align align = new Align();
	Font font;
	String text;
	ColorListSelector color;
	
	int maxLines = -1;
	int lines = -1;
	
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
		setProperty("lines", SimpleXML.getAttribute(element, "lines"));
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
			top += metrics.height + lineSeparator;
		}
		
	}
	
	public Point getSize(Canvas canvas, String text, int width) {
		canvas.setFont(font);
		int fixedHeight = -1;
		if (lines > 0) {
			fixedHeight = canvas.getTextHeight(lines);
		}
		
		String textToMeasure = CoreUtils.isEmptyString(text) ? "A" : text;
		textWrapper = canvas.getTextWrap(textToMeasure, width, maxLines);
		if (fixedHeight != -1) {
			textWrapper.getSize().y = fixedHeight;
		}
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
	
	public void setMaxLines(int maxLines) {
		this.maxLines = maxLines;
		adjustMaxLines();
	}

	public void setLines(int lines) {
		this.lines = lines;
		adjustMaxLines();
	}

	private void adjustMaxLines() {
		if (lines > 0 && maxLines > lines) maxLines = lines;
	}

	public void setState(boolean[] stateFlags) {
		color.setState(stateFlags);
	}

}
