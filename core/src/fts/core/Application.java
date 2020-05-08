package fts.core;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import fts.core.xml.ParserException;
import fts.core.xml.SimpleXML;

public class Application {
	static ComponentFactory factory;
	
	public Application(ComponentFactory factory) {
		Application.factory = factory;
	}
	
	public static Window createWindow() {
		return factory.createWindow();
	}
	
	public static NativeView createNativeView(Window w) {
		return factory.createNativeView(w);
	}
	
	protected Widget createWidget(Window w, Node node) {
		Widget widget = null;
		
		String name = node.getNodeName();
		widget = factory.createWidget(node);
		if (widget == null) {
			String viewClassName = "fts.widgets." + name;
			widget = (Widget)createComponentInstance(w, viewClassName);
		}
		
		if (widget == null) {
			throw new RuntimeException("Cannot create widget " + name);
		}
		
		NamedNodeMap attributes = node.getAttributes();
		for(int i=0; i<attributes.getLength(); i++) {
			Node item = attributes.item(i);
			widget.setProperty(item.getNodeName(), item.getNodeValue());
		}
		
		if (widget instanceof Container) {
			Container container = (Container)widget;
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				if (childNode instanceof Element) {
					container.add(createWidget(w, childNode));
				}
			}
		}
		
		return widget;
	}
			
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Component createComponentInstance(Window w, String className) {
		Class componentClass;
		try {
			componentClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
			return null;
		}
		
		try {
			return (Component)componentClass.getDeclaredConstructor(Window.class).newInstance(w);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			return null;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Missing constructor " + className +"(Window)");
		}
	}
	
	public Widget inflate(Window w, String name) {
		File file = new File("res/layout/" + name + ".xml"); // TODO replace by resource lookup
		if (!file.exists()) {
			throw new RuntimeException("File not found " + file.getAbsolutePath());
		}

		Document doc;
		try {
			doc = SimpleXML.parse(file);
		} catch (ParserException e) {
			throw new RuntimeException("Cannot parse XML " + file.getAbsolutePath(), e);
		} catch (IOException e) {
			throw new RuntimeException("Cannot parse XML " + file.getAbsolutePath(), e);
		}
		
		Element root = doc.getDocumentElement();
		return createWidget(w, root);
		
	}
}
