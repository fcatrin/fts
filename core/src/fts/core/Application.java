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
import fts.views.View;
import fts.views.ViewGroup;

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
	
	protected View createView(Window w, Node node) {
		View view = null;
		
		String name = node.getNodeName();
		view = factory.createView(node);
		if (view == null) {
			String viewClassName = "fts.views." + name;
			view = (View)createComponentInstance(w, viewClassName);
		}
		
		if (view == null) {
			throw new RuntimeException("Cannot create view " + name);
		}
		
		NamedNodeMap attributes = node.getAttributes();
		for(int i=0; i<attributes.getLength(); i++) {
			Node item = attributes.item(i);
			view.setProperty(item.getNodeName(), item.getNodeValue());
		}
		
		if (view instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup)view;
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				if (childNode instanceof Element) {
					viewGroup.add(createView(w, childNode));
				}
			}
		}
		
		return view;
	}
			
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public static Component createComponentInstance(Window w, String className) {
		Class layoutClass;
		try {
			layoutClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
		
		try {
			return (Component)layoutClass.getDeclaredConstructor(Window.class).newInstance(w);
		} catch (InstantiationException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | SecurityException e) {
			return null;
		} catch (NoSuchMethodException e) {
			throw new RuntimeException("Missing constructor " + className +"(Window)");
		}
	}
	
	public View inflateView(Window w, String name) {
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
		return createView(w, root);
		
	}
}
