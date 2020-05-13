package fts.graphics;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;

public class Shape extends Drawable {
	
	Color fillColor = null;
	Color strokeColor = null;
	int strokeWidth = 1;
	int radius = 0;
	
	public Shape() {}
	
	public Shape(Element element) {
		load(element);
	}

	@Override
	public void load(Element element) {
		setProperty("fillColor",   SimpleXML.getAttribute(element, "fillColor"));
		setProperty("strokeColor", SimpleXML.getAttribute(element, "strokeColor"));
		setProperty("strokeWidth", SimpleXML.getAttribute(element, "strokeWidth"));
		setProperty("radius",      SimpleXML.getAttribute(element, "radius"));
	}
	
	@Override
	public void draw(Canvas canvas) {
		if (fillColor!=null) {
			canvas.setForeground(fillColor);
			canvas.drawFilledRect(bounds.x, bounds.y, bounds.width, bounds.height, radius);
		}
		
		if (strokeColor!=null && strokeWidth>0) {
			canvas.setForeground(strokeColor);
			canvas.drawRect(bounds.x + strokeWidth/2, bounds.y + strokeWidth/2, bounds.width - strokeWidth, bounds.height - strokeWidth, radius);
		}
	}

	public Color getFillColor() {
		return fillColor;
	}

	public void setFillColor(Color fillColor) {
		this.fillColor = fillColor;
	}

	public Color getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(Color strokeColor) {
		this.strokeColor = strokeColor;
	}

	public int getStrokeWidth() {
		return strokeWidth;
	}

	public void setStrokeWidth(int strokeWidth) {
		this.strokeWidth = strokeWidth;
	}

	public int getRadius() {
		return radius;
	}

	public void setRadius(int radius) {
		this.radius = radius;
	}

	
}
