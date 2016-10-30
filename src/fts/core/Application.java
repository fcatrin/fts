package fts.core;

import java.io.File;
import java.io.IOException;
import java.util.List;

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
	
	public Window createWindow() {
		return factory.createWindow();
	}
	
	protected Component createComponent(String name, Node node) {
		Component component = factory.createComponent(name);
		if (component == null) {
			String layoutClassName = "fts.layouts." + name;
			component = createComponentInstance(layoutClassName);
			if (component == null) {
				String viewClassName = "fts.views." + name;
				component = createComponentInstance(viewClassName);
			}
		}
		
		if (component == null) {
			throw new RuntimeException("Cannot create view " + name);
		}
		
		NamedNodeMap attributes = node.getAttributes();
		for(int i=0; i<attributes.getLength(); i++) {
			Node item = attributes.item(i);
			component.setProperty(item.getNodeName(), item.getNodeValue());
		}
		
		if (component instanceof ViewGroup) {
			ViewGroup viewGroup = (ViewGroup)component;
			NodeList childNodes = node.getChildNodes();
			for (int i = 0; i < childNodes.getLength(); i++) {
				Node childNode = childNodes.item(i);
				viewGroup.add(createComponent(childNode.getNodeName(), childNode));
			}
		}
		
		return component;
	}
			
	public static Component createComponentInstance(String className) {
		Class layoutClass;
		try {
			layoutClass = Class.forName(className);
		} catch (ClassNotFoundException e) {
			return null;
		}
		
		try {
			return (Component)layoutClass.newInstance();
		} catch (InstantiationException | IllegalAccessException e) {
			return null;
		}
	}
	
	public static View loadView(String name) {
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
		String viewName = root.getTagName();
		return createComponent(viewName, root);
		
	}
}
