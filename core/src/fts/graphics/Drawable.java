package fts.graphics;

import org.w3c.dom.Element;

import fts.core.Component;

public abstract class Drawable extends Component {
	protected Rectangle padding = new Rectangle();
	protected Rectangle bounds = new Rectangle();
	
	public void setBounds(Rectangle bounds) {
		this.bounds = bounds.clone();
	}
	
	public void setPadding(Rectangle padding) {
		this.padding = padding.clone();
	}

	public abstract void load(Element element);
	public abstract void draw(Canvas canvas);
	public void destroy(){}

	@Override
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (propertyName.equals("fillColor")) {
			return resolveColor(value);
		}
		return super.resolvePropertyValue(propertyName, value);
	};

	private Color resolveColor(String value) {
		if (value.startsWith("#")) {
			Color color = Color.load(value);
			return color;
		}
		throw new RuntimeException("Invalid color " + value);
	}
}
