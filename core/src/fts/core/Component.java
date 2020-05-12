package fts.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashSet;
import java.util.Locale;
import java.util.Set;

import org.w3c.dom.Element;

import fts.graphics.Align;
import fts.graphics.Color;
import fts.graphics.Dimension;
import fts.graphics.Drawable;
import fts.graphics.Font;
import fts.graphics.Shape;

public abstract class Component {
	static Set<String> colorProperties     = new HashSet<String>();
	static Set<String> dimensionProperties = new HashSet<String>();
	static Set<String> intProperties = new HashSet<String>();
	
	static {
		colorProperties.add("color");
		colorProperties.add("fillColor");
		colorProperties.add("strokeColor");
		
		dimensionProperties.add("strokeWidth");
		dimensionProperties.add("radius");
		
		intProperties.add("maxLines");
	}
	
	protected void setProperty(String name, String value) {
		
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
			if (method.getName().equals(methodName)) return method;
		}
		
		Class superClass = _class.getSuperclass();
		if (superClass == null) {
			throw new RuntimeException("method " + methodName + " not found in component " + getClass().getName());
		} else {
			return resolveMethod(superClass, prefix, name);
		}
	}
	
	protected Object resolvePropertyValue(String propertyName, String value) {
		if (propertyName.equals("text")) {
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
		}
		throw new RuntimeException("don't know how to handle " + propertyName + " in component " + getClass().getName());
	}

	private Align resolveAlign(String propertyName, String spec) {
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
	
	private Drawable resolveBackground(String value) {
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

	private int resolvePropertyValueInt(String propertyName, String value) {
		try {
			return Integer.parseInt(value);
		} catch (Exception e) {
			throw new RuntimeException("Invalid property " + getClass().getName() + "::" + propertyName +": Invalid integer " + value);
		}
	}

	private Color resolvePropertyValueColor(String propertyName, String value) {
		if (value.startsWith("#")) {
			Color color = Color.load(value);
			return color;
		}
		throw new RuntimeException("Invalid property " + getClass().getName() + "::" + propertyName +": Invalid format " + value);
	}
	
	protected Font resolvePropertyValueFont(Element element) {
		return Font.load(element);
	}
	
	protected int resolvePropertyValueDimen(String propertyName, String value) {
		try {
			return Dimension.parse(value);
		} catch (Exception e) {
			throw new RuntimeException("Invalid property " + getClass().getName() + "::" + propertyName +": " + e.getMessage());
		}
	}
	
}
