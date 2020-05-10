package fts.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

import fts.graphics.Dimension;

public abstract class Component {
	
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
		throw new RuntimeException("don't know how to handle " + propertyName + " in component " + getClass().getName());
	}
	
	protected int resolvePropertyValueDimen(String propertyName, String value) {
		try {
			return Dimension.parse(value);
		} catch (Exception e) {
			throw new RuntimeException("Invalid property " + getClass().getName() + "::" + propertyName +": " + e.getMessage());
		}
	}
	
}
