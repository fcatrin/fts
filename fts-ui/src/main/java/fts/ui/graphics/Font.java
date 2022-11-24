package fts.ui.graphics;

import org.w3c.dom.Element;

import fts.core.xml.SimpleXML;

public class Font {
	public String name;
	public int size;
	
	public Font(String name, int size) {
		this.name = name;
		this.size = size;
	}

	public Font(String name, String size) {
		this.name = name;
		this.size = Dimension.parse(size);
	}

	public static Font load(Element element) {
		String name = SimpleXML.getAttribute(element, "font");
		int    size = Dimension.parse(SimpleXML.getAttribute(element, "fontSize"));
		return new Font(name, size);
	}
}
