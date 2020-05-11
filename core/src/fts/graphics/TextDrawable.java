package fts.graphics;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;

public class TextDrawable extends Drawable {
	Align align;
	Font font;
	String text;
	Color color;
	int lineHeight;

	public TextDrawable() {}

	public TextDrawable(Element element) {
		load(element);
	}

	@Override
	public void load(Element element) {
		setProperty("text", SimpleXML.getAttribute(element, "text"));
		font = resolvePropertyValueFont(element);
		setProperty("color", SimpleXML.getAttribute(element, "color"));
	}

	@Override
	public void draw(Canvas canvas) {
		if (text == null || text.trim().isEmpty()) return;
		
		canvas.setFont(font);
		canvas.setForeground(color);
		canvas.drawText(bounds.x, bounds.y + lineHeight, bounds.width, bounds.height, text);
	}
	
	public Point getSize(Canvas canvas, String text) {
		canvas.setFont(font);

		Point textSize = canvas.getTextSize(text);
		lineHeight = textSize.y;
		return textSize;
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
	
	
	
}
