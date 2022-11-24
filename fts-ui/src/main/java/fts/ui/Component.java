package fts.ui;

import org.w3c.dom.Element;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import fts.ui.graphics.Align;
import fts.ui.graphics.ColorListSelector;
import fts.ui.graphics.Dimension;
import fts.ui.graphics.Drawable;
import fts.ui.graphics.Font;
import fts.ui.graphics.Shape;

public abstract class Component {
	static Set<String> colorProperties     = new HashSet<String>();
	static Set<String> dimensionProperties = new HashSet<String>();
	static Set<String> intProperties = new HashSet<String>();
	
	static {
		colorProperties.add("color");
		colorProperties.add("fillColor");
		colorProperties.add("strokeColor");
		colorProperties.add("startColor");
		colorProperties.add("endColor");
		
		dimensionProperties.add("strokeWidth");
		dimensionProperties.add("radius");
		dimensionProperties.add("padding");
		dimensionProperties.add("paddingLeft");
		dimensionProperties.add("paddingRight");
		dimensionProperties.add("paddingTop");
		dimensionProperties.add("paddingBottom");
		dimensionProperties.add("margin");
		dimensionProperties.add("marginLeft");
		dimensionProperties.add("marginRight");
		dimensionProperties.add("marginTop");
		dimensionProperties.add("marginBottom");
		dimensionProperties.add("separator");
		
		intProperties.add("maxLines");
	}
	
	protected void setProperty(String name, String value) {
		if (value == null) return;
		
		Method m = resolveMethod(getClass(), "set", name);
		Object o = resolvePropertyValue(name, value);

		try {
			m.invoke(this, o);
		} catch (IllegalAccessException e) {
			throw new RuntimeException("cannot set " + name + "=" + value +" in component " + getClass().getName(), e);
		} catch (IllegalArgumentException e) {
			throw new RuntimeException("cannot set " + name + "=" + value +" in component " + getClass().getName(), e);
		} catch (InvocationTargetException e) {
			throw new RuntimeException("cannot set " + name + "=" + value +" in component " + getClass().getName(), e);
		}
	}
	
	@SuppressWarnings("rawtypes")
	protected Method resolveMethod(Class _class, String prefix, String name) {
		String methodName = prefix + name.substring(0, 1).toUpperCase(Locale.US) +name.substring(1);
		Method[] methods = _class.getDeclaredMethods();
		for (Method method : methods ) {
			if (method.getName().equals(methodName) && method.getParameterTypes().length == 1) return method;
		}
		
		Class superClass = _class.getSuperclass();
		if (superClass == null) {
			throw new RuntimeException("method " + methodName + " not found in component " + getClass().getName());
		} else {
			return resolveMethod(superClass, prefix, name);
		}
	}
	
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (value.startsWith("@string/")) {
			String alias = value.substring("@string/".length());
			value = Application.factory.getString(alias);
		}
		if (propertyName.equals("text") || propertyName.equals("id")) {
			return value;
		} else if (colorProperties.contains(propertyName)) {
				return resolvePropertyValueColor(propertyName, value);
		} else if (dimensionProperties.contains(propertyName)) {
			return resolvePropertyValueDimen(propertyName, value);
		} else if (intProperties.contains(propertyName)) {
			return resolvePropertyValueInt(propertyName, value);
		} else if (propertyName.equals("background")) {
			return resolveBackground(value);
		} else if (propertyName.equals("align")) {
			return resolveAlign(propertyName, value);
		} else if (propertyName.equals("containerAlign")) {
			return resolveAlign(propertyName, value);
		} else if (propertyName.equals("angle")) {
			return resolveAngle(propertyName, value);
		}
		throw new RuntimeException("don't know how to handle " + propertyName + " in component " + getClass().getName());
	}

	public Align resolveAlign(String propertyName, String spec) {
		Align align = new Align();
		
		String parts[] = spec.split("[|]");
		for(String part : parts) {
			if (part.equals("center")) {
				align.h = Align.HAlign.Center;
				align.v = Align.VAlign.Center;
			} else if (part.equals("center_horizontal")) {
				align.h = Align.HAlign.Center;
			} else if (part.equals("center_vertical")) {
				align.v = Align.VAlign.Center;
			} else if (part.equals("left")) {
				align.h = Align.HAlign.Left;
			} else if (part.equals("right")) {
				align.h = Align.HAlign.Right;
			} else if (part.equals("top")) {
				align.v = Align.VAlign.Top;
			} else if (part.equals("bottom")) {
				align.v = Align.VAlign.Bottom;
			} else {
				throw new RuntimeException("Invalid property " + getClass().getName() + "::" + propertyName +": Invalid alignment " + spec);
			}
		}
		return align;
	}

	public int resolveAngle(String propertyName, String value) {
		try {
			int angle = Integer.parseInt(value);
			if (angle >= 0 && angle < 360) return angle;
		} catch (Exception e) {}

		throw new RuntimeException("Invalid property " + getClass().getName() + "::" + propertyName +": Invalid angle " + value);
	}

	public Drawable resolveBackground(String value) {
		if (value.startsWith("@drawable/")) {
			String name = value.substring("@drawable/".length());
			return Application.loadDrawable(name);
		} else if (value.startsWith("@color") || value.startsWith("#")) {
			Shape shape = new Shape();
			shape.setProperty("fillColor", value);
			return shape;
		}
		throw new RuntimeException("don't know how to drawable " + value);
	}

	public int resolvePropertyValueInt(String propertyName, String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			throw new RuntimeException("Invalid property " + getClass().getName() + "::" + propertyName +": Invalid integer " + value);
		}
	}

	public ColorListSelector resolveColor(String value) {
		return resolvePropertyValueColor("color", value);
	}
	
	public ColorListSelector resolvePropertyValueColor(String propertyName, String value) {
		try {
			return Application.factory.getColor(value);
		} catch (RuntimeException e) {
			throw new RuntimeException("Invalid property " + getClass().getName() + "::" + propertyName +": Invalid format " + value);
		}
	}
	
	protected Font resolvePropertyValueFont(Element element) {
		return Font.load(element);
	}
	
	protected int resolvePropertyValueDimen(String propertyName, String value) {
		if (value.startsWith("@dimen/")) {
			String alias = value.substring("@dimen/".length());
			String knownDimen = Application.factory.getDimen(alias);
			if (knownDimen == null) {
				throw new RuntimeException("Unknown dimension " + value + " " + getClass().getName() + "::" + propertyName);
			}
			try {
				return Dimension.parse(knownDimen);
			} catch (Exception e) {
				throw new RuntimeException("Invalid dimension " + value + " as " + knownDimen + " for " + getClass().getName() + "::" + propertyName +": " + e.getMessage(), e);
			}
		}
		try {
			return Dimension.parse(value);
		} catch (Exception e) {
			throw new RuntimeException("Invalid property " + getClass().getName() + "::" + propertyName +": " + e.getMessage(), e);
		}
	}
	
	public int getDimen(String dimen) {
		return resolvePropertyValueDimen("auto", dimen);
	}
	
}
