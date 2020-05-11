package fts.graphics;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;

public class TextDrawable extends Drawable {
	Align align;
	Font font;
	String text;
	Color color;

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
		canvas.drawText(bounds.x, bounds.y, bounds.width, bounds.height, text);
	}
	
}
