package fts.core;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Locale;

public abstract class Component {
	
	protected void setProperty(String name, String value) {
		Method m = resolveMethod("set", name);
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
	
	protected Method resolveMethod(String prefix, String name) {
		String methodName = prefix + name.substring(0, 0).toUpperCase(Locale.US) +name.substring(1);
		Method[] methods = getClass().getDeclaredMethods();
		for (Method method : methods ) {
			if (method.getName().equals(methodName)) return method;
		}
		
		throw new RuntimeException("method " + methodName + " not found in component " + getClass().getName());
	}
	
	protected abstract Object resolvePropertyValue(String propertyName, String value);
	
	protected int resolvePropertyValueDimen(String propertyName, String value) {
		if (value.endsWith("px")) {
			return Integer.parseInt(value.substring(0, value.length()-2));
		}
		if (value.endsWith("dp")) {
			return Context.instance.dp2px(Integer.parseInt(value.substring(0, value.length()-2)));
		}
		throw new RuntimeException("Invalid dimension " + value + " for property " + propertyName + " in component " + getClass().getName());
	}
	
}
