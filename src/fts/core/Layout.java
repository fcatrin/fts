package fts.core;

public abstract class Layout extends Component {
	public static final String VALUE_MATCH_PARENT = "match_parent";
	public static final String VALUE_WRAP_CONTENT = "wrap_parent";
	
	public final int MATCH_PARENT = -1;
	public final int WRAP_CONTENT = -2;
	
	int width;
	int height;
	@Override
	
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (propertyName.equals("width") || propertyName.equals("height")) {
			if (VALUE_MATCH_PARENT.equals(value)) return MATCH_PARENT;
			if (VALUE_WRAP_CONTENT.equals(value)) return WRAP_CONTENT;
			return resolvePropertyValueDimen(propertyName, value);
		}
		throw new RuntimeException("don't know how to handle " + propertyName + " in componente " + getClass().getName());
	}
	public int getWidth() {
		return width;
	}
	public void setWidth(int width) {
		this.width = width;
	}
	public int getHeight() {
		return height;
	}
	public void setHeight(int height) {
		this.height = height;
	}
	
	@Override
	public String toString() {
		return String.format("{class: %s, width: %i, height %i}", 
				getClass().getName(),
				width,
				height);
	}
}
