package fts.core.xml;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import fts.core.Utils;

public abstract class SimpleXML {
	
	private SimpleXML() {}
	
	public static Document parse(InputStream is) throws ParserException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc = db.parse(is);
			doc.getDocumentElement().normalize();
			return doc;
		} catch (Exception e) {
			throw new ParserException("Error parsing XML", e);
		}
	}

	public static Document parse(File f) throws ParserException, IOException {
		FileInputStream fis = new FileInputStream(f);
		return parse(fis);
	}

	public static Document parse(byte b[]) throws ParserException {
		ByteArrayInputStream bais = new ByteArrayInputStream(b);
		return parse(bais);
	}

	public static List<Element> getElements(Element xml) {
		return getElements(xml, null);
	}
	
	public static List<Element> getElements(Element xml, String name) {
		List<Element> elements = new ArrayList<Element>();
		if (xml == null) return elements;
		
		NodeList childNodes = xml.getChildNodes();
		for (int i = 0; i < childNodes.getLength(); i++) {
			Node node = childNodes.item(i);
			if ((name == null || node.getNodeName().equals(name)) && node instanceof Element) {
				elements.add((Element) node);
			}
		}
		return elements;
	}
	
	public static Element getElement(Element xml, String name) {
		List<Element> elements = getElements(xml, name);
		if (elements.size()==0) return null;
		return elements.get(0);
	}

	public static String getText(Node xml) {
		return getElementValue(xml, null);
	}

	
	public static String getText(Element xml, String name) {
		return getText(xml, name, null);
	}

	public static String getText(Element xml, String name, String defaultValue) {
		List<Element> elements = getElements(xml, name);

		if (elements.size() > 0)
			return getElementValue(elements.get(0), defaultValue);
		return defaultValue;
	}
	
	private static String getElementValue(Node elem, String defaultValue) {
        Node child;
        if( elem != null && elem.hasChildNodes()){
            for( child = elem.getFirstChild(); child != null; child = child.getNextSibling() ){
                if( child.getNodeType() == Node.TEXT_NODE  ){
                    return child.getNodeValue();
                }
            }
        }
        return defaultValue;
 }    
	
	public static boolean getBoolean(Element xml, String name, boolean defaultValue) {
		String sValue = defaultValue?"true":"false";
		sValue = getText(xml, name, sValue);
		return sValue!=null && sValue.equals("true");
	}

	public static String getAttribute(Node node, String name) {
		return getAttribute(node, name, null);
	}
	
	public static String getAttribute(Node node, String name, String defaultValue) {
		if (node == null) return defaultValue;
		
		NamedNodeMap attributes = node.getAttributes();
		Node namedItem = attributes.getNamedItem(name);
		if (namedItem != null)
			return namedItem.getNodeValue();
		return defaultValue;
	}
	
	public static List<String> getAttributeNames(Node node) {
		List<String> list = new ArrayList<String>();
		
		NamedNodeMap attributes = node.getAttributes();
		for(int i=0; i<attributes.getLength(); i++) {
			Node item = attributes.item(i);
			list.add(item.getNodeName());
		}
		return list;
	}

	public static int getIntAttribute(Element xml, String name) {
		return getIntAttribute(xml, name, 0);
	}
	
	public static int getIntAttribute(Element xml, String name, int defaultValue) {
		if (xml == null) return defaultValue;
		
		NamedNodeMap attributes = xml.getAttributes();
		Node namedItem = attributes.getNamedItem(name);
		if (namedItem == null) return defaultValue;
		return Utils.str2i(namedItem.getNodeValue(), defaultValue);
	}

	public static float getFloatAttribute(Element xml, String name) {
		return getFloatAttribute(xml, name, 0);
	}

	public static float getFloatAttribute(Element xml, String name, float defaultValue) {
		if (xml == null) return defaultValue;
		
		NamedNodeMap attributes = xml.getAttributes();
		Node namedItem = attributes.getNamedItem(name);
		if (namedItem == null) return defaultValue;
		return Utils.str2f(namedItem.getNodeValue(), defaultValue);
	}

	public static boolean getBoolAttribute(Node node, String name) {
		return getBoolAttribute(node, name, false);
	}
	
	public static boolean getBoolAttribute(Node node, String name, boolean defaultValue) {
		if (node == null) return defaultValue;
		
		NamedNodeMap attributes = node.getAttributes();
		Node namedItem = attributes.getNamedItem(name);
		if (namedItem != null) {
			String sValue = namedItem.getNodeValue();
			return sValue!=null && sValue.equals("true");
		}
		return defaultValue;
	}
	

	public static int getInt(Element node, String name) {
		return getInt(node, name, 0);
	}

	public static int getInt(Element node, String name, int defaultValue) {
		String value = getText(node, name);
		return Utils.str2i(value, defaultValue);
	}

	public static int getHex(Element node, String name, int defaultValue) {
		String value = getText(node, name);
		return Utils.strHex2i(value, defaultValue);
	}

}
