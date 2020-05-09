package fts.graphics;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;

public class Shape extends Drawable {
	
	Color fillColor = null;
	Color strokeColor = null;
	int strokeWidth = 1;
	int radius = 0;

	@Override
	public void load(Element element) {
		fillColor   = Color.load(SimpleXML.getAttribute(element, "fill-color"));
		strokeColor = Color.load(SimpleXML.getAttribute(element, "stroke-color"));
		strokeWidth = Dimension.parse(SimpleXML.getAttribute(element, "stroke-width"));
		radius = Dimension.parse(SimpleXML.getAttribute(element, "radius"));
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (fillColor!=null) {
			canvas.setForeground(fillColor);
			canvas.drawFilledRect(bounds.x, bounds.y, bounds.width, bounds.height, radius);
		}
		
		if (strokeColor!=null && strokeWidth>0) {
			canvas.setForeground(strokeColor);
			canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, radius);
		}
	}
}
