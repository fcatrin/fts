package fts.graphics;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;

public class Shape extends Drawable {
	
	Color fillColor = null;
	Color strokeColor = null;
	int strokeWidth = 4;
	int radius = 0;
	
	// gradient attributes
	Color startColor = null;
	Color endColor = null;
	int angle;
	
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

		setProperty("startColor",  SimpleXML.getAttribute(element, "startColor"));
		setProperty("endColor",    SimpleXML.getAttribute(element, "endColor"));
		setProperty("angle",       SimpleXML.getAttribute(element, "angle"));
	}

	float fangle = 0;
	
	@Override
	public void draw(Canvas canvas) {
		if (fillColor!=null) {
			canvas.setColor(fillColor);
			canvas.drawFilledRect(0, 0, bounds.width, bounds.height, radius);
		} else if (startColor != null && endColor != null) {
			canvas.drawGradientRect(0, 0, bounds.width, bounds.height, radius, 
					angle, startColor, endColor);
		}
		
		if (strokeColor!=null && strokeWidth>0) {
			canvas.setColor(strokeColor);
			if (radius == 0) {
				canvas.drawRect(0, 0, bounds.width, bounds.height, strokeWidth);
			} else {
				canvas.drawRoundedRect(0, 0, bounds.width, bounds.height, radius, strokeWidth);
			}
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

	public Color getStartColor() {
		return startColor;
	}

	public void setStartColor(Color startColor) {
		this.startColor = startColor;
	}

	public Color getEndColor() {
		return endColor;
	}

	public void setEndColor(Color endColor) {
		this.endColor = endColor;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	
}
