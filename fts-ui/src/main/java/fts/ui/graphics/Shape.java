package fts.ui.graphics;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;

public class Shape extends Drawable {
	
	ColorListSelector fillColor = null;
	ColorListSelector strokeColor = null;
	int strokeWidth = 4;
	int radius = 0;
	
	// gradient attributes
	ColorListSelector startColor = null;
	ColorListSelector endColor = null;
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
			canvas.setColor(fillColor.getSelectedItem());
			canvas.drawFilledRect(bounds.x, bounds.y, bounds.width, bounds.height, radius);
		} else if (startColor != null && endColor != null) {
			canvas.drawGradientRect(bounds.x, bounds.y, bounds.width, bounds.height, radius, 
					angle, startColor.getSelectedItem(), endColor.getSelectedItem());
		}
		
		if (strokeColor!=null && strokeWidth>0) {
			canvas.setColor(strokeColor.getSelectedItem());
			if (radius == 0) {
				canvas.drawRect(bounds.x, bounds.y, bounds.width, bounds.height, strokeWidth);
			} else {
				canvas.drawRoundedRect(bounds.x, bounds.y, bounds.width, bounds.height, radius, strokeWidth);
			}
		}
	}

	public ColorListSelector getFillColor() {
		return fillColor;
	}

	public void setFillColor(ColorListSelector fillColor) {
		this.fillColor = fillColor;
	}

	public ColorListSelector getStrokeColor() {
		return strokeColor;
	}

	public void setStrokeColor(ColorListSelector strokeColor) {
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

	public ColorListSelector getStartColor() {
		return startColor;
	}

	public void setStartColor(ColorListSelector startColor) {
		this.startColor = startColor;
	}

	public ColorListSelector getEndColor() {
		return endColor;
	}

	public void setEndColor(ColorListSelector endColor) {
		this.endColor = endColor;
	}

	public int getAngle() {
		return angle;
	}

	public void setAngle(int angle) {
		this.angle = angle;
	}

	
}
