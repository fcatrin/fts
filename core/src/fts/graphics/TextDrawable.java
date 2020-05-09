package fts.graphics;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;

public class TextDrawable extends Drawable {
	Font font;
	String text;
	Color color;

	public TextDrawable() {}

	public TextDrawable(Element element) {
		load(element);
	}

	@Override
	public void load(Element element) {
		text = SimpleXML.getAttribute(element, "text");
		font = Font.load(element);
		color = Color.load(SimpleXML.getAttribute(element, "color"));
	}

	@Override
	public void draw(Canvas canvas) {
		if (text == null || text.trim().isEmpty()) return;
		
		canvas.setFont(font);
		canvas.setForeground(color);
		canvas.drawText(bounds.x, bounds.y, bounds.width, bounds.height, text);
	}

}
